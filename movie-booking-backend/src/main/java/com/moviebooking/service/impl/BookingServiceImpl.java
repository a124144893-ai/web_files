package com.moviebooking.service.impl;

import com.moviebooking.dto.request.BookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.dto.response.PageResponse;
import com.moviebooking.entity.*;
import com.moviebooking.exception.BusinessException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.security.JwtUtil;
import com.moviebooking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                            ShowtimeRepository showtimeRepository,
                            UserRepository userRepository,
                            JwtUtil jwtUtil) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for showtime: {}", request.getShowtimeId());
        
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("场次不存在"));
        
        // 检查座位是否充足
        Integer bookedSeats = bookingRepository.findTotalBookedSeats(request.getShowtimeId());
        bookedSeats = bookedSeats != null ? bookedSeats : 0;
        
        if (bookedSeats + request.getSeatCount() > showtime.getTotalSeats()) {
            throw new BusinessException("INSUFFICIENT_SEATS", "座位不足，可用座位数：" + (showtime.getTotalSeats() - bookedSeats));
        }
        
        Double totalPrice = showtime.getPrice() * request.getSeatCount();
        
        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .seatCount(request.getSeatCount())
                .seatNumbers(request.getSeatNumbers())
                .totalPrice(totalPrice)
                .bookingTime(java.time.LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // 更新可用座位数
        showtime.setAvailableSeats(showtime.getAvailableSeats() - request.getSeatCount());
        showtimeRepository.save(showtime);
        
        log.info("Booking created successfully, ID: {}", savedBooking.getId());
        return convertToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在，ID: " + id));
        return convertToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getCurrentUserBookings(Integer pageNum, Integer pageSize) {
        Long userId = getCurrentUserId();
        return getUserBookings(userId, pageNum, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getUserBookings(Long userId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Booking> page = bookingRepository.findByUserId(userId, pageable);
        
        List<BookingResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<BookingResponse>builder()
                .content(content)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在，ID: " + id));
        
        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BusinessException("BOOKING_ALREADY_CANCELED", "预约已取消");
        }
        
        booking.setStatus(BookingStatus.CANCELED);
        
        // 恢复可用座位数
        Showtime showtime = booking.getShowtime();
        showtime.setAvailableSeats(showtime.getAvailableSeats() + booking.getSeatCount());
        showtimeRepository.save(showtime);
        
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking canceled, ID: {}", id);
        
        return convertToResponse(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getShowtimeBookings(Long showtimeId) {
        List<Booking> bookings = bookingRepository.findActiveBookingsByShowtime(showtimeId);
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .movieId(booking.getShowtime().getMovie().getId())
                .movieTitle(booking.getShowtime().getMovie().getTitle())
                .showtimeStart(booking.getShowtime().getStartTime())
                .cinemaHall(booking.getShowtime().getCinemaHall())
                .seatCount(booking.getSeatCount())
                .seatNumbers(booking.getSeatNumbers())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().toString())
                .bookingTime(booking.getBookingTime())
                .build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return Long.parseLong(authentication.getName());
        }
        throw new BusinessException("UNAUTHORIZED", "未授权的操作");
    }
}

package com.moviebooking.service;

import com.moviebooking.dto.request.BookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);

    BookingResponse getBookingById(Long id);

    PageResponse<BookingResponse> getCurrentUserBookings(Integer pageNum, Integer pageSize);

    PageResponse<BookingResponse> getUserBookings(Long userId, Integer pageNum, Integer pageSize);

    BookingResponse cancelBooking(Long id);

    List<BookingResponse> getShowtimeBookings(Long showtimeId);
}

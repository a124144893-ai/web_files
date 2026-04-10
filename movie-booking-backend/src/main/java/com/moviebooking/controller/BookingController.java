package com.moviebooking.controller;

import com.moviebooking.dto.request.BookingRequest;
import com.moviebooking.dto.response.BookingResponse;
import com.moviebooking.dto.response.PageResponse;
import com.moviebooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "电影预约", description = "电影预约相关接口")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建预约", description = "创建新的电影预约")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取预约详情", description = "根据预约ID获取预约信息")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户的预约", description = "获取当前用户所有的电影预约记录（分页）")
    public ResponseEntity<PageResponse<BookingResponse>> getUserBookings(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(bookingService.getCurrentUserBookings(pageNum, pageSize));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "取消预约", description = "取消指定的电影预约")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/showtime/{showtimeId}")
    @Operation(summary = "获取场次的所有预约", description = "获取指定场次的所有有效预约记录")
    public ResponseEntity<List<BookingResponse>> getShowtimeBookings(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(bookingService.getShowtimeBookings(showtimeId));
    }
}

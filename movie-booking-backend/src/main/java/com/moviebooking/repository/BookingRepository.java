package com.moviebooking.repository;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    List<Booking> findByShowtimeId(Long showtimeId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId,
                                        @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status != 'CANCELED'")
    List<Booking> findActiveBookingsByShowtime(@Param("showtimeId") Long showtimeId);

    @Query("SELECT SUM(b.seatCount) FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status != 'CANCELED'")
    Integer findTotalBookedSeats(@Param("showtimeId") Long showtimeId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingTime >= :startTime AND b.bookingTime <= :endTime")
    Long countBookingsByDateRange(@Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);
}

package com.moviebooking.repository;

import com.moviebooking.entity.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieId(Long movieId);

    Page<Showtime> findByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :startTime")
    List<Showtime> findUpcomingShowtimes(@Param("movieId") Long movieId,
                                        @Param("startTime") LocalDateTime startTime);

    @Query("SELECT s FROM Showtime s WHERE s.startTime BETWEEN :startTime AND :endTime")
    List<Showtime> findShowtimesByDateRange(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM Showtime s WHERE s.availableSeats < s.totalSeats")
    List<Showtime> findPartiallyBookedShowtimes();
}

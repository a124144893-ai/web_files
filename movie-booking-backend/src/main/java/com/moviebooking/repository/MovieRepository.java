package com.moviebooking.repository;

import com.moviebooking.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByIsHotTrue();

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Movie> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE " +
           "(:genre IS NULL OR :genre MEMBER OF m.genres) AND " +
           "(:minRating IS NULL OR m.rating >= :minRating) AND " +
           "(:maxRating IS NULL OR m.rating <= :maxRating)")
    Page<Movie> findByFilters(@Param("genre") String genre,
                              @Param("minRating") Double minRating,
                              @Param("maxRating") Double maxRating,
                              Pageable pageable);

    List<Movie> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT m.genres FROM Movie m WHERE m.genres IS NOT NULL")
    List<String> findAllGenres();
}

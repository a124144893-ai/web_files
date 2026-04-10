package com.moviebooking.service.impl;

import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.dto.response.ShowtimeResponse;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Showtime;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("电影不存在，ID: " + id));
        return convertToResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getHotMovies() {
        List<Movie> movies = movieRepository.findByIsHotTrue();
        return movies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getAllMovies(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Movie> page = movieRepository.findAll(pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getShowtimesByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("电影不存在，ID: " + movieId));
        
        List<Showtime> showtimes = showtimeRepository.findByMovieId(movieId);
        return showtimes.stream()
                .map(showtime -> convertShowtimeToResponse(showtime, movie.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponse createMovie(MovieResponse movieResponse) {
        // 实现创建电影的逻辑
        log.info("Creating new movie: {}", movieResponse.getTitle());
        return movieResponse;
    }

    @Override
    public MovieResponse updateMovie(Long id, MovieResponse movieResponse) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("电影不存在，ID: " + id));
        log.info("Updating movie: {}", id);
        return convertToResponse(movie);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("电影不存在，ID: " + id));
        movieRepository.delete(movie);
        log.info("Deleted movie: {}", id);
    }

    private MovieResponse convertToResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .genres(movie.getGenres() == null ? List.of() : List.copyOf(movie.getGenres()))
                .director(movie.getDirector())
                .cast(movie.getCast())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .releaseDate(movie.getReleaseDate())
                .isHot(movie.getIsHot())
                .createdAt(movie.getCreatedAt())
                .build();
    }

    private ShowtimeResponse convertShowtimeToResponse(Showtime showtime, String movieTitle) {
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .movieTitle(movieTitle)
                .startTime(showtime.getStartTime())
                .availableSeats(showtime.getAvailableSeats())
                .totalSeats(showtime.getTotalSeats())
                .cinemaHall(showtime.getCinemaHall())
                .price(showtime.getPrice())
                .build();
    }
}

package com.moviebooking.service;

import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.dto.response.ShowtimeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {
    MovieResponse getMovieById(Long id);

    List<MovieResponse> getHotMovies();

    Page<MovieResponse> getAllMovies(Integer pageNum, Integer pageSize);

    List<ShowtimeResponse> getShowtimesByMovieId(Long movieId);

    MovieResponse createMovie(MovieResponse movieResponse);

    MovieResponse updateMovie(Long id, MovieResponse movieResponse);

    void deleteMovie(Long id);
}

package com.moviebooking.service;

import com.moviebooking.dto.request.SearchRequest;
import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {
    PageResponse<MovieResponse> searchMovies(SearchRequest request);

    List<String> getAllGenres();

    PageResponse<MovieResponse> searchByKeyword(String keyword, Integer pageNum, Integer pageSize);
}

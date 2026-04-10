package com.moviebooking.service.impl;

import com.moviebooking.dto.request.SearchRequest;
import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.dto.response.PageResponse;
import com.moviebooking.entity.Movie;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class SearchServiceImpl implements SearchService {

    private final MovieRepository movieRepository;

    @Autowired
    public SearchServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> searchMovies(SearchRequest request) {
        log.info("Searching movies with keyword: {}, genre: {}", request.getKeyword(), request.getGenre());
        
        Pageable pageable = createPageable(request.getPageNum(), request.getPageSize(), request.getSortBy());
        
        Page<Movie> page;
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            page = movieRepository.searchByKeyword(request.getKeyword(), pageable);
        } else {
            Double minRating = request.getMinRating() != null ? request.getMinRating().doubleValue() : null;
            Double maxRating = request.getMaxRating() != null ? request.getMaxRating().doubleValue() : null;
            page = movieRepository.findByFilters(request.getGenre(), minRating, maxRating, pageable);
        }
        
        return convertPageToResponse(page, request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllGenres() {
        return movieRepository.findAllGenres();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> searchByKeyword(String keyword, Integer pageNum, Integer pageSize) {
        log.info("Searching movies by keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Movie> page = movieRepository.searchByKeyword(keyword, pageable);
        
        return convertPageToResponse(page, pageNum, pageSize);
    }

    private Pageable createPageable(Integer pageNum, Integer pageSize, String sortBy) {
        Sort.Direction direction = Sort.Direction.DESC;
        String property = "releaseDate";
        
        if ("rating".equals(sortBy)) {
            property = "rating";
        } else if ("title".equals(sortBy)) {
            property = "title";
            direction = Sort.Direction.ASC;
        }
        
        return PageRequest.of(pageNum - 1, pageSize, Sort.by(direction, property));
    }

    private PageResponse<MovieResponse> convertPageToResponse(Page<Movie> page, Integer pageNum, Integer pageSize) {
        List<MovieResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<MovieResponse>builder()
                .content(content)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private MovieResponse convertToResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .genres(movie.getGenres())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .releaseDate(movie.getReleaseDate())
                .isHot(movie.getIsHot())
                .createdAt(movie.getCreatedAt())
                .build();
    }
}

package com.moviebooking.controller;

import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.dto.response.ShowtimeResponse;
import com.moviebooking.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@Tag(name = "电影管理", description = "电影相关接口")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门电影", description = "获取所有标记为热门的电影列表")
    public ResponseEntity<List<MovieResponse>> getHotMovies() {
        return ResponseEntity.ok(movieService.getHotMovies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电影详情", description = "根据电影ID获取电影详细信息")
    @Parameter(name = "id", description = "电影ID", required = true)
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping
    @Operation(summary = "获取所有电影", description = "分页获取所有电影")
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(movieService.getAllMovies(pageNum, pageSize));
    }

    @GetMapping("/{movieId}/showtimes")
    @Operation(summary = "获取电影的所有场次", description = "根据电影ID获取所有放映场次")
    public ResponseEntity<List<ShowtimeResponse>> getShowtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getShowtimesByMovieId(movieId));
    }
}

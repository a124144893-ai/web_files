package com.moviebooking.controller;

import com.moviebooking.dto.request.SearchRequest;
import com.moviebooking.dto.response.MovieResponse;
import com.moviebooking.dto.response.PageResponse;
import com.moviebooking.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@Tag(name = "电影搜索", description = "电影搜索相关接口")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    @Operation(summary = "搜索电影", description = "根据关键词和筛选条件搜索电影")
    public ResponseEntity<PageResponse<MovieResponse>> searchMovies(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(searchService.searchMovies(request));
    }

    @GetMapping("/genres")
    @Operation(summary = "获取所有电影类型", description = "获取系统中所有存在的电影类型")
    public ResponseEntity<List<String>> getAllGenres() {
        return ResponseEntity.ok(searchService.getAllGenres());
    }

    @GetMapping("/keyword")
    @Operation(summary = "按关键词搜索", description = "使用关键词搜索电影（分页）")
    public ResponseEntity<PageResponse<MovieResponse>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(searchService.searchByKeyword(keyword, pageNum, pageSize));
    }
}

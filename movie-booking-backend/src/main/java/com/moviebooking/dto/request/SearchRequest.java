package com.moviebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchRequest {
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;

    private String genre;

    private Integer minRating;

    private Integer maxRating;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String sortBy = "releaseDate"; // releaseDate, rating, title
}

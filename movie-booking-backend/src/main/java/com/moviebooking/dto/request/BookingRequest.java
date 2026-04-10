package com.moviebooking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "场次ID不能为空")
    @Positive(message = "场次ID必须为正整数")
    private Long showtimeId;

    @NotNull(message = "座位数不能为空")
    @Positive(message = "座位数必须大于0")
    private Integer seatCount;

    private String seatNumbers; // 座位号，如"A1,A2,A3"
}

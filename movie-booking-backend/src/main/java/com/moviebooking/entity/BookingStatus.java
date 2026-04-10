package com.moviebooking.entity;

public enum BookingStatus {
    PENDING("待支付"),
    CONFIRMED("已确认"),
    CANCELED("已取消"),
    COMPLETED("已完成");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

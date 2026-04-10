package com.moviebooking.entity;

public enum Role {
    USER("普通用户"),
    ADMIN("管理员");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

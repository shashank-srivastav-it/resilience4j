package com.backend.resilience4jdemo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Response<T> {
    private LocalDateTime timestamp;
    private T message;

    public Response(T message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
}
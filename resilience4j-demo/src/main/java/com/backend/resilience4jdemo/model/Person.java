package com.backend.resilience4jdemo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    private String name;
    private String email;
}
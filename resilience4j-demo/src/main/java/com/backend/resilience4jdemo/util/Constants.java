package com.backend.resilience4jdemo.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String DEFAULT = "default";
    public static final String RETRY_SAMPLE_API = "retry-sample-api";
    public static final String CIRCUIT_BREAKER_SLOW_API = "circuit-breaker-slow-api";
    public static final String CIRCUIT_BREAKER = "circuit-breaker";
    public static final String RATE_LIMITER_SAMPLE_API = "rate-limiter-sample-api";
    public static final String BULKHEAD_SAMPLE_API = "bulkhead-sample-api";
    public static final String TIME_LIMITER_SAMPLE_API = "time-limiter-sample-api";
}

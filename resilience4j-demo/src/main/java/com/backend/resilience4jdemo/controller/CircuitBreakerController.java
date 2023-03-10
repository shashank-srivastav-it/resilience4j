package com.backend.resilience4jdemo.controller;

import com.backend.resilience4jdemo.model.Person;
import com.backend.resilience4jdemo.util.Constants;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/sample-api")
public class CircuitBreakerController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${service.dummy.url}")
    private String dummyEndpoint;
    @Value("${service.text.delay.url}")
    private String sendDelayTextEndpoint;
    @Value("${service.text.url}")
    private String sendTextEndpoint;
    @Value("${service.send.person.url}")
    private String sendPersonEndpoint;
    private String cache;


    //@Retry(name = Constants.DEFAULT)
    @GetMapping("/retry")
    @Retry(name = Constants.RETRY_SAMPLE_API, fallbackMethod = "handler")
    public String retrySampleApi() {
        log.info("Retry sample api call received");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Person person = Person.builder().name("ab").email("test").build();
        HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(sendPersonEndpoint, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }

    @GetMapping("/circuit-breaker-1")
    @CircuitBreaker(name = Constants.CIRCUIT_BREAKER_1, fallbackMethod = "handler")
    public String circuitBreakerSampleApi1() {
        log.info("Circuit Breaker-1 sample api call received");
        ResponseEntity<String> forEntity = restTemplate.getForEntity(dummyEndpoint, String.class);
        return forEntity.getBody();
    }

    @GetMapping("/circuit-breaker-2")
    @CircuitBreaker(name = Constants.CIRCUIT_BREAKER_2, fallbackMethod = "circuitBreakerHandler")
    public ResponseEntity<String> circuitBreakerSampleApi2() {
        log.info("Circuit Breaker-2 sample api call received");
        ResponseEntity<String> forEntity = restTemplate.getForEntity(sendDelayTextEndpoint, String.class);
        cache = forEntity.getBody();
        return forEntity;
    }

    @GetMapping("/retry-with-cb")
    @CircuitBreaker(name = Constants.CIRCUIT_BREAKER_1, fallbackMethod = "handler")
    @Retry(name = Constants.RETRY_SAMPLE_API)
    public String retryWithCircuitBreakerSampleApi() {
        log.info("Retry sample api call received");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Person person = Person.builder().name("ab").email("test").build();
        HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(sendPersonEndpoint, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }

    @GetMapping("/rate-limiter")
    @RateLimiter(name = Constants.RATE_LIMITER_SAMPLE_API, fallbackMethod = "rateLimiterHandler")
    public ResponseEntity<String> rateLimiterSampleApi() {
        log.info("Sample api call received");
        return restTemplate.getForEntity(sendTextEndpoint, String.class);
    }

    @GetMapping("/bulkhead")
    @Bulkhead(name = Constants.BULKHEAD_SAMPLE_API, fallbackMethod = "bulkheadHandler")
    public ResponseEntity<String> bulkheadSampleApi() {
        log.info("Sample api call received");
        return restTemplate.getForEntity(sendTextEndpoint, String.class);
    }

    @GetMapping("/time-limiter")
    @TimeLimiter(name = Constants.TIME_LIMITER_SAMPLE_API, fallbackMethod = "timeLimiterHandler")
    @Bulkhead(name = Constants.DEFAULT, fallbackMethod = "timeLimiterHandler", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> timeLimiterSampleApi() {
        long startTime = System.currentTimeMillis();
        String result = restTemplate.getForEntity(sendDelayTextEndpoint, String.class).getBody();
        long responseTime = System.currentTimeMillis() - startTime;
        cache = result; // update cache
        log.info("Sample api call received response time: {}", responseTime);
        return CompletableFuture.completedFuture(result);
    }

    public CompletableFuture<String> timeLimiterHandler(Throwable t) {
        log.warn("sampleApi fallback triggered: {}", t.getMessage());
        //returning data from cache
        return CompletableFuture.completedFuture(cache);
    }

    public ResponseEntity<String> bulkheadHandler(Throwable t) {
        log.warn(t.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", "10s"); // set custom header
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(headers)
                .body("Too many concurrent requests - Please try after some time");
    }

    public ResponseEntity<String> rateLimiterHandler(Exception ex) {
        log.warn(ex.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", "1s"); // set custom header
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(headers)
                .body("Too many request - No further calls are accepted");
    }

    public ResponseEntity<String> circuitBreakerHandler(Throwable t) {
        log.error(t.getMessage());
        //return data from cache
        return ResponseEntity.ok().body(cache);
    }

    public String handler(Exception ex) {
        log.error(ex.getMessage());
        return "fallback-response";
    }
}

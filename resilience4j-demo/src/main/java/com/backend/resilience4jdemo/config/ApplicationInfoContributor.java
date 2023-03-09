package com.backend.resilience4jdemo.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicationInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> map = new HashMap<>();
        map.put("App Name", "Resilience4jDemo");
        map.put("App Description", "Resilience4j Demo Web Application for fault tolerance.");
        map.put("App Version", "1.0.0");
        map.put("Documentation Link", "https://resilience4j.readme.io/docs/getting-started");
        map.put("Swagger Link", "http://localhost:8080/swagger-ui/index.html");
        builder.withDetail("resilience4j-demo-info", map);
    }
}

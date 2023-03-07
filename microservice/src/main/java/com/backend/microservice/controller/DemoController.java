package com.backend.microservice.controller;

import com.backend.microservice.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/send")
public class DemoController {
    public static final String CALL_NO = "Service call no ";
    private int count;

    @GetMapping("/text")
    public ResponseEntity<?> sendMessage(@RequestParam(value = "name", defaultValue = "default message") String name) {
        log.info(CALL_NO + count++);
        return ResponseEntity.ok().body("Demo of " + name);
    }

    @GetMapping("/text-with-delay")
    public ResponseEntity<?> sendMessageWithDelay(@RequestParam(value = "name", defaultValue = "default message") String name) throws InterruptedException {
        log.info(CALL_NO + count++);
        return getDelay(name);
    }

    @PostMapping("/person")
    public ResponseEntity<?> showPerson(@RequestBody @Valid Person person) {
        log.info(CALL_NO + count++);
        return ResponseEntity.ok().body(person);
    }

    private ResponseEntity<?> getDelay(String name) throws InterruptedException {
        int i = new Random().nextInt(2);
        if (i == 0) {
            log.warn("Call taking 2sec");
            Thread.sleep(2000);
        }
        return ResponseEntity.ok().body("Demo of " + name);
    }
}

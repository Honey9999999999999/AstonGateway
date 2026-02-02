package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/user-data")
    public Mono<String> userFallback() {
        return Mono.just("{\"error\": \"User Service временно недоступен. Попробуйте позже.\"}");
    }
}
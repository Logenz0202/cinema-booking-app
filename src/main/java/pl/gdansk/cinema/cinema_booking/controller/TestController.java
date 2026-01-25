package pl.gdansk.cinema.cinema_booking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Projekt działa.";
    }

    @GetMapping("/api/v1/whoami")
    public String whoami(Authentication authentication) {
        if (authentication == null) return "anonymous";
        return authentication.getName();
    }
}

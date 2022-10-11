package ru.taksebe.telegram.premium.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/premium/test")
    public String getTestMessage() {
        return "I believe I can fly";
    }
}
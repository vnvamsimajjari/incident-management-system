package com.vamsi.incident_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 👉 Default route
    @GetMapping("/")
    public String home() {
        return "redirect:/pages/login.html";
    }
}
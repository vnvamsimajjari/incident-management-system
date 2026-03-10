package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String testEmail() {

        emailService.sendEmail(
                "incident.alerts.system@gmail.com",
                "Test Email",
                "Email module working successfully"
        );

        return "Email Sent Successfully";
    }
}
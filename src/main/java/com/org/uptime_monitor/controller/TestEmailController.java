package com.org.uptime_monitor.controller;

import com.org.uptime_monitor.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api2")
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String testEmail() {

        emailService.sendAlertMail(
                "manassingh449@gmail.com",
                "Uptime Monitor Test",
                "Email sending is working!"
        );

        return "Email sent";
    }
}

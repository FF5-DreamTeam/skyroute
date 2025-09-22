package com.skyroute.skyroute.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@ConditionalOnBean(EmailService.class)
public class EmailController {

    @Autowired(required = false)
    private EmailService emailService;

    @PostMapping("/test-registration")
    public ResponseEntity<String> testRegistrationEmail(@RequestParam String email) {
        if (emailService == null) {
            return ResponseEntity.badRequest().body("Email service not available");
        }

        try {
            emailService.sendRegistrationConfirmation(email, "Test", "User");
            return ResponseEntity.ok("Registration email sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/test-booking")
    public ResponseEntity<String> testBookingEmail(@RequestParam String email) {
        if (emailService == null) {
            return ResponseEntity.badRequest().body("Email service not available");
        }

        try {
            emailService.sendBookingConfirmation(
                    email,
                    "Test",
                    "User",
                    "BKG123456",
                    "SR001",
                    "2024-12-25 08:00",
                    "2024-12-25 11:30");
            return ResponseEntity.ok("Booking email sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }
}

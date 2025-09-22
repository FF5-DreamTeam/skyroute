package com.skyroute.skyroute.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
    }

    @Test
    void sendRegistrationEmail_shouldSendEmailSuccessfully() throws MessagingException {
        String to = "test@example.com";
        String subject = "Welcome to SkyRoute";
        String plainText = "Welcome message";
        String htmlContent = "<html><body>Welcome</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendRegistrationEmail(to, subject, plainText, htmlContent);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendBookingEmail_shouldSendEmailSuccessfully() throws MessagingException {
        String to = "test@example.com";
        String subject = "Booking Confirmation";
        String plainText = "Booking details";
        String htmlContent = "<html><body>Booking</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendBookingEmail(to, subject, plainText, htmlContent);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendRegistrationEmail_shouldCallMailSenderMethods() throws MessagingException {
        String to = "test@example.com";
        String subject = "Welcome to SkyRoute";
        String plainText = "Welcome message";
        String htmlContent = "<html><body>Welcome</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendRegistrationEmail(to, subject, plainText, htmlContent);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendBookingEmail_shouldCallMailSenderMethods() throws MessagingException {
        String to = "test@example.com";
        String subject = "Booking Confirmation";
        String plainText = "Booking details";
        String htmlContent = "<html><body>Booking</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendBookingEmail(to, subject, plainText, htmlContent);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
}

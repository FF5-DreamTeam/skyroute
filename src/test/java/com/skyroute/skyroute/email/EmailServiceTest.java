package com.skyroute.skyroute.email;

import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.entity.Route;
import com.skyroute.skyroute.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private Booking mockBooking;

    @Mock
    private User mockUser;

    @Mock
    private Flight mockFlight;

    @Mock
    private Route mockRoute;

    @Mock
    private Airport mockOriginAirport;

    @Mock
    private Airport mockDestinationAirport;

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

    @Test
    void sendBookingConfirmationEmail_shouldSendEmailSuccessfully() throws MessagingException {
        setupMockBooking();
        setupMockUser();
        setupMockFlight();
        setupMockRoute();
        setupMockAirports();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendBookingConfirmationEmail(mockBooking, mockUser, mockFlight);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendBookingConfirmationEmail_shouldCallMailSenderMethods() throws MessagingException {
        setupMockBooking();
        setupMockUser();
        setupMockFlight();
        setupMockRoute();
        setupMockAirports();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendBookingConfirmationEmail(mockBooking, mockUser, mockFlight);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendBookingConfirmationEmail_shouldNotThrowException_whenMessagingFails() {
        setupMockBooking();
        setupMockUser();
        setupMockFlight();
        setupMockRoute();
        setupMockAirports();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server unavailable"))
                .when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendBookingConfirmationEmail(mockBooking, mockUser, mockFlight));

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    private void setupMockBooking() {
        when(mockBooking.getBookingNumber()).thenReturn("SR-ABC123");
        when(mockBooking.getTotalPrice()).thenReturn(299.99);
    }

    private void setupMockUser() {
        when(mockUser.getFirstName()).thenReturn("John");
        when(mockUser.getLastName()).thenReturn("Doe");
        when(mockUser.getEmail()).thenReturn("john.doe@example.com");
    }

    private void setupMockFlight() {
        when(mockFlight.getFlightNumber()).thenReturn("SR001");
        when(mockFlight.getDepartureTime()).thenReturn(LocalDateTime.of(2024, 1, 15, 10, 0));
        when(mockFlight.getArrivalTime()).thenReturn(LocalDateTime.of(2024, 1, 15, 14, 0));
        when(mockFlight.getRoute()).thenReturn(mockRoute);
    }

    private void setupMockRoute() {
        when(mockRoute.getOrigin()).thenReturn(mockOriginAirport);
        when(mockRoute.getDestination()).thenReturn(mockDestinationAirport);
    }

    private void setupMockAirports() {
        when(mockOriginAirport.getCity()).thenReturn("New York");
        when(mockDestinationAirport.getCity()).thenReturn("Los Angeles");
    }
}

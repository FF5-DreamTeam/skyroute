package com.skyroute.skyroute.email;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

        private final JavaMailSender mailSender;
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        public void sendRegistrationEmail(String to, String subject, String plainText, String htmlContent)
                        throws MessagingException {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(plainText, htmlContent);

                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
                helper.addInline("logo", logoResource);

                mailSender.send(message);
        }

        public void sendBookingEmail(String to, String subject, String plainText, String htmlContent)
                        throws MessagingException {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(plainText, htmlContent);

                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
                helper.addInline("logo", logoResource);

                mailSender.send(message);
        }

        public void sendPasswordResetEmail(String to, String subject, String plainText, String htmlContent)
                        throws MessagingException {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(plainText, htmlContent);

                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
                helper.addInline("logo", logoResource);

                mailSender.send(message);
        }

        public void sendBookingConfirmationEmail(Booking booking, User user, Flight flight) {
                try {
                        log.info("Sending booking confirmation email for booking {} to user {}",
                                        booking.getBookingNumber(), user.getEmail());

                        String departureTime = flight.getDepartureTime().format(DATE_TIME_FORMATTER);
                        String arrivalTime = flight.getArrivalTime().format(DATE_TIME_FORMATTER);

                        // Get cities from route
                        String departureCity = flight.getRoute().getOrigin().getCity();
                        String arrivalCity = flight.getRoute().getDestination().getCity();

                        String plainText = BookingEmailTemplates.getPlainText(
                                        user.getFirstName(),
                                        user.getLastName(),
                                        booking.getBookingNumber(),
                                        flight.getFlightNumber(),
                                        departureTime,
                                        arrivalTime,
                                        departureCity,
                                        arrivalCity,
                                        booking.getTotalPrice());

                        String htmlContent = BookingEmailTemplates.getHtml(
                                        user.getFirstName(),
                                        user.getLastName(),
                                        booking.getBookingNumber(),
                                        flight.getFlightNumber(),
                                        departureTime,
                                        arrivalTime,
                                        departureCity,
                                        arrivalCity,
                                        booking.getTotalPrice());

                        sendBookingEmail(
                                        user.getEmail(),
                                        BookingEmailTemplates.getSubject(),
                                        plainText,
                                        htmlContent);

                        log.info("Booking confirmation email sent successfully to {}", user.getEmail());
                } catch (Exception e) {
                        log.error("Failed to send booking confirmation email to {}: {}",
                                        user.getEmail(), e.getMessage(), e);
                }
        }
}
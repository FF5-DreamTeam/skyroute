package com.skyroute.skyroute.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(JavaMailSender.class)
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendRegistrationConfirmation(String to, String firstName, String lastName) {
        if (mailSender == null) {
            System.out.println("Email service not available - registration confirmation not sent to " + to);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to SkyRoute - Registration Confirmed");
        message.setText(String.format(
                "Dear %s %s,\n\n" +
                        "Welcome to SkyRoute! Your account has been successfully created.\n\n" +
                        "You can now book flights and manage your travel plans with us.\n\n" +
                        "Thank you for choosing SkyRoute!\n\n" +
                        "Best regards,\n" +
                        "SkyRoute Team",
                firstName, lastName));

        mailSender.send(message);
    }

    public void sendBookingConfirmation(String to, String firstName, String lastName,
            String bookingNumber, String flightNumber,
            String departureTime, String arrivalTime) {
        if (mailSender == null) {
            System.out.println("Email service not available - booking confirmation not sent to " + to);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SkyRoute - Booking Confirmation");
        message.setText(String.format(
                "Dear %s %s,\n\n" +
                        "Your flight booking has been confirmed!\n\n" +
                        "Booking Details:\n" +
                        "Booking Number: %s\n" +
                        "Flight Number: %s\n" +
                        "Departure: %s\n" +
                        "Arrival: %s\n\n" +
                        "Please arrive at the airport at least 2 hours before departure.\n\n" +
                        "Thank you for choosing SkyRoute!\n\n" +
                        "Best regards,\n" +
                        "SkyRoute Team",
                firstName, lastName, bookingNumber, flightNumber, departureTime, arrivalTime));

        mailSender.send(message);
    }

    public void sendBookingCancellation(String to, String firstName, String lastName,
            String bookingNumber, String flightNumber) {
        if (mailSender == null) {
            System.out.println("Email service not available - booking cancellation not sent to " + to);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SkyRoute - Booking Cancelled");
        message.setText(String.format(
                "Dear %s %s,\n\n" +
                        "Your flight booking has been cancelled.\n\n" +
                        "Cancelled Booking Details:\n" +
                        "Booking Number: %s\n" +
                        "Flight Number: %s\n\n" +
                        "If you have any questions, please contact our customer service.\n\n" +
                        "Thank you for choosing SkyRoute!\n\n" +
                        "Best regards,\n" +
                        "SkyRoute Team",
                firstName, lastName, bookingNumber, flightNumber));

        mailSender.send(message);
    }
}

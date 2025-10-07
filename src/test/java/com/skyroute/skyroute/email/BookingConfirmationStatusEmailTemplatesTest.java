package com.skyroute.skyroute.email;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class BookingConfirmationStatusEmailTemplatesTest {

    @Test
    void getSubject_shouldReturnCorrectSubject() {
        String subject = BookingConfirmationStatusEmailTemplates.getSubject();

        assertEquals("SkyRoute - Booking Confirmed! 🎉", subject);
    }

    @Test
    void getPlainText_shouldReturnFormattedPlainText() {
        String firstName = "John";
        String lastName = "Doe";
        String bookingNumber = "BK123456";
        String flightNumber = "SR001";
        String departureTime = "2024-01-15 10:00";
        String arrivalTime = "2024-01-15 14:00";
        String departureCity = "New York";
        String arrivalCity = "Los Angeles";
        Double totalPrice = 299.99;

        String plainText = BookingConfirmationStatusEmailTemplates.getPlainText(
                firstName, lastName, bookingNumber, flightNumber,
                departureTime, arrivalTime, departureCity, arrivalCity, totalPrice);

        assertNotNull(plainText);
        assertTrue(plainText.contains("Dear John Doe,"));
        assertTrue(plainText.contains("Great news! Your booking has been confirmed!"));
        assertTrue(plainText.contains("BK123456"));
        assertTrue(plainText.contains("SR001"));
        assertTrue(plainText.contains("New York → Los Angeles"));
        assertTrue(plainText.contains("299.99"));
        assertTrue(plainText.contains("Your payment has been processed successfully"));
        assertTrue(plainText.contains("http://localhost:3000/login/"));
    }

    @Test
    void getPlainText_shouldHandleEmptyNames() {
        String firstName = "";
        String lastName = "";
        String bookingNumber = "BK123456";
        String flightNumber = "SR001";
        String departureTime = "2024-01-15 10:00";
        String arrivalTime = "2024-01-15 14:00";
        String departureCity = "New York";
        String arrivalCity = "Los Angeles";
        Double totalPrice = 299.99;

        String plainText = BookingConfirmationStatusEmailTemplates.getPlainText(
                firstName, lastName, bookingNumber, flightNumber,
                departureTime, arrivalTime, departureCity, arrivalCity, totalPrice);

        assertNotNull(plainText);
        assertTrue(plainText.contains("Dear  ,"));
    }

    @Test
    void getHtml_shouldReturnFormattedHtml() {
        String firstName = "Jane";
        String lastName = "Smith";
        String bookingNumber = "BK789012";
        String flightNumber = "SR002";
        String departureTime = "2024-01-20 08:30";
        String arrivalTime = "2024-01-20 12:30";
        String departureCity = "Chicago";
        String arrivalCity = "Miami";
        Double totalPrice = 450.50;

        String html = BookingConfirmationStatusEmailTemplates.getHtml(
                firstName, lastName, bookingNumber, flightNumber,
                departureTime, arrivalTime, departureCity, arrivalCity, totalPrice);

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("Dear Jane Smith,"));
        assertTrue(html.contains("Great news! Your booking has been"));
        assertTrue(html.contains("confirmed"));
        assertTrue(html.contains("BK789012"));
        assertTrue(html.contains("SR002"));
        assertTrue(html.contains("Chicago → Miami"));
        assertTrue(html.contains("450.50"));
        assertTrue(html.contains("✓ CONFIRMED"));
        assertTrue(html.contains("View Your Bookings"));
        assertTrue(html.contains("http://localhost:3000/login/"));
        assertTrue(html.contains("Have a great flight!"));
    }

    @Test
    void getHtml_shouldHandleEmptyNames() {
        String firstName = "";
        String lastName = "";
        String bookingNumber = "BK123456";
        String flightNumber = "SR001";
        String departureTime = "2024-01-15 10:00";
        String arrivalTime = "2024-01-15 14:00";
        String departureCity = "New York";
        String arrivalCity = "Los Angeles";
        Double totalPrice = 299.99;

        String html = BookingConfirmationStatusEmailTemplates.getHtml(
                firstName, lastName, bookingNumber, flightNumber,
                departureTime, arrivalTime, departureCity, arrivalCity, totalPrice);

        assertNotNull(html);
        assertTrue(html.contains("Dear  ,"));
    }
}

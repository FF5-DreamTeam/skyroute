package com.skyroute.skyroute.email;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class RegistrationEmailTemplatesTest {

    @Test
    void getSubject_shouldReturnCorrectSubject() {
        String subject = RegistrationEmailTemplates.getSubject();

        assertEquals("Welcome to SkyRoute - Registration Confirmed", subject);
    }

    @Test
    void getPlainText_shouldReturnFormattedPlainText() {
        String firstName = "John";
        String lastName = "Doe";

        String plainText = RegistrationEmailTemplates.getPlainText(firstName, lastName);

        assertNotNull(plainText);
        assertTrue(plainText.contains("Dear John Doe,"));
        assertTrue(plainText.contains("Welcome to SkyRoute!"));
        assertTrue(plainText.contains("http://localhost:3000/login/"));
    }

    @Test
    void getPlainText_shouldHandleEmptyNames() {
        String firstName = "";
        String lastName = "";

        String plainText = RegistrationEmailTemplates.getPlainText(firstName, lastName);

        assertNotNull(plainText);
        assertTrue(plainText.contains("Dear  ,"));
    }

    @Test
    void getHtml_shouldReturnFormattedHtml() {
        String firstName = "Jane";
        String lastName = "Smith";

        String html = RegistrationEmailTemplates.getHtml(firstName, lastName);

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("Dear Jane Smith,"));
        assertTrue(html.contains("Welcome to SkyRoute!"));
        assertTrue(html.contains("Go to Your Account"));
        assertTrue(html.contains("http://localhost:3000/login/"));
    }

    @Test
    void getHtml_shouldHandleEmptyNames() {
        String firstName = "";
        String lastName = "";

        String html = RegistrationEmailTemplates.getHtml(firstName, lastName);

        assertNotNull(html);
        assertTrue(html.contains("Dear  ,"));
    }
}
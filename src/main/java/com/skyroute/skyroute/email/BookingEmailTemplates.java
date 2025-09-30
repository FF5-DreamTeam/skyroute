package com.skyroute.skyroute.email;

import java.util.Locale;

public class BookingEmailTemplates {

    public static String getSubject() {
        return "SkyRoute - Booking Confirmation";
    }

    public static String getPlainText(String firstName, String lastName, String bookingNumber,
            String flightNumber, String departureTime, String arrivalTime, String departureCity,
            String arrivalCity, Double totalPrice) {
        return String.format(Locale.US, "Dear %s %s,\n\n" +
                "Your flight booking has been created!\n\n" +
                "Booking Details:\n" +
                "Booking Number: %s\n" +
                "Flight Number: %s\n" +
                "Route: %s → %s\n" +
                "Departure: %s\n" +
                "Arrival: %s\n" +
                "Total Price: $%.2f\n\n" +
                "Please arrive at the airport at least 2 hours before departure.\n\n" +
                "View your bookings: http://localhost:3000/login/\n\n" +
                "Thank you for choosing SkyRoute!\n\n" +
                "Best regards,\n" +
                "SkyRoute Team", firstName, lastName, bookingNumber, flightNumber, departureCity, arrivalCity,
                departureTime, arrivalTime, totalPrice);
    }

    public static String getHtml(String firstName, String lastName, String bookingNumber,
            String flightNumber, String departureTime, String arrivalTime, String departureCity,
            String arrivalCity, Double totalPrice) {
        return String.format(Locale.US,
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8" />
                            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                            <title>Booking Confirmation - SkyRoute</title>
                        </head>
                        <body
                            style="
                                margin: 0;
                                padding: 0;
                                font-family: Arial, sans-serif;
                                background-color: #2c2c2c;
                                color: #ffffff;
                            "
                        >
                            <div style="max-width: 600px; margin: 0 auto; background-color: #2c2c2c">
                                <!-- Header -->
                                <div style="background-color: #4a9b9b; padding: 20px; text-align: center">
                                    <img
                                        src="cid:logo"
                                        alt="SkyRoute Logo"
                                        style="max-height: 60px; margin-bottom: 40px"
                                    />
                                    <h1 style="margin: 0; color: #ffffff; font-size: 24px">
                                        Booking Confirmation
                                    </h1>
                                </div>

                                <!-- Content -->
                                <div style="padding: 30px 20px">
                                    <h2 style="color: #ffffff; margin-top: 0">
                                        Dear %s %s,
                                    </h2>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        Your flight booking has been created!
                                    </p>

                                    <div
                                        style="
                                            background-color: #3a3a3a;
                                            padding: 20px;
                                            border-radius: 5px;
                                            margin: 20px 0;
                                        "
                                    >
                                        <h3 style="color: #ffffff; margin-top: 0">Booking Details:</h3>
                                        <p style="margin: 5px 0; font-size: 16px; color: #ffffff">
                                            <strong>Booking Number:</strong> %s
                                        </p>
                                        <p style="margin: 5px 0; font-size: 16px; color: #ffffff">
                                            <strong>Flight Number:</strong> %s
                                        </p>
                                        <p style="margin: 5px 0; font-size: 16px; color: #ffffff">
                                            <strong>Route:</strong> %s → %s
                                        </p>
                                        <p style="margin: 5px 0; font-size: 16px; color: #ffffff">
                                            <strong>Departure:</strong> %s
                                        </p>
                                        <p style="margin: 5px 0; font-size: 16px; color: #ffffff">
                                            <strong>Arrival:</strong> %s
                                        </p>
                                        <p style="margin: 5px 0; font-size: 16px; color: #ffffff">
                                            <strong>Total Price:</strong> $%.2f
                                        </p>
                                    </div>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        Please arrive at the airport at least 2 hours before departure.
                                    </p>

                                    <div style="text-align: center; margin: 30px 0">
                                        <a
                                            href="http://localhost:3000/login/"
                                            style="
                                                background-color: #f7a34f;
                                                color: #ffffff !important;
                                                padding: 12px 30px;
                                                text-decoration: none;
                                                border-radius: 5px;
                                                font-weight: bold;
                                                display: inline-block;
                                                text-decoration: none !important;
                                            "
                                        >
                                            <span style="color: #ffffff !important; text-decoration: none !important;">View Your Bookings</span>
                                        </a>
                                    </div>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        Thank you for choosing SkyRoute!
                                    </p>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        Best regards,<br />
                                        SkyRoute Team
                                    </p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                firstName, lastName, bookingNumber, flightNumber, departureCity, arrivalCity,
                departureTime, arrivalTime, totalPrice);
    }
}

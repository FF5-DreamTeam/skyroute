package com.skyroute.skyroute.email;

import java.util.Locale;

public class BookingCancellationEmailTemplates {

    public static String getSubject() {
        return "SkyRoute - Booking Cancelled";
    }

    public static String getPlainText(String firstName, String lastName, String bookingNumber,
            String flightNumber, String departureTime, String arrivalTime, String departureCity,
            String arrivalCity, Double totalPrice) {
        return String.format(Locale.US, "Dear %s %s,\n\n" +
                "Your booking has been cancelled.\n\n" +
                "Cancelled Booking Details:\n" +
                "Booking Number: %s\n" +
                "Flight Number: %s\n" +
                "Route: %s → %s\n" +
                "Departure: %s\n" +
                "Arrival: %s\n" +
                "Total Price: $%.2f\n\n" +
                "If you paid for this booking, a refund will be processed within 5-7 business days.\n\n" +
                "If you have any questions, please contact our customer service.\n\n" +
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
                            <title>Booking Cancelled - SkyRoute</title>
                        </head>
                        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;">
                            <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color: #f5f5f5; padding: 40px 0;">
                                <tr>
                                    <td align="center">
                                        <!-- Main Container -->
                                        <table width="600" cellpadding="0" cellspacing="0" border="0" style="background: linear-gradient(135deg, #4a9b9b 0%%, #6dbaba 100%%); border-radius: 15px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border: 2px solid #4a9b9b;">
                                            <!-- Header -->
                                            <tr>
                                                <td align="center" style="padding: 40px 20px 30px 20px;">
                                                    <img src="cid:logo" alt="SkyRoute Logo" style="max-height: 60px; margin-bottom: 20px;" />
                                                    <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: bold;">
                                                        Booking Cancelled
                                                    </h1>
                                                </td>
                                            </tr>

                                            <!-- Content -->
                                            <tr>
                                                <td style="background-color: #ffffff; padding: 40px 30px;">
                                                    <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                                        <tr>
                                                            <td>
                                                                <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 22px;">
                                                                    Dear %s %s,
                                                                </h2>

                                                                <p style="font-size: 16px; line-height: 1.6; color: #555555; margin: 0 0 25px 0;">
                                                                    Your booking has been <strong style="color: #d32f2f;">cancelled</strong>. Below are the details of the cancelled booking:
                                                                </p>

                                                                <!-- Cancelled Badge -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 25px;">
                                                                    <tr>
                                                                        <td align="center" style="padding: 15px; background: linear-gradient(135deg, #d32f2f 0%%, #f44336 100%%); border-radius: 8px;">
                                                                            <p style="margin: 0; color: #ffffff; font-size: 18px; font-weight: bold;">
                                                                                ✕ CANCELLED
                                                                            </p>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <!-- Booking Details Table -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color: #f8f9fa; border: 2px solid #d32f2f; border-radius: 10px; overflow: hidden; margin-bottom: 25px;">
                                                                    <tr>
                                                                        <td colspan="2" style="background-color: #d32f2f; padding: 15px; text-align: center;">
                                                                            <h3 style="margin: 0; color: #ffffff; font-size: 18px;">Cancelled Booking Details</h3>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; width: 40%%; background-color: #ffffff;">
                                                                            <strong style="color: #d32f2f;">Booking Number:</strong>
                                                                        </td>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #ffffff;">
                                                                            <span style="color: #333333; font-weight: bold; font-size: 16px;">%s</span>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #f8f9fa;">
                                                                            <strong style="color: #d32f2f;">Flight Number:</strong>
                                                                        </td>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #f8f9fa;">
                                                                            <span style="color: #333333;">%s</span>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #ffffff;">
                                                                            <strong style="color: #d32f2f;">Route:</strong>
                                                                        </td>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #ffffff;">
                                                                            <span style="color: #333333;">%s → %s</span>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #f8f9fa;">
                                                                            <strong style="color: #d32f2f;">Departure:</strong>
                                                                        </td>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #f8f9fa;">
                                                                            <span style="color: #333333;">🛫 %s</span>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #ffffff;">
                                                                            <strong style="color: #d32f2f;">Arrival:</strong>
                                                                        </td>
                                                                        <td style="padding: 12px 20px; border-bottom: 1px solid #e0e0e0; background-color: #ffffff;">
                                                                            <span style="color: #333333;">🛬 %s</span>
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td style="padding: 12px 20px; background-color: #f8f9fa;">
                                                                            <strong style="color: #d32f2f;">Total Price:</strong>
                                                                        </td>
                                                                        <td style="padding: 12px 20px; background-color: #f8f9fa;">
                                                                            <span style="color: #666666; font-weight: bold; font-size: 18px; text-decoration: line-through;">$%.2f</span>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <!-- Refund Info Box -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 25px;">
                                                                    <tr>
                                                                        <td style="background-color: #fff9e6; border-left: 4px solid #f7a34f; padding: 15px; border-radius: 5px;">
                                                                            <p style="margin: 0 0 10px 0; font-size: 14px; line-height: 1.5; color: #555555;">
                                                                                💰 <strong>Refund Information:</strong>
                                                                            </p>
                                                                            <p style="margin: 0; font-size: 14px; line-height: 1.5; color: #666666;">
                                                                                If you paid for this booking, a refund will be processed within 5-7 business days to your original payment method.
                                                                            </p>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <!-- Support Info Box -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 25px;">
                                                                    <tr>
                                                                        <td style="background-color: #e8f5f5; border-left: 4px solid #4a9b9b; padding: 15px; border-radius: 5px;">
                                                                            <p style="margin: 0; font-size: 14px; line-height: 1.5; color: #555555;">
                                                                                💬 <strong>Need Help?</strong> If you have any questions about this cancellation, please contact our customer service team.
                                                                            </p>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <!-- Button -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                                                    <tr>
                                                                        <td align="center" style="padding: 20px 0;">
                                                                            <table cellpadding="0" cellspacing="0" border="0">
                                                                                <tr>
                                                                                    <td align="center" style="background-color: #f7a34f; border-radius: 8px; box-shadow: 0 2px 4px rgba(247,163,79,0.3);">
                                                                                        <a href="http://localhost:3000/login/" style="display: inline-block; padding: 14px 35px; font-size: 16px; color: #ffffff; text-decoration: none; font-weight: bold;">
                                                                                            View Your Bookings
                                                                                        </a>
                                                                                    </td>
                                                                                </tr>
                                                                            </table>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <p style="font-size: 16px; line-height: 1.6; color: #555555; margin: 20px 0 0 0; text-align: center;">
                                                                    We hope to serve you again soon!
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>

                                            <!-- Footer -->
                                            <tr>
                                                <td align="center" style="background-color: #4a9b9b; padding: 25px 20px;">
                                                    <p style="margin: 0; color: #ffffff; font-size: 14px; line-height: 1.6;">
                                                        Best regards,<br />
                                                        <strong>SkyRoute Team</strong>
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </body>
                        </html>
                        """,
                firstName, lastName, bookingNumber, flightNumber, departureCity, arrivalCity,
                departureTime, arrivalTime, totalPrice);
    }
}

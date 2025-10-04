package com.skyroute.skyroute.email;

public class PasswordResetEmailTemplates {

    public static String getSubject() {
        return "SkyRoute - Password Reset Request";
    }

    public static String getPlainText(String firstName, String lastName, String resetToken) {
        return String.format("Dear %s %s,\n\n" +
                "You have requested to reset your password for your SkyRoute account.\n\n" +
                "To reset your password, please click on the following link:\n" +
                "http://localhost:3000/reset-password?token=%s\n\n" +
                "This link will expire in 1 hour for security reasons.\n\n" +
                "If you did not request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "SkyRoute Team", firstName, lastName, resetToken);
    }

    public static String getHtml(String firstName, String lastName, String resetToken) {
        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8" />
                            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                            <title>Password Reset - SkyRoute</title>
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
                                                        Password Reset Request
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

                                                                <p style="font-size: 16px; line-height: 1.6; color: #555555; margin: 0 0 15px 0;">
                                                                    You have requested to reset your password for your SkyRoute account.
                                                                </p>

                                                                <p style="font-size: 16px; line-height: 1.6; color: #555555; margin: 0 0 30px 0;">
                                                                    To reset your password, please click on the button below:
                                                                </p>

                                                                <!-- Button -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                                                    <tr>
                                                                        <td align="center" style="padding: 20px 0;">
                                                                            <table cellpadding="0" cellspacing="0" border="0">
                                                                                <tr>
                                                                                    <td align="center" style="background-color: #f7a34f; border-radius: 8px; box-shadow: 0 2px 4px rgba(247,163,79,0.3);">
                                                                                        <a href="http://localhost:3000/reset-password?token=%s" style="display: inline-block; padding: 14px 35px; font-size: 16px; color: #ffffff; text-decoration: none; font-weight: bold;">
                                                                                            Reset Password
                                                                                        </a>
                                                                                    </td>
                                                                                </tr>
                                                                            </table>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <!-- Warning Box -->
                                                                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="margin-top: 25px;">
                                                                    <tr>
                                                                        <td style="background-color: #fff4e6; border-left: 4px solid #f7a34f; padding: 15px; border-radius: 5px;">
                                                                            <p style="margin: 0; font-size: 14px; line-height: 1.5; color: #666666;">
                                                                                ⏱️ <strong>Important:</strong> This link will expire in 1 hour for security reasons.
                                                                            </p>
                                                                        </td>
                                                                    </tr>
                                                                </table>

                                                                <p style="font-size: 14px; line-height: 1.6; color: #888888; margin: 20px 0 0 0;">
                                                                    If you did not request this password reset, please ignore this email and your password will remain unchanged.
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
                firstName, lastName, resetToken);
    }
}

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
                                        Password Reset Request
                                    </h1>
                                </div>

                                <!-- Content -->
                                <div style="padding: 30px 20px">
                                    <h2 style="color: #ffffff; margin-top: 0">
                                        Dear %s %s,
                                    </h2>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        You have requested to reset your password for your SkyRoute account.
                                    </p>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        To reset your password, please click on the button below:
                                    </p>

                                    <div style="text-align: center; margin: 30px 0">
                                        <a href="http://localhost:3000/reset-password?token=%s"
                                           style="
                                               display: inline-block;
                                               background-color: #4a9b9b;
                                               color: #ffffff;
                                               padding: 15px 30px;
                                               text-decoration: none;
                                               border-radius: 5px;
                                               font-weight: bold;
                                               font-size: 16px;
                                           ">
                                            Reset Password
                                        </a>
                                    </div>

                                    <p style="font-size: 14px; line-height: 1.6; color: #cccccc">
                                        This link will expire in 1 hour for security reasons.
                                    </p>

                                    <p style="font-size: 14px; line-height: 1.6; color: #cccccc">
                                        If you did not request this password reset, please ignore this email.
                                    </p>
                                </div>

                                <!-- Footer -->
                                <div style="background-color: #1a1a1a; padding: 20px; text-align: center">
                                    <p style="margin: 0; color: #cccccc; font-size: 14px">
                                        Best regards,<br />
                                        SkyRoute Team
                                    </p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """, firstName, lastName, resetToken);
    }
}

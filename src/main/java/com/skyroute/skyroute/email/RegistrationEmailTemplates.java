package com.skyroute.skyroute.email;

public class RegistrationEmailTemplates {

    public static String getSubject() {
        return "Welcome to SkyRoute - Registration Confirmed";
    }

    public static String getPlainText(String firstName, String lastName) {
        return String.format("Dear %s %s,\n\n" +
                "Welcome to SkyRoute! Your account has been successfully created.\n\n" +
                "You can now book flights and manage your travel plans with us.\n\n" +
                "Go to your account: http://localhost:3000/login/\n\n" +
                "Thank you for choosing SkyRoute!\n\n" +
                "Best regards,\n" +
                "SkyRoute Team", firstName, lastName);
    }

    public static String getHtml(String firstName, String lastName) {
        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8" />
                            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                            <title>Welcome to SkyRoute</title>
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
                                        Welcome to SkyRoute
                                    </h1>
                                </div>

                                <!-- Content -->
                                <div style="padding: 30px 20px">
                                    <h2 style="color: #ffffff; margin-top: 0">
                                        Dear %s %s,
                                    </h2>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        Welcome to SkyRoute! Your account has been successfully created.
                                    </p>

                                    <p style="font-size: 16px; line-height: 1.6; color: #ffffff">
                                        You can now book flights and manage your travel plans with us.
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
                                            <span style="color: #ffffff !important; text-decoration: none !important;">Go to Your Account</span>
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
                firstName, lastName);
    }
}

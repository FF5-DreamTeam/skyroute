package com.skyroute.skyroute.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

        private final JavaMailSender mailSender;

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
}
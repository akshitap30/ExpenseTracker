package com.project1.ExpenseTracker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(
            String to,
            String username) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true);

            // IMPORTANT
            helper.setFrom(
                    "expense@demomailtrap.co");

            helper.setTo(to);

            helper.setSubject(
                    "Welcome to Expense Tracker");

            helper.setText(
                    "Hello " + username + ",\n\n"
                            + "Welcome to Expense Tracker!\n\n"
                            + "Your account has been successfully created.\n\n"
                            + "Thank you for using Expense Tracker."
            );

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send welcome email",
                    e);
        }
    }
}
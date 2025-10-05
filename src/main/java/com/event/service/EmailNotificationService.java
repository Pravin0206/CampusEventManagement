package com.event.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.event.entity.Registration;

@Service
public final class EmailNotificationService {

    private final JavaMailSender mailSender;

    // Use constructor injection instead of @Autowired
    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // New method to send a registration confirmation email
    public void sendRegistrationConfirmationHtml(Registration registration) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(registration.getUser().getEmail());
        helper.setSubject("✅ Registration Confirmed!");

        String eventDateTime = registration.getEvent().getDate() + " " + registration.getEvent().getTime();

        String htmlBody = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #4CAF50;'>Registration Confirmed!</h2>"
                + "<p>Hello <strong>" + registration.getUser().getUsername() + "</strong>,</p>"

                + "<h3>📌 Registration Details</h3>"
                + "<table style='border-collapse: collapse; width: 100%;'>"
                + "<tr><td><b>Name</b></td><td>" + registration.getUser().getUsername() + "</td></tr>"
                + "<tr><td><b>Email</b></td><td>" + registration.getUser().getEmail() + "</td></tr>"
                + "<tr><td><b>Phone</b></td><td>" + registration.getUser().getPhone() + "</td></tr>"
                + "<tr><td><b>Bio</b></td><td>" + registration.getUser().getBio() + "</td></tr>"
                + "<tr><td><b>Registration Time</b></td><td>" + registration.getRegistrationDate() + "</td></tr>"
                + "</table><br>"

                + "<h3>📌 Event Details</h3>"
                + "<table style='border-collapse: collapse; width: 100%;'>"
                + "<tr><td><b>Title</b></td><td>" + registration.getEvent().getTitle() + "</td></tr>"
                + "<tr><td><b>Date & Time</b></td><td>" + eventDateTime + "</td></tr>"
                + "</table><br>"

                + "<p style='margin-top: 20px;'>Thank you for registering!<br>"
                + "<strong>Event Management System</strong></p>"

                + "</body></html>";

        helper.setText(htmlBody, true); // enable HTML
        mailSender.send(mimeMessage);
    }

    // Send a plain text email
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // Send an HTML email
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true indicates HTML

        mailSender.send(mimeMessage);
    }
}
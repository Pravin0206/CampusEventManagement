package com.event.controller;

import com.event.entity.Attendance;
import com.event.entity.Registration;
import com.event.entity.User;
import com.event.service.AttendanceService;
import com.event.service.EmailNotificationService;
import com.event.service.RegistrationService;
import com.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.mail.MessagingException;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/student/attendance")
public class StudentAttendanceController {

    private final AttendanceService attendanceService;
    private final RegistrationService registrationService;
    private final UserService userService;
    private final EmailNotificationService emailNotificationService;

    public StudentAttendanceController(AttendanceService attendanceService,
                                       RegistrationService registrationService,
                                       UserService userService,
                                       EmailNotificationService emailNotificationService) {
        this.attendanceService = attendanceService;
        this.registrationService = registrationService;
        this.userService = userService;
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping
    public String viewAttendances(Model model, Principal principal) {
        User student = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Registration> registrations = registrationService.getRegistrationsByUser(student);
        model.addAttribute("registrations", registrations);

        return "student-registrations";
    }

    @PostMapping("/mark/{registrationId}")
    public String markAttendance(@PathVariable Long registrationId, Principal principal,
                                 RedirectAttributes redirectAttributes) {

        Registration registration = registrationService.getRegistrationById(registrationId);

        if (!registration.getUser().getUsername().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized action.");
            return "redirect:/student/registrations";
        }

        // Mark attendance (status = PENDING)
        Attendance attendance = attendanceService.markAttendance(registrationId); // Make sure this method returns Attendance

        // Prepare student email
        String studentEmail = registration.getUser().getEmail();
        String subject = "Attendance Marked for " + registration.getEvent().getTitle();

        String htmlBody = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #673AB7;'>Attendance Marked</h2>"
                + "<p>Hello " + registration.getUser().getUsername() + ",</p>"
                + "<p>Your attendance for the event <strong>" + registration.getEvent().getTitle() + "</strong> has been marked.</p>"
                + "<table style='border-collapse: collapse; width: 100%; max-width: 500px;'>"
                + "<tr style='background-color: #f2f2f2;'><td style='padding: 8px;'>Event:</td><td style='padding: 8px;'>" + registration.getEvent().getTitle() + "</td></tr>"
                + "<tr><td style='padding: 8px;'>Date:</td><td style='padding: 8px;'>" + registration.getEvent().getDate() + "</td></tr>"
                + "<tr style='background-color: #f2f2f2;'><td style='padding: 8px;'>Time:</td><td style='padding: 8px;'>" + registration.getEvent().getTime() + "</td></tr>"
                + "<tr><td style='padding: 8px;'>Location:</td><td style='padding: 8px;'>" + registration.getEvent().getLocation() + "</td></tr>"
                + "<tr style='background-color: #f2f2f2;'><td style='padding: 8px;'>Status:</td><td style='padding: 8px;'>"
                + "<span style='display:inline-block; padding: 4px 8px; border-radius: 4px; color: white; background-color: orange;'>" 
                + attendance.getStatus() + "</span></td></tr>"
                + "</table>"
                + "<p>You will be notified once the admin approves your attendance.</p>"
                + "<br><p>Thank you,<br>Event Management Team</p>"
                + "</body></html>";

        try {
            emailNotificationService.sendHtmlEmail(studentEmail, subject, htmlBody);
        } catch (MessagingException e) {
            e.printStackTrace(); // Optional: log error
        }

        redirectAttributes.addFlashAttribute("attendanceMessage", "Attendance marked successfully! Awaiting admin approval.");
        return "redirect:/student/registrations";
    }
}
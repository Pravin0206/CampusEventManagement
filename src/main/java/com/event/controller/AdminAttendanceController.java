package com.event.controller;

import com.event.entity.User;
import com.event.entity.Attendance;
import com.event.service.AttendanceService;
import com.event.service.EmailNotificationService;
import com.event.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.mail.MessagingException;
import java.security.Principal;

@Controller
@RequestMapping("/admin/attendance")
public class AdminAttendanceController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;

    public AdminAttendanceController(AttendanceService attendanceService,
                                     UserRepository userRepository,
                                     EmailNotificationService emailNotificationService) {
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping("/pending")
    public String viewPendingAttendance(Model model) {
        model.addAttribute("pendingAttendances", attendanceService.findPendingAttendances());
        return "view-pending-attendance";
    }

    private User getAuthenticatedUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    @PostMapping("/approve/{attendanceId}")
    public String approveAttendance(@PathVariable Long attendanceId, Principal principal,
                                    RedirectAttributes redirectAttributes) throws MessagingException {

        User admin = getAuthenticatedUser(principal);

        Attendance attendance = attendanceService.getAttendanceById(attendanceId);
        attendanceService.approveAttendance(attendanceId, admin);

        // Send email to student
        String studentEmail = attendance.getRegistration().getUser().getEmail();
        String subject = "Attendance Approved for " + attendance.getRegistration().getEvent().getTitle();

        String htmlBody = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #673AB7;'>Attendance Approved</h2>"
                + "<p>Hello " + attendance.getRegistration().getUser().getUsername() + ",</p>"
                + "<p>Your attendance for the event <strong>" 
                + attendance.getRegistration().getEvent().getTitle() 
                + "</strong> has been approved by admin.</p>"
                + "<p>Status: <span style='color: green; font-weight: bold;'>APPROVED</span></p>"
                + "</body></html>";

        emailNotificationService.sendHtmlEmail(studentEmail, subject, htmlBody);

        redirectAttributes.addFlashAttribute("message", "Attendance approved and email sent to student!");
        return "redirect:/admin/attendance/pending";
    }

    @PostMapping("/reject/{attendanceId}")
    public String rejectAttendance(@PathVariable Long attendanceId, Principal principal,
                                   RedirectAttributes redirectAttributes) throws MessagingException {

        User admin = getAuthenticatedUser(principal);

        Attendance attendance = attendanceService.getAttendanceById(attendanceId);
        attendanceService.rejectAttendance(attendanceId, admin);

        // Send email to student
        String studentEmail = attendance.getRegistration().getUser().getEmail();
        String subject = "Attendance Rejected for " + attendance.getRegistration().getEvent().getTitle();

        String htmlBody = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #673AB7;'>Attendance Rejected</h2>"
                + "<p>Hello " + attendance.getRegistration().getUser().getUsername() + ",</p>"
                + "<p>Your attendance for the event <strong>" 
                + attendance.getRegistration().getEvent().getTitle() 
                + "</strong> has been rejected by admin.</p>"
                + "<p>Status: <span style='color: red; font-weight: bold;'>REJECTED</span></p>"
                + "</body></html>";

        emailNotificationService.sendHtmlEmail(studentEmail, subject, htmlBody);

        redirectAttributes.addFlashAttribute("message", "Attendance rejected and email sent to student!");
        return "redirect:/admin/attendance/pending";
    }

    @PostMapping("/mark-past-events")
    public String markPastEvents() {
        attendanceService.markPastEventsAutomatically();
        return "redirect:/admin/attendance/pending";
    }
}
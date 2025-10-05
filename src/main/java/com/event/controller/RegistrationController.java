package com.event.controller;

import com.event.entity.Registration;
import com.event.entity.User;
import com.event.entity.Event;
import com.event.service.RegistrationService;
import com.event.service.UserService;
import com.event.service.EventService;
import com.event.service.EmailNotificationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final EventService eventService;
    private final EmailNotificationService emailService;

    // Use Constructor Injection - Recommended Best Practice
    public RegistrationController(RegistrationService registrationService,
                                  UserService userService,
                                  EventService eventService,
                                  EmailNotificationService emailService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.eventService = eventService;
        this.emailService = emailService;
    }

    // View all registrations
    @GetMapping
    public String myRegistrations(Model model, Principal principal) {
        User student = userService.getUserByUsername(principal.getName())
                                  .orElseThrow(() -> new RuntimeException("User not found."));

        List<Registration> registrations = registrationService.getRegistrationsByUser(student);
        model.addAttribute("registrations", registrations);
        return "student-registrations";
    }

    // Register for an event
    @PostMapping("/register/{id}")
    public String registerForEvent(@PathVariable("id") Long eventId, Principal principal,
                                   RedirectAttributes redirectAttributes) {

        try {
            User user = userService.getUserByUsername(principal.getName())
                                   .orElseThrow(() -> new RuntimeException("User not found."));
            Event event = eventService.getEventById(eventId)
                                      .orElseThrow(() -> new RuntimeException("Event not found."));

            // 1. ATTEMPT DATABASE REGISTRATION FIRST
            registrationService.registerUserForEvent(user, event);

            // 2. SEPARATE TRY-CATCH FOR NON-CRITICAL EMAIL LOGIC
            try {
                String studentSubject = "Successful Registration: " + event.getTitle();
                String studentBody = "<h3>Registration Confirmed!</h3>"
                        + "<p>Hello " + user.getName() + ",</p>"
                        + "<p>You are successfully registered for the event: <b>" + event.getTitle() + "</b>.</p>";
                emailService.sendHtmlEmail(user.getEmail(), studentSubject, studentBody);

                // Add success message for both DB and email
                redirectAttributes.addFlashAttribute("successMessage", "You are successfully registered. A confirmation email has been sent!");
            } catch (Exception emailEx) {
                // Log the email error but don't roll back the transaction
                System.err.println("WARNING: Registration succeeded, but failed to send confirmation email. Error: " + emailEx.getMessage());
                // Add a slightly different message to inform the user
                redirectAttributes.addFlashAttribute("successMessage", "Registration succeeded, but failed to send email.");
            }

        } catch (RuntimeException e) {
            // This only catches exceptions from the critical database registration
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Guarantees the redirect, regardless of email status
        return "redirect:/student/registrations";
    }

    // Cancel a registration
    @PostMapping("/delete/{id}")
    public String deleteRegistration(@PathVariable("id") Long registrationId,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {

        try {
            User user = userService.getUserByUsername(principal.getName())
                                   .orElseThrow(() -> new RuntimeException("User not found."));
            
            Registration registration = registrationService.getRegistrationById(registrationId);
            
            if (!registration.getUser().equals(user)) {
                throw new RuntimeException("You can only cancel your own registrations.");
            }
            
            // Delete the registration
            registrationService.cancelRegistration(registrationId);

            // Send email to student
            String studentSubject = "Registration Cancelled: " + registration.getEvent().getTitle();
            String studentBody = "<h3>Registration Cancelled</h3>"
                    + "<p>Hello " + user.getName() + ",</p>"
                    + "<p>Your registration for <b>" + registration.getEvent().getTitle() + "</b> has been cancelled.</p>";
            emailService.sendHtmlEmail(user.getEmail(), studentSubject, studentBody);
            
            redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled successfully!");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            System.err.println("WARNING: Failed to send cancellation email. " + e.getMessage());
            redirectAttributes.addFlashAttribute("successMessage", "Cancellation succeeded, but failed to send email.");
        }

        // Redirects to the same page to show the updated list
        return "redirect:/student/registrations";
    }
}
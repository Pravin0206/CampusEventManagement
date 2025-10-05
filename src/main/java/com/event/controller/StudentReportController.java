package com.event.controller;

import com.event.entity.User;
import com.event.repository.EventRepository;
import com.event.repository.RegistrationRepository;
import com.event.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentReportController {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public StudentReportController(EventRepository eventRepository,
                                   RegistrationRepository registrationRepository,
                                   UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/reports")
    public String studentReports(Model model, Authentication authentication) {
        // Get current logged-in student's username
        String username = authentication.getName();

        // Fetch student entity
        User student = userRepository.findByUsername(username).orElseThrow();

        // Event statistics
        long totalEvents = eventRepository.count();
        long upcomingEvents = eventRepository.countUpcomingEvents();
        long pastEvents = eventRepository.countPastEvents();

        // Registration statistics for this student
        long totalRegistrations = registrationRepository.countByUser_Username(username);

        // Add attributes to model
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("pastEvents", pastEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("studentName", username);

        return "student-reports"; // Thymeleaf template
    }
}
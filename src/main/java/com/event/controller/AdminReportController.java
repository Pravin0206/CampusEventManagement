package com.event.controller;

import com.event.repository.EventRepository;
import com.event.repository.UserRepository;
import com.event.repository.RegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminReportController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public AdminReportController(EventRepository eventRepository,
                                 UserRepository userRepository,
                                 RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    @GetMapping("/admin/reports")
    public String adminReports(Model model) {
        // Event stats
        model.addAttribute("totalEvents", eventRepository.count());
        model.addAttribute("upcomingEvents", eventRepository.countUpcomingEvents());
        model.addAttribute("pastEvents", eventRepository.countPastEvents());

        // User stats
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("adminCount", userRepository.countByRole("admin"));
        model.addAttribute("studentCount", userRepository.countByRole("student"));

        // Registration stats
        model.addAttribute("totalRegistrations", registrationRepository.count());

        return "admin-reports"; // Thymeleaf template
    }
}

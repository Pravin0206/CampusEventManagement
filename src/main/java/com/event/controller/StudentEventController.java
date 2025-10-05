package com.event.controller;

import com.event.entity.Category;
import com.event.entity.Event;
import com.event.entity.Registration;
import com.event.entity.User;
import com.event.service.EventService;
import com.event.service.RegistrationService;
import com.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentEventController {

    private final EventService eventService;
    private final UserService userService;
    private final RegistrationService registrationService;

    public StudentEventController(EventService eventService,
                                  UserService userService,
                                  RegistrationService registrationService) {
        this.eventService = eventService;
        this.userService = userService;
        this.registrationService = registrationService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User student = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Registration> registrations = registrationService.getRegistrationsByUser(student);

        model.addAttribute("studentName", student.getName());
        model.addAttribute("eventCount", eventService.getAllEvents().size());
        model.addAttribute("registrationCount", registrations.size());
        model.addAttribute("categoryCount", 0); 
        model.addAttribute("reportCount", 0);

        return "student-dashboard";
    }

    // View all events + Search filter + Categories dropdown
    @GetMapping("/events")
    public String viewEvents(@RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "categoryId", required = false) Long categoryId,
                             Model model,
                             Principal principal) {

        // Fetch all events and filter
    	List<Event> events;
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            events = eventService.searchEvents(keyword.trim(), categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            events = eventService.searchEvents(keyword.trim(), null);
        } else if (categoryId != null) {
            events = eventService.searchEvents(null, categoryId);
        } else {
            events = eventService.getAllEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        // Fetch all categories for dropdown
        List<Category> categories = eventService.getAllCategories();
        model.addAttribute("categories", categories);

        // Student info for registrations
        User student = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Registration> registrations = registrationService.getRegistrationsByUser(student);

        Set<Long> registeredEventIds = registrations.stream()
                .map(r -> r.getEvent().getId())
                .collect(Collectors.toSet());
        model.addAttribute("registeredEventIds", registeredEventIds);

        // Map eventId -> registrationId for cancel buttons
        model.addAttribute("eventIdToRegistrationId",
                registrations.stream()
                        .collect(Collectors.toMap(r -> r.getEvent().getId(), Registration::getId))
        );

        return "student-events";
    }
}
package com.event.controller;

import com.event.entity.Event;
import com.event.entity.Category;
import com.event.entity.Registration;
import com.event.repository.EventRepository;
import com.event.repository.CategoryRepository;
import com.event.repository.RegistrationRepository;
import com.event.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminEventController {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RegistrationRepository registrationRepository;
    private final RegistrationService registrationService;

    public AdminEventController(EventRepository eventRepository,
                                CategoryRepository categoryRepository,
                                RegistrationRepository registrationRepository,
                                RegistrationService registrationService) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.registrationRepository = registrationRepository;
        this.registrationService = registrationService;
    }

    // Admin dashboard
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        model.addAttribute("adminName", principal.getName());
        model.addAttribute("eventCount", eventRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("registrationCount", registrationRepository.count());
        return "admin-dashboard";
    }

    // List all events
    @GetMapping("/events")
    public String events(
            Model model,
            @RequestParam(value = "location", required = false) String location) {

        List<Event> events;
        String message = null;

        if (location != null && !location.isEmpty()) {
            // Fetch events whose location starts with the given prefix (case-insensitive)
            events = eventRepository.findByLocationStartingWithIgnoreCase(location);

            if (events.isEmpty()) {
                message = "No location found for '" + location + "'";
            }
        } else {
            // No location filter, show all events
            events = eventRepository.findAll();
        }

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("events", events);
        model.addAttribute("categories", categories);
        model.addAttribute("searchLocation", location);
        model.addAttribute("message", message);

        return "admin-events";
    }

    // Show form to create new event
    @GetMapping("/event/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", categoryRepository.findAll());
        return "event-form";
    }

    // Show form to edit existing event
    @GetMapping("/events/update/{id}")
    public String editEventForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + id));
        model.addAttribute("event", event);
        model.addAttribute("categories", categoryRepository.findAll());
        return "event-form";
    }

    // Save new or edited event
    @PostMapping("/event/save")
    public String saveEvent(@ModelAttribute Event event) {
        eventRepository.save(event);
        return "redirect:/admin/events";
    }

    // Delete an event
    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/admin/events";
    }

    // View all registrations
    @GetMapping("/registrations")
    public String viewRegistrations(Model model) {
        List<Registration> registrations = registrationService.getAllRegistrations();
        model.addAttribute("registrations", registrations);
        return "view-registrations";
    }
}
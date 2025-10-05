package com.event.controller;

import com.event.entity.Event;
import com.event.entity.Category;
import com.event.entity.User;
import com.event.service.EventService;
import com.event.service.CategoryService;
import com.event.service.EmailNotificationService;
import com.event.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EmailNotificationService emailService;

    @Autowired
    private UserService userService;

    // ========================
    // Thymeleaf Pages
    // ========================

    @GetMapping
    public String getAllEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("events", events);
        model.addAttribute("categories", categories);
        return "manage-events";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "event-form";
    }

    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") Event event) {
        // Load actual Category entity from DB
        if (event.getCategory() != null && event.getCategory().getId() != null) {
            Category cat = categoryService.getCategoryById(event.getCategory().getId())
                                          .orElseThrow(() -> new RuntimeException("Category not found"));
            event.setCategory(cat);
        }

        // Save event
        eventService.saveEvent(event);

        // OPTIONAL: Send email notification (only to admin or skip for now)
        try {
            String subject = "New Event Created: " + event.getTitle();
            String body = "<h3>A new event has been created!</h3>" +
                          "<p><b>Event:</b> " + event.getTitle() + "</p>" +
                          "<p><b>Date:</b> " + event.getDate() + "</p>" +
                          "<p><b>Time:</b> " + event.getTime() + "</p>" +
                          "<p><b>Location:</b> " + event.getLocation() + "</p>";

            // Example: notify admin only
            emailService.sendHtmlEmail("s99042132@gmail.com", subject, body);

        } catch (Exception e) {
            System.out.println("Failed to send event emails: " + e.getMessage());
        }

        return "redirect:/events";
    }

    @GetMapping("/update/{id}")
    public String editEvent(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "event-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }
}
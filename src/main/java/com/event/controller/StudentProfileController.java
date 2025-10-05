package com.event.controller;

import com.event.entity.User;
import com.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/student/profile")
public class StudentProfileController {

    private final UserService userService;

    public StudentProfileController(UserService userService) {
        this.userService = userService;
    }

    // Show profile edit form
    @GetMapping("/edit")
    public String showProfile(Model model, Principal principal) {
        String username = principal.getName(); // logged-in user
        Optional<User> userOpt = userService.getUserByUsername(username);

        model.addAttribute("user", userOpt.orElse(new User())); // fallback if not found
        return "student-profile"; // Thymeleaf template
    }

    // Handle form submission
    @PostMapping("/update")
    public String updateProfile(User updatedUser, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        userService.getUserByUsername(username).ifPresent(existingUser -> {
            // Delegate profile update to service
            userService.updateUserProfile(existingUser.getId(), updatedUser);
        });

        redirectAttributes.addFlashAttribute("message", "✅ Profile updated successfully!");
        return "redirect:/student/profile/edit?success";
    }

    // Optional: Add new student (if you need it)
    @PostMapping("/create")
    public String createStudent(User user) {
        // Use saveUser instead of createUser
        userService.saveUser(user);
        return "redirect:/student/profile/create?success";
    }
}
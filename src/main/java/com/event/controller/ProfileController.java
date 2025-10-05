package com.event.controller;

import com.event.entity.User;
import com.event.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // Show profile edit form
    @GetMapping("/edit")
    public String showEditProfile(Model model, Principal principal) {
        String username = principal.getName(); // logged-in user
        Optional<User> userOpt = userService.getUserByUsername(username);

        model.addAttribute("user", userOpt.orElse(new User())); // cleaner fallback

        return "profile-edit"; // Thymeleaf template
    }

    // Handle form submission
    @PostMapping("/update")
    public String updateProfile(User updatedUser, Principal principal) {
        String username = principal.getName();
        userService.getUserByUsername(username).ifPresent(existingUser -> {
            userService.updateUserProfile(existingUser.getId(), updatedUser);
        });

        return "redirect:/user/profile/edit?success";
    }
}
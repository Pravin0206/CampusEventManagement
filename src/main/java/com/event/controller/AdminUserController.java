package com.event.controller;

import com.event.entity.User;
import com.event.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // List all users
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users-list"; // your Thymeleaf template
    }

    // Inline update user
    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String username,
                           @RequestParam String role,
                           @RequestParam(required = false) String password) {

        // Fetch existing user
        User existingUser = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setUsername(username);
        existingUser.setRole(role.toLowerCase()); // normalize role

        // Only update password if provided
        if (password != null && !password.isBlank()) {
            existingUser.setPassword(password);
        }

        userService.updateUserProfile(id, existingUser);

        return "redirect:/admin/users";
    }

    // Delete user
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
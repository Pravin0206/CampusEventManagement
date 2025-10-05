package com.event.controller;

import com.event.entity.User;
import com.event.implementation.UserServiceImplementation;
import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserServiceImplementation userService;
    private final AuthenticationManager authenticationManager;

    // Constructor injection for both services
    public UserController(UserServiceImplementation userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // ===================== SIGNUP =====================
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup"; // Thymeleaf template
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute User user, Model model) {
        try {
            // Save new user (password encoded inside your UserService)
            userService.saveUser(user);

            // Authenticate the new user
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Redirect to profile/dashboard
            return "redirect:/users/profile";

        } catch (Exception e) {
            model.addAttribute("error", "User with this username or email already exists!");
            return "signup";
        }
    }

    // ===================== LOGIN =====================
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "signupSuccess", required = false) String signupSuccess,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid username or password!");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        if (signupSuccess != null) model.addAttribute("message", "Signup successful! Please log in.");
        return "login"; // Thymeleaf template: templates/login.html
    }

    // ===================== PROFILE EDIT =====================
    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, Principal principal) {
        String username = principal.getName();
        User existingUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update all fields including password directly (no encoding)
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setLinkedIn(updatedUser.getLinkedIn());
        existingUser.setBio(updatedUser.getBio());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(updatedUser.getPassword()); // plain text
        }

        userService.updateUserProfile(existingUser.getId(), existingUser);
        return "redirect:/users/profile/edit?success";
    }

    // ===================== VIEW PROFILE =====================
    @GetMapping("/profile")
    public String viewProfileRedirect(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // not logged in
        }

        // Get roles
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        if (isAdmin) {
            // Admins still go to their profile page
            String username = principal.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            return "admin-profile"; 
        } else {
            // Students go to their profile
            String username = principal.getName();
            User student = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            model.addAttribute("user", student);
            return "student-profile"; // template: student-profile.html
        }
    }
}
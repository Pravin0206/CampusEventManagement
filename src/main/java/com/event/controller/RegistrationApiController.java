package com.event.controller;

import com.event.entity.Registration;
import com.event.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 💡 Use @RestController for API endpoints
@RequestMapping("/api/registrations") // 💡 Use a clear API path
public class RegistrationApiController {

    @Autowired
    private RegistrationService registrationService;

    // =========================================================================
    // OPTION: Query by User ID
    // URL: GET /api/registrations/user/{userId}
    // =========================================================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Registration>> getRegistrationsByUser(@PathVariable Long userId) {
        
        List<Registration> registrations = registrationService.getRegistrationsByUserId(userId);

        if (registrations.isEmpty()) {
            // Returns 404 Not Found if no registrations are found
            return ResponseEntity.notFound().build();
        }
        
        // Returns 200 OK with the list of registrations (including event details)
        return ResponseEntity.ok(registrations);
    }
}
package com.event.controller;

import com.event.entity.User;
import com.event.implementation.UserServiceImplementation; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController // Crucial: Handles API requests and returns JSON
@RequestMapping("/api/auth") // Prefix for all API authentication endpoints
public class AuthController {

    private final UserServiceImplementation userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserServiceImplementation userService, 
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // ===================== API SIGNUP/REGISTER =====================
    // URL: POST /api/auth/register
    // Accepts the full User entity from the JSON request body
 // URL: POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        
        // 1. Check for existing username (Quick exit on known error)
        Optional<User> existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
        }

        try {
            // 2. Attempt to save the user (This is where the DB error occurs)
            User savedUser = userService.saveUser(user);

            // If successful, return 201 CREATED
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        } catch (Exception e) {
            // 🚨 CRITICAL FIX: Log the exception and return a non-200 status.
            System.err.println("--- DB SAVE FAILURE ---");
            System.err.println("Registration failed. Check the stack trace below for the specific MySQL/Hibernate error:");
            e.printStackTrace(); 
            
            // 3. Force 500 status to stop the misleading 200 OK response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null); 
        }
    }

    // ===================== API LOGIN =====================
    // URL: POST /api/auth/login
    // Accepts the User entity, expecting only username and password fields to be set.
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        try {
            // 1. Authenticate user using the credentials from the User entity
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
            );

            // 2. Set the authenticated user in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Get the role for the response
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            // 4. Return success status and a simple message/role structure
            return ResponseEntity.ok(new AuthResponse("Login successful", role));

        } catch (Exception e) {
            // Return 401 Unauthorized for invalid credentials
            return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
        }
    }
}

// Simple internal class to structure the login response JSON
class AuthResponse {
    private String message;
    private String role;
    
    public AuthResponse(String message, String role) {
        this.message = message;
        this.role = role;
    }
    
	public String getMessage() {
		return message;
	}

	public String getRole() {
		return role;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
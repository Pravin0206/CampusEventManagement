package com.event.service;

import com.event.entity.Registration;
import com.event.entity.User;
import com.event.entity.Event;
import java.util.List;

public interface RegistrationService {

    // WRITE operation
    Registration registerUserForEvent(User user, Event event);
    
    // READ operation - Critical for My Registrations list
    List<Registration> getRegistrationsByUser(User user);

    // READ operation
    List<Registration> getRegistrationsByEvent(Event event);
    
    // READ operation - Must return the entity to align with common usage (Fixes Errors 2 & 3)
    Registration getRegistrationById(Long id); 

    // WRITE operation
    void cancelRegistration(Long id);
    
    // READ operation
    List<Registration> getAllRegistrations();
    
    // WRITE operation
    Registration saveRegistration(Registration registration);
    
    List<Registration> getRegistrationsByUserId(Long userId);
}
package com.event.implementation;

import com.event.entity.Registration;
import com.event.entity.User;
import com.event.entity.Event;
import com.event.repository.RegistrationRepository;
import com.event.service.RegistrationService;
import com.event.service.EmailNotificationService;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistrationServiceImplementation implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EmailNotificationService emailNotificationService;

    // ✅ Constructor injection for both dependencies
    public RegistrationServiceImplementation(RegistrationRepository registrationRepository,
                                             EmailNotificationService emailNotificationService) {
        this.registrationRepository = registrationRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public Registration registerUserForEvent(User user, Event event) {
        // Prevent duplicate registrations
        if (registrationRepository.findByUserAndEvent(user, event).isPresent()) {
            throw new RuntimeException("Already registered for this event.");
        }

        // Create new registration
        Registration reg = new Registration(user, event);

        // ✅ Set registration timestamp
        reg.setRegistrationDate(LocalDateTime.now());

        // Save into database
        Registration saved = registrationRepository.save(reg);

        // ✅ Send registration confirmation email
        try {
            emailNotificationService.sendRegistrationConfirmationHtml(saved);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send confirmation email: " + e.getMessage());
        }

        // Debug log
        System.out.println("DEBUG: Saved registration id=" + saved.getId()
                + " user=" + user.getUsername()
                + " event=" + event.getTitle()
                + " at " + saved.getRegistrationDate());

        return saved;
    }

    @Override
    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUserId(user.getId());
    }

    @Override
    public List<Registration> getRegistrationsByEvent(Event event) {
        return registrationRepository.findByEvent(event);
    }

    @Override
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with ID: " + id));
    }

    @Override
    public void cancelRegistration(Long id) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with ID: " + id));

        User user = reg.getUser();
        Event event = reg.getEvent();

        // ✅ Send cancellation email
        try {
            String subject = "Registration Cancelled for " + event.getTitle();
            String htmlBody = "<html>"
                    + "<body style='font-family: Arial, sans-serif;'>"
                    + "<h2 style='color: #E53935;'>Registration Cancelled</h2>"
                    + "<p>Hello " + user.getUsername() + ",</p>"
                    + "<p>Your registration for the event <strong>" + event.getTitle() + "</strong> has been cancelled.</p>"
                    + "<p>If this was a mistake, please register again.</p>"
                    + "</body></html>";

            emailNotificationService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
        } catch (Exception e) {
            System.err.println("❌ Failed to send cancellation email: " + e.getMessage());
        }

        // ✅ Now delete registration
        registrationRepository.deleteById(id);

        System.out.println("DEBUG: Registration cancelled for user=" + user.getUsername()
                + " event=" + event.getTitle());
    }

    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public Registration saveRegistration(Registration registration) {
        // Ensure timestamp is set if missing
        if (registration.getRegistrationDate() == null) {
            registration.setRegistrationDate(LocalDateTime.now());
        }
        return registrationRepository.save(registration);
    }

    @Override
    public List<Registration> getRegistrationsByUserId(Long userId) {
        return registrationRepository.findByUserId(userId);
    }
}
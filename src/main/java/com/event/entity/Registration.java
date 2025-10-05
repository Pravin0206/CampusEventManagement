package com.event.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Attendance attendance;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // 💡 Maps to the new SQL column 'registered_at'
    @Column(name = "registered_at", nullable = false) 
    private LocalDateTime registrationDate;

    // ---------------- Constructors ----------------
    public Registration() {
    }

    // Recommended constructor for manual creation (date must be set in service)
    public Registration(User user, Event event) {
        this.user = user;
        this.event = event;
    }
    
    // ---------------- Getters and Setters ----------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    // Setter is still required, used by the service to set LocalDateTime.now()
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
}
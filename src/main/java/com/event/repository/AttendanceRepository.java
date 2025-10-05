package com.event.repository;

import com.event.entity.Attendance;
import com.event.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Existing method
    Optional<Attendance> findByRegistration(Registration registration);

    // New method to support marking attendance by registration ID
    Optional<Attendance> findByRegistrationId(Long registrationId);
}
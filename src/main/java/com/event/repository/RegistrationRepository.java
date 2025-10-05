package com.event.repository;

import com.event.entity.Registration;
import com.event.entity.User; // <--- MISSING IMPORT ADDED
import com.event.entity.Event; // <--- MISSING IMPORT ADDED
import java.util.List;
import java.util.Optional; // <--- ADDED for findByUserAndEvent
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository; // <--- MISSING ANNOTATION ADDED
import jakarta.persistence.QueryHint;


@Repository // <--- ADDED
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // 1. RESTORED: Needed by StudentReportController (Fixes compilation error)
    long countByUser_Username(String username);

    // 2. RESTORED: Needed by AttendanceServiceImplementation (Fixes compilation error)
    List<Registration> findByEvent(Event event); 
    
    // 3. RESTORED: Often used to check if a user is already registered
    Optional<Registration> findByUserAndEvent(User user, Event event);

    // Basic: fetch by userId only (Keep this for simpler queries if needed)
    List<Registration> findByUserId(Long userId);

    // ✅ CORE FIX: Advanced fetch by userId + join fetch event details (to solve the '0 registrations' issue)
    // NOTE: QueryHints are a fallback; the transactional fix is usually enough.
    /* @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "false"),
        @QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS")
    })
    */
    @Query("SELECT r FROM Registration r " +
           "JOIN FETCH r.event e " +
           "WHERE r.user.id = :userId")
    List<Registration> findByUserWithDetails(@Param("userId") Long userId);

    // Fetch all registrations for an event
    List<Registration> findByEventId(Long eventId);
}
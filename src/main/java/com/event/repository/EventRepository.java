package com.event.repository;

import com.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Past events
    @Query("SELECT e FROM Event e WHERE e.date < CURRENT_DATE")
    List<Event> findPastEventsList();

    // Admin Reports
    @Query("SELECT COUNT(e) FROM Event e WHERE e.date >= CURRENT_DATE")
    long countUpcomingEvents();

    @Query("SELECT COUNT(e) FROM Event e WHERE e.date < CURRENT_DATE")
    long countPastEvents();

    // Student Reports
    @Query("SELECT COUNT(e) FROM Event e WHERE e.date >= CURRENT_DATE")
    long countAvailableForStudent();

    @Query("SELECT COUNT(r.event) FROM Registration r WHERE r.user.username = :username AND r.event.date >= CURRENT_DATE")
    long countUpcomingForStudent(@Param("username") String username);

    @Query("SELECT COUNT(r.event) FROM Registration r WHERE r.user.username = :username AND r.event.date < CURRENT_DATE")
    long countPastForStudent(@Param("username") String username);

    // 🔹 Flexible search methods

    // Search by title anywhere, case-insensitive
    List<Event> findByTitleContainingIgnoreCase(String keyword);

    // Search by title and category
    List<Event> findByTitleContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId);

    // Optional: search only by category
    List<Event> findByCategoryId(Long categoryId);

    // Optional: search by location
    List<Event> findByLocationStartingWithIgnoreCase(String prefix);
}
package com.event.service;

import com.event.entity.Attendance;
import com.event.entity.User;

import java.util.List;

public interface AttendanceService {

    List<Attendance> findPendingAttendances();

    Attendance getAttendanceById(Long attendanceId);

    Attendance markAttendance(Long registrationId);

    void approveAttendance(Long attendanceId, User admin);

    void rejectAttendance(Long attendanceId, User admin);

    void markPastEventsAutomatically();
}
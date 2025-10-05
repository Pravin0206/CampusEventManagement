package com.event.implementation;

import com.event.entity.Attendance;
import com.event.entity.Registration;
import com.event.entity.User;
import com.event.repository.AttendanceRepository;
import com.event.service.AttendanceService;
import com.event.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceServiceImplementation implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private RegistrationService registrationService;

    @Override
    public List<Attendance> findPendingAttendances() {
        return attendanceRepository.findAll()
                .stream()
                .filter(a -> a.getStatus() == Attendance.Status.PENDING)
                .toList();
    }

    @Override
    public Attendance getAttendanceById(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
    }

    @Override
    public Attendance markAttendance(Long registrationId) {
        Attendance attendance = attendanceRepository.findByRegistrationId(registrationId)
                .orElseGet(() -> {
                    Attendance newAttendance = new Attendance();
                    Registration reg = registrationService.getRegistrationById(registrationId);
                    newAttendance.setRegistration(reg);
                    newAttendance.setStatus(Attendance.Status.PENDING);
                    return newAttendance;
                });

        attendance.setAttended(true);
        attendance.setAttendedAt(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public void approveAttendance(Long attendanceId, User admin) {
        Attendance attendance = getAttendanceById(attendanceId);
        attendance.setStatus(Attendance.Status.APPROVED);
        attendance.setApprovedBy(admin);
        attendance.setApprovalDate(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }

    @Override
    public void rejectAttendance(Long attendanceId, User admin) {
        Attendance attendance = getAttendanceById(attendanceId);
        attendance.setStatus(Attendance.Status.REJECTED);
        attendance.setRejectedBy(admin);
        attendance.setRejectionDate(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }

    @Override
    public void markPastEventsAutomatically() {
        LocalDateTime now = LocalDateTime.now();
        List<Attendance> attendances = attendanceRepository.findAll();
        for (Attendance a : attendances) {
            if (a.getStatus() == Attendance.Status.PENDING &&
                a.getRegistration().getEvent().getDate().atStartOfDay().isBefore(now)) {
                a.setAttended(true);
                a.setAttendedAt(now);
                attendanceRepository.save(a);
            }
        }
    }
}
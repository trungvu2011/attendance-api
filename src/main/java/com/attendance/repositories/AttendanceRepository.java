package com.attendance.repositories;

import com.attendance.entities.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findBySchedule_ScheduleId(UUID scheduleId);
    List<Attendance> findByStudent_UserId(UUID userId);
}

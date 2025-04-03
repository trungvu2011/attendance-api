package com.attendance.repositories;

import com.attendance.entities.Attendance;
import com.attendance.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
}

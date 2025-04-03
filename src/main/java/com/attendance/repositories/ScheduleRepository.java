package com.attendance.repositories;

import com.attendance.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByClassRoom_ClassId(UUID classId);
}

package com.attendance.repositories;

import com.attendance.entities.ExamAttendance;
import com.attendance.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

}

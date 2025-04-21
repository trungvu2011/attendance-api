package com.attendance.repositories;

import com.attendance.entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findBySubject(String subject);
    List<Exam> findBySemester(String semester);
    List<Exam> findByDate(LocalDate date);
    List<Exam> findByRoom_RoomId(UUID roomId);
    List<Exam> findBySchedule_ScheduleId(Integer scheduleId);
}
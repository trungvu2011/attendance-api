package com.attendance.repositories;

import com.attendance.entities.ExamAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamAttendanceRepository extends JpaRepository<ExamAttendance, UUID> {
    List<ExamAttendance> findByExam_ExamId(UUID examID);
    List<ExamAttendance> findByCandidate_UserId(UUID userId);
}

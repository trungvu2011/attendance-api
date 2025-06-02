package com.attendance.repositories;

import com.attendance.entities.ExamAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamAttendanceRepository extends JpaRepository<ExamAttendance, UUID> {
    List<ExamAttendance> findByExam_ExamId(UUID examId);

    List<ExamAttendance> findByCandidate_UserId(UUID userId);

    List<ExamAttendance> findByAttendanceTimeBetween(LocalDateTime start, LocalDateTime end);

    List<ExamAttendance> findByExam_ExamIdAndAttendanceTimeBetween(UUID examId, LocalDateTime start, LocalDateTime end);

    Optional<ExamAttendance> findByCandidate_UserIdAndExam_ExamId(UUID candidateId, UUID examId);
}

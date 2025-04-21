package com.attendance.repositories;

import com.attendance.entities.MonitoringLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonitoringLogsRepository extends JpaRepository<MonitoringLogs, UUID> {
    List<MonitoringLogs> findAll();
    List<MonitoringLogs> findByCandidate_UserId(UUID candidateId);
    List<MonitoringLogs> findByExam_ExamId(UUID examId);
}

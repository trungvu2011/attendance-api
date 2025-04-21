package com.attendance.service;

import com.attendance.entities.Exam;
import com.attendance.entities.MonitoringLogs;
import com.attendance.entities.User;
import com.attendance.repositories.ExamRepository;
import com.attendance.repositories.MonitoringLogsRepository;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonitoringLogsService {

    private final MonitoringLogsRepository monitoringLogsRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    @Autowired
    public MonitoringLogsService(
            MonitoringLogsRepository monitoringLogsRepository,
            UserRepository userRepository,
            ExamRepository examRepository) {
        this.monitoringLogsRepository = monitoringLogsRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    public List<MonitoringLogs> getAllLogs() {
        return monitoringLogsRepository.findAll();
    }

    public Optional<MonitoringLogs> getLogById(UUID logId) {
        return monitoringLogsRepository.findById(logId);
    }

    public List<MonitoringLogs> getLogsByExamId(UUID examId) {
        return monitoringLogsRepository.findByExam_ExamId(examId);
    }

    public List<MonitoringLogs> getLogsByCandidateId(UUID candidateId) {
        return monitoringLogsRepository.findByCandidate_UserId(candidateId);
    }

    public MonitoringLogs createLog(UUID candidateId, UUID examId, MonitoringLogs.CameraEvent cameraEvent, String faceUrl) {
        Optional<User> candidateOpt = userRepository.findById(candidateId);
        Optional<Exam> examOpt = examRepository.findById(examId);

        if (candidateOpt.isPresent() && examOpt.isPresent()) {
            MonitoringLogs log = new MonitoringLogs();
            log.setCandidate(candidateOpt.get());
            log.setExam(examOpt.get());
            log.setTimestamp(LocalDateTime.now());
            log.setCameraEvent(cameraEvent);
            log.setFaceUrl(faceUrl);
            return monitoringLogsRepository.save(log);
        } else {
            throw new IllegalArgumentException("Candidate or Exam not found");
        }
    }

    public void deleteLog(UUID logId) {
        monitoringLogsRepository.deleteById(logId);
    }
}
package com.attendance.service;

import com.attendance.entities.CandidateInExam;
import com.attendance.entities.Exam;
import com.attendance.entities.ExamAttendance;
import com.attendance.entities.User;
import com.attendance.repositories.CandidateInExamRepository;
import com.attendance.repositories.ExamAttendanceRepository;
import com.attendance.repositories.ExamRepository;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExamAttendanceService {

    private final ExamAttendanceRepository examAttendanceRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final CandidateInExamRepository candidateInExamRepository;

    @Autowired
    public ExamAttendanceService(
            ExamAttendanceRepository examAttendanceRepository,
            UserRepository userRepository,
            ExamRepository examRepository,
            CandidateInExamRepository candidateInExamRepository) {
        this.examAttendanceRepository = examAttendanceRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.candidateInExamRepository = candidateInExamRepository;
    }

    public List<ExamAttendance> getAllAttendances() {
        return examAttendanceRepository.findAll();
    }

    public Optional<ExamAttendance> getAttendanceById(UUID attendanceId) {
        return examAttendanceRepository.findById(attendanceId);
    }

    public List<ExamAttendance> getAttendancesByExamId(UUID examId) {
        return examAttendanceRepository.findByExam_ExamId(examId);
    }

    public List<ExamAttendance> getAttendancesByCandidateId(UUID candidateId) {
        return examAttendanceRepository.findByCandidate_UserId(candidateId);
    }

    public ExamAttendance markAttendance(UUID candidateId, UUID examId, boolean citizenCardVerified, boolean faceVerified) {
        // Check if the candidate is registered for this exam
        CandidateInExam candidateInExam = candidateInExamRepository.findByCandidate_UserIdAndExam_ExamId(candidateId, examId);
        if (candidateInExam == null) {
            throw new IllegalArgumentException("Candidate is not registered for this exam");
        }

        // Check if attendance is already recorded
        List<ExamAttendance> existingAttendance = examAttendanceRepository.findByCandidate_UserId(candidateId);
        for (ExamAttendance attendance : existingAttendance) {
            if (attendance.getExam().getExamId().equals(examId)) {                // Update existing attendance record
                attendance.setCitizenCardVerified(citizenCardVerified);
                attendance.setFaceVerified(faceVerified);
                attendance.setAttendanceTime(LocalDateTime.now());
                return examAttendanceRepository.save(attendance);
            }
        }

        // Create new attendance record
        Optional<User> candidateOpt = userRepository.findById(candidateId);
        Optional<Exam> examOpt = examRepository.findById(examId);

        if (candidateOpt.isPresent() && examOpt.isPresent()) {
            ExamAttendance attendance = new ExamAttendance();
            attendance.setCandidate(candidateOpt.get());            attendance.setExam(examOpt.get());
            attendance.setCitizenCardVerified(citizenCardVerified);
            attendance.setFaceVerified(faceVerified);
            attendance.setAttendanceTime(LocalDateTime.now());
            return examAttendanceRepository.save(attendance);
        } else {
            throw new IllegalArgumentException("Candidate or Exam not found");
        }
    }

    public boolean verifyCitizenCard(UUID candidateId, String citizenCardNumber) {
        Optional<User> candidate = userRepository.findById(candidateId);
        return candidate.map(user -> user.getCitizenId().equals(citizenCardNumber)).orElse(false);
    }

    public boolean verifyFace(UUID candidateId) {
        // In a real application, this would call a face recognition service
        // For now, we'll just return true for demonstration purposes
        return true;
    }    public void deleteAttendance(UUID attendanceId) {
        examAttendanceRepository.deleteById(attendanceId);
    }
    
    public List<ExamAttendance> getAttendancesByTimeRange(LocalDateTime start, LocalDateTime end) {
        return examAttendanceRepository.findByAttendanceTimeBetween(start, end);
    }
    
    public List<ExamAttendance> getAttendancesByExamIdAndTimeRange(UUID examId, LocalDateTime start, LocalDateTime end) {
        return examAttendanceRepository.findByExam_ExamIdAndAttendanceTimeBetween(examId, start, end);
    }
}
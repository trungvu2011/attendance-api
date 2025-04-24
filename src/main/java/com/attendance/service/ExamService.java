package com.attendance.service;

import com.attendance.entities.Exam;
import com.attendance.repositories.CandidateInExamRepository;
import com.attendance.repositories.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final CandidateInExamRepository candidateInExamRepository;

    @Autowired
    public ExamService(ExamRepository examRepository, CandidateInExamRepository candidateInExamRepository) {
        this.examRepository = examRepository;
        this.candidateInExamRepository = candidateInExamRepository;
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Optional<Exam> getExamById(UUID examId) {
        return examRepository.findById(examId);
    }

    public List<Exam> getExamsBySubject(String subject) {
        return examRepository.findBySubject(subject);
    }

    public List<Exam> getExamsBySemester(String semester) {
        return examRepository.findBySemester(semester);
    }

    public List<Exam> getExamsByDate(LocalDate date) {
        return examRepository.findByDate(date);
    }

    public List<Exam> getExamsByRoomId(UUID roomId) {
        return examRepository.findByRoom_RoomId(roomId);
    }

    public List<Exam> getExamsByScheduleId(Integer scheduleId) {
        return examRepository.findBySchedule_ScheduleId(scheduleId);
    }

    public Exam createExam(Exam exam) {
        return examRepository.save(exam);
    }

    public Exam updateExam(Exam exam) {
        return examRepository.save(exam);
    }

    public void deleteExam(UUID examId) {
        examRepository.deleteById(examId);
    }

    // New method to get all exams for a specific candidate
    public List<Exam> getExamsByCandidate(UUID candidateId) {
        return candidateInExamRepository.findByCandidate_UserId(candidateId)
                .stream()
                .map(candidateInExam -> candidateInExam.getExam())
                .collect(Collectors.toList());
    }
}
package com.attendance.controller;

import com.attendance.entities.Exam;
import com.attendance.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        List<Exam> exams = examService.getAllExams();
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable UUID examId) {
        Optional<Exam> exam = examService.getExamById(examId);
        return exam.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Exam>> getExamsBySubject(@PathVariable String subject) {
        List<Exam> exams = examService.getExamsBySubject(subject);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Exam>> getExamsBySemester(@PathVariable String semester) {
        List<Exam> exams = examService.getExamsBySemester(semester);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Exam>> getExamsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Exam> exams = examService.getExamsByDate(date);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Exam>> getExamsByRoomId(@PathVariable UUID roomId) {
        List<Exam> exams = examService.getExamsByRoomId(roomId);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Exam>> getExamsByScheduleId(@PathVariable Integer scheduleId) {
        List<Exam> exams = examService.getExamsByScheduleId(scheduleId);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        Exam createdExam = examService.createExam(exam);
        return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
    }

    @PutMapping("/{examId}")
    public ResponseEntity<Exam> updateExam(@PathVariable UUID examId, @RequestBody Exam exam) {
        Optional<Exam> existingExam = examService.getExamById(examId);
        if (existingExam.isPresent()) {
            exam.setExamId(examId);
            Exam updatedExam = examService.updateExam(exam);
            return new ResponseEntity<>(updatedExam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable UUID examId) {
        Optional<Exam> existingExam = examService.getExamById(examId);
        if (existingExam.isPresent()) {
            examService.deleteExam(examId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
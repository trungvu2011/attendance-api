package com.attendance.controller;

import com.attendance.dto.CheckInDTO;
import com.attendance.entities.ExamAttendance;
import com.attendance.service.ExamAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class ExamAttendanceController {

    private final ExamAttendanceService attendanceService;

    @Autowired
    public ExamAttendanceController(ExamAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<ExamAttendance>> getAllAttendances() {
        List<ExamAttendance> attendances = attendanceService.getAllAttendances();
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<ExamAttendance> getAttendanceById(@PathVariable UUID attendanceId) {
        Optional<ExamAttendance> attendance = attendanceService.getAttendanceById(attendanceId);
        return attendance.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamAttendance>> getAttendancesByExamId(@PathVariable UUID examId) {
        List<ExamAttendance> attendances = attendanceService.getAttendancesByExamId(examId);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ExamAttendance>> getAttendancesByCandidateId(@PathVariable UUID candidateId) {
        List<ExamAttendance> attendances = attendanceService.getAttendancesByCandidateId(candidateId);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }    @PostMapping("/check-in")
    public ResponseEntity<?> markAttendance(
            @RequestParam UUID candidateId,
            @RequestParam UUID examId,
            @RequestParam String citizenCardNumber) {
        
        try {
            boolean citizenCardVerified = attendanceService.verifyCitizenCard(candidateId, citizenCardNumber);
            boolean faceVerified = attendanceService.verifyFace(candidateId);
            
            if (!citizenCardVerified) {
                return new ResponseEntity<>("Citizen card verification failed", HttpStatus.BAD_REQUEST);
            }
            
            if (!faceVerified) {
                return new ResponseEntity<>("Face verification failed", HttpStatus.BAD_REQUEST);
            }
            
            ExamAttendance attendance = attendanceService.markAttendance(
                    candidateId, examId, citizenCardVerified, faceVerified);
            
            return new ResponseEntity<>(attendance, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/check-in/json")
    public ResponseEntity<?> markAttendanceJson(@RequestBody CheckInDTO checkInDTO) {
        try {
            boolean citizenCardVerified = attendanceService.verifyCitizenCard(
                    checkInDTO.getCandidateId(), checkInDTO.getCitizenCardNumber());
            boolean faceVerified = attendanceService.verifyFace(checkInDTO.getCandidateId());
            
            if (!citizenCardVerified) {
                return new ResponseEntity<>("Citizen card verification failed", HttpStatus.BAD_REQUEST);
            }
            
            if (!faceVerified) {
                return new ResponseEntity<>("Face verification failed", HttpStatus.BAD_REQUEST);
            }
            
            ExamAttendance attendance = attendanceService.markAttendance(
                    checkInDTO.getCandidateId(), checkInDTO.getExamId(), 
                    citizenCardVerified, faceVerified);
            
            return new ResponseEntity<>(attendance, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }@DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID attendanceId) {
        Optional<ExamAttendance> attendance = attendanceService.getAttendanceById(attendanceId);
        if (attendance.isPresent()) {
            attendanceService.deleteAttendance(attendanceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/time-range")
    public ResponseEntity<List<ExamAttendance>> getAttendancesByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ExamAttendance> attendances = attendanceService.getAttendancesByTimeRange(start, end);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }
    
    @GetMapping("/exam/{examId}/time-range")
    public ResponseEntity<List<ExamAttendance>> getAttendancesByExamIdAndTimeRange(
            @PathVariable UUID examId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ExamAttendance> attendances = attendanceService.getAttendancesByExamIdAndTimeRange(examId, start, end);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }
}
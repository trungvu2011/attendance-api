package com.attendance.controller;

import com.attendance.dto.CreateExamDTO;
import com.attendance.dto.ExamDTO;
import com.attendance.entities.Exam;
import com.attendance.entities.Room;
import com.attendance.entities.Schedule;
import com.attendance.service.ExamService;
import com.attendance.service.RoomService;
import com.attendance.service.ScheduleService;
import com.attendance.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamService examService;
    private final RoomService roomService;
    private final ScheduleService scheduleService;
    private final TokenUtil tokenUtil;

    @Autowired
    public ExamController(ExamService examService, RoomService roomService, ScheduleService scheduleService,
            TokenUtil tokenUtil) {
        this.examService = examService;
        this.roomService = roomService;
        this.scheduleService = scheduleService;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping
    public ResponseEntity<List<ExamDTO>> getAllExams() {
        List<Exam> exams = examService.getAllExams();
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamDTO> getExamById(@PathVariable UUID examId) {
        Optional<Exam> exam = examService.getExamById(examId);
        return exam.map(value -> new ResponseEntity<>(ExamDTO.fromEntity(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/my-exams")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ExamDTO>> getMyExams() {
        // Get current user ID from security context
        UUID currentUserId = tokenUtil.getCurrentUserId();

        if (currentUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Exam> exams = examService.getExamsByCandidate(currentUserId);
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<ExamDTO>> getExamsBySubject(@PathVariable String subject) {
        List<Exam> exams = examService.getExamsBySubject(subject);
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<ExamDTO>> getExamsBySemester(@PathVariable String semester) {
        List<Exam> exams = examService.getExamsBySemester(semester);
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ExamDTO>> getExamsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Exam> exams = examService.getExamsByDate(date);
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ExamDTO>> getExamsByRoomId(@PathVariable UUID roomId) {
        List<Exam> exams = examService.getExamsByRoomId(roomId);
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<ExamDTO>> getExamsByScheduleId(@PathVariable Integer scheduleId) {
        List<Exam> exams = examService.getExamsByScheduleId(scheduleId);
        List<ExamDTO> examDTOs = exams.stream()
                .map(ExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examDTOs, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createExam(@RequestBody CreateExamDTO createExamDTO) {
        // Kiểm tra và lấy Room
        Optional<Room> roomOpt = roomService.getRoomById(createExamDTO.getRoomId());
        if (roomOpt.isEmpty()) {
            return new ResponseEntity<>("Phòng thi không tồn tại", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra và lấy Schedule
        Optional<Schedule> scheduleOpt = scheduleService.getScheduleById(createExamDTO.getScheduleId());
        if (scheduleOpt.isEmpty()) {
            return new ResponseEntity<>("Ca thi không tồn tại", HttpStatus.BAD_REQUEST);
        }

        // Tạo đối tượng Exam từ DTO
        Exam exam = createExamDTO.toEntity(roomOpt.get(), scheduleOpt.get());

        // Lưu vào cơ sở dữ liệu
        Exam createdExam = examService.createExam(exam);

        // Chuyển đổi kết quả sang DTO để trả về
        ExamDTO examDTO = ExamDTO.fromEntity(createdExam);
        return new ResponseEntity<>(examDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ExamDTO> updateExam(@PathVariable UUID examId, @RequestBody Exam exam) {
        Optional<Exam> existingExam = examService.getExamById(examId);
        if (existingExam.isPresent()) {
            exam.setExamId(examId);
            Exam updatedExam = examService.updateExam(exam);
            ExamDTO examDTO = ExamDTO.fromEntity(updatedExam);
            return new ResponseEntity<>(examDTO, HttpStatus.OK);
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
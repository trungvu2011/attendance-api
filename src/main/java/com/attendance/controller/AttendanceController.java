package com.attendance.controller;

import com.attendance.entities.Attendance;
import com.attendance.entities.User;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Lấy danh sách Attendance theo scheduleId
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Attendance>> getAttendanceBySchedule(@PathVariable UUID scheduleId) {
        List<Attendance> attendances = attendanceService.getAttendanceBySchedule(scheduleId);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }

    // Tạo mới Attendance
    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody Attendance attendance) {
        Attendance createdAttendance = attendanceService.createAttendance(attendance);
        return new ResponseEntity<>(createdAttendance, HttpStatus.CREATED);
    }

    // Cập nhật Attendance
    @PutMapping("/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable UUID attendanceId, @RequestBody Attendance attendance) {
        attendance.setAttendanceId(attendanceId);
        Attendance updatedAttendance = attendanceService.updateAttendance(attendance);
        return new ResponseEntity<>(updatedAttendance, HttpStatus.OK);
    }

    // Xóa Attendance
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // Lấy thống kê điểm danh theo lớp học và khoảng thời gian
    @GetMapping("/stats/class/{classId}")
    public ResponseEntity<Map<User, Map<LocalDate, Attendance.Status>>> getAttendanceStatsByClass(
            @PathVariable UUID classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<User, Map<LocalDate, Attendance.Status>> stats = 
                attendanceService.getAttendanceStatsByClassAndDateRange(classId, startDate, endDate);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    
    // Lấy tỷ lệ điểm danh theo lớp học
    @GetMapping("/rate/class/{classId}")
    public ResponseEntity<Map<User, Double>> getAttendanceRateByClass(@PathVariable UUID classId) {
        Map<User, Double> rates = attendanceService.getAttendanceRateByClass(classId);
        return new ResponseEntity<>(rates, HttpStatus.OK);
    }
}

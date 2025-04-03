package com.attendance.controller;

import com.attendance.entities.Attendance;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}

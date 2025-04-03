package com.attendance.controllers;

import com.attendance.entities.Schedule;
import com.attendance.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // Lấy tất cả lịch học của một lớp
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> getSchedulesByClass(@PathVariable UUID classId) {
        List<Schedule> schedules = scheduleService.getSchedulesByClass(classId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    // Tạo mới một lịch học
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule createdSchedule = scheduleService.createSchedule(schedule);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    // Cập nhật lịch học
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable UUID scheduleId, @RequestBody Schedule schedule) {
        schedule.setScheduleId(scheduleId);
        Schedule updatedSchedule = scheduleService.updateSchedule(schedule);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    // Xóa lịch học
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

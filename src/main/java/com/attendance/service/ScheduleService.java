package com.attendance.service;

import com.attendance.entities.Schedule;
import com.attendance.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // Lấy danh sách tất cả lịch học của một lớp
    public List<Schedule> getSchedulesByClass(UUID classId) {
        return scheduleRepository.findByClassRoom_ClassId(classId);
    }

    // Tạo mới một lịch học
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Cập nhật lịch học
    public Schedule updateSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Xóa lịch học
    public void deleteSchedule(UUID scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}

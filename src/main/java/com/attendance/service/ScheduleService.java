package com.attendance.service;

import com.attendance.entities.Schedule;
import com.attendance.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // Lấy tất cả lịch ca thi
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Lấy lịch ca thi theo ID
    public Optional<Schedule> getScheduleById(Integer scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    // Tạo mới một ca thi
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Cập nhật ca thi
    public Schedule updateSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Xóa lịch ca thi
    public void deleteSchedule(Integer scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}

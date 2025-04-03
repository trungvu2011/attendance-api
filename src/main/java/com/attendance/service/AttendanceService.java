package com.attendance.service;

import com.attendance.entities.Attendance;
import com.attendance.repositories.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // Lấy tất cả bản ghi Attendance của một lịch học (schedule)
    public List<Attendance> getAttendanceBySchedule(UUID scheduleId) {
        return attendanceRepository.findBySchedule_ScheduleId(scheduleId);
    }

    // Tạo mới bản ghi Attendance
    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    // Cập nhật trạng thái Attendance (Ví dụ: present, absent, late)
    public Attendance updateAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    // Xóa Attendance
    public void deleteAttendance(UUID attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }
}

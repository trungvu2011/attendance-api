package com.attendance.service;

import com.attendance.entities.Attendance;
import com.attendance.entities.ClassRoom;
import com.attendance.entities.Schedule;
import com.attendance.entities.User;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repositories.AttendanceRepository;
import com.attendance.repositories.ClassRoomRepository;
import com.attendance.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public AttendanceService(
            AttendanceRepository attendanceRepository,
            ScheduleRepository scheduleRepository,
            ClassRoomRepository classRoomRepository) {
        this.attendanceRepository = attendanceRepository;
        this.scheduleRepository = scheduleRepository;
        this.classRoomRepository = classRoomRepository;
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
    
    // Lấy thống kê điểm danh theo lớp học và khoảng thời gian
    public Map<User, Map<LocalDate, Attendance.Status>> getAttendanceStatsByClassAndDateRange(
            UUID classId, LocalDate startDate, LocalDate endDate) {
        
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found with id: " + classId));
                
        // Lấy tất cả các lịch học (schedule) của lớp trong khoảng thời gian
        List<Schedule> schedules = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getClassRoom().getClassId().equals(classId))
                .filter(schedule -> {
                    LocalDate scheduleDate = schedule.getStartTime().toLocalDate();
                    return !scheduleDate.isBefore(startDate) && !scheduleDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
                
        // Lấy tất cả các bản ghi điểm danh liên quan đến các lịch học đó
        List<Attendance> attendances = new ArrayList<>();
        for (Schedule schedule : schedules) {
            attendances.addAll(attendanceRepository.findBySchedule_ScheduleId(schedule.getScheduleId()));
        }
        
        // Tổng hợp dữ liệu điểm danh theo học sinh và theo ngày
        Map<User, Map<LocalDate, Attendance.Status>> stats = new HashMap<>();
        
        for (Attendance attendance : attendances) {
            User student = attendance.getStudent();
            LocalDate date = attendance.getSchedule().getStartTime().toLocalDate();
            Attendance.Status status = attendance.getStatus();
            
            if (!stats.containsKey(student)) {
                stats.put(student, new HashMap<>());
            }
            
            stats.get(student).put(date, status);
        }
        
        return stats;
    }
    
    // Lấy tỷ lệ điểm danh theo lớp học
    public Map<User, Double> getAttendanceRateByClass(UUID classId) {
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found with id: " + classId));
                
        // Lấy tất cả các lịch học của lớp
        List<Schedule> schedules = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getClassRoom().getClassId().equals(classId))
                .collect(Collectors.toList());
                
        // Lấy tất cả các bản ghi điểm danh liên quan đến các lịch học đó
        List<Attendance> attendances = new ArrayList<>();
        for (Schedule schedule : schedules) {
            attendances.addAll(attendanceRepository.findBySchedule_ScheduleId(schedule.getScheduleId()));
        }
        
        // Tính tỷ lệ điểm danh theo học sinh
        Map<User, List<Attendance>> attendancesByStudent = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getStudent));
                
        Map<User, Double> attendanceRates = new HashMap<>();
        
        for (Map.Entry<User, List<Attendance>> entry : attendancesByStudent.entrySet()) {
            User student = entry.getKey();
            List<Attendance> studentAttendances = entry.getValue();
            
            long presentCount = studentAttendances.stream()
                    .filter(a -> a.getStatus() == Attendance.Status.PRESENT)
                    .count();
                    
            double attendanceRate = schedules.isEmpty() ? 0 : (double) presentCount / schedules.size();
            attendanceRates.put(student, attendanceRate);
        }
        
        return attendanceRates;
    }
}

package com.attendance.service;

import com.attendance.entities.ClassRoom;
import com.attendance.entities.ClassRoom.ClassStatus;
import com.attendance.entities.User;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repositories.ClassRoomRepository;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassRoomService {
    private final ClassRoomRepository classRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ClassRoomService(ClassRoomRepository classRoomRepository, UserRepository userRepository){ 
        this.classRoomRepository = classRoomRepository;
        this.userRepository = userRepository;
    }

    public List<ClassRoom> getAllClassRoom() { 
        return classRoomRepository.findAll();
    }

    public Optional<ClassRoom> getClassRoomById(UUID classId) {
        return classRoomRepository.findById(classId);
    }

    public ClassRoom createClassRoom(ClassRoom classRoom) {
        if (classRoom.getStatus() == null) {
            classRoom.setStatus(ClassStatus.ACTIVE);
        }
        
        // Kiểm tra xem teacher có được chỉ định không
        if (classRoom.getTeacher() == null) {
            throw new IllegalArgumentException("Teacher must be specified for classroom");
        }
        
        // Nếu chỉ có userId, lấy thông tin đầy đủ của teacher từ repository
        UUID teacherId = classRoom.getTeacher().getUserId();
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        
        // Kiểm tra xem teacher có vai trò TEACHER không
        if (teacher.getRole() != User.Role.TEACHER) {
            throw new IllegalArgumentException("Only users with TEACHER role can be assigned as classroom teachers");
        }
        
        // Gán lại teacher với thông tin đầy đủ
        classRoom.setTeacher(teacher);
        
        return classRoomRepository.save(classRoom);
    }

    public ClassRoom updateClassRoom(ClassRoom classRoom) {
        return classRoomRepository.save(classRoom);
    }

    public void deleteClassRoom(UUID classId) { 
        classRoomRepository.deleteById(classId);
    }
    
    public List<ClassRoom> searchClassRooms(String className, String academicYear, String semester) {
        List<ClassRoom> classRooms = classRoomRepository.findAll();
        
        return classRooms.stream()
                .filter(classroom -> className == null || classroom.getClassName().toLowerCase().contains(className.toLowerCase()))
                .filter(classroom -> academicYear == null || classroom.getAcademicYear().equals(academicYear))
                .filter(classroom -> semester == null || classroom.getSemester().equals(semester))
                .collect(Collectors.toList());
    }
    
    public List<ClassRoom> findByTeacherId(UUID teacherId) {
        List<ClassRoom> allClassRooms = classRoomRepository.findAll();
        return allClassRooms.stream()
                .filter(classroom -> classroom.getTeacher() != null && 
                        classroom.getTeacher().getUserId().equals(teacherId))
                .collect(Collectors.toList());
    }
    
    public List<ClassRoom> findByStatus(ClassStatus status) {
        List<ClassRoom> allClassRooms = classRoomRepository.findAll();
        return allClassRooms.stream()
                .filter(classroom -> classroom.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    public List<ClassRoom> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<ClassRoom> allClassRooms = classRoomRepository.findAll();
        return allClassRooms.stream()
                .filter(classroom -> {
                    // Kiểm tra nếu lớp học diễn ra trong khoảng thời gian được chỉ định
                    LocalDate classStartDate = classroom.getStartDate();
                    LocalDate classEndDate = classroom.getEndDate();
                    
                    // Nếu thời gian kết thúc của lớp > thời gian bắt đầu của khoảng thời gian
                    // VÀ thời gian bắt đầu của lớp < thời gian kết thúc của khoảng thời gian
                    return classEndDate != null && classStartDate != null && 
                           classEndDate.isAfter(startDate) && 
                           classStartDate.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }
    
    public ClassRoom updateClassRoomStatus(UUID classId, ClassStatus status) {
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found with id: " + classId));
        classRoom.setStatus(status);
        return classRoomRepository.save(classRoom);
    }
}

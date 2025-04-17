package com.attendance.service;

import com.attendance.dto.StudentInClassDTO;
import com.attendance.entities.ClassRoom;
import com.attendance.entities.StudentInClass;
import com.attendance.entities.User;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repositories.ClassRoomRepository;
import com.attendance.repositories.StudentInClassRepository;
import com.attendance.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentInClassService {
    private final StudentInClassRepository studentInClassRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public StudentInClassService(
            StudentInClassRepository studentInClassRepository,
            UserRepository userRepository,
            ClassRoomRepository classRoomRepository) {
        this.studentInClassRepository = studentInClassRepository;
        this.userRepository = userRepository;
        this.classRoomRepository = classRoomRepository;
    }

    public StudentInClass addStudentToClass(StudentInClassDTO studentInClassDTO) {
        User student = userRepository.findById(studentInClassDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentInClassDTO.getStudentId()));
        
        ClassRoom classRoom = classRoomRepository.findById(studentInClassDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + studentInClassDTO.getClassId()));
        
        // Kiểm tra xem học sinh đã được thêm vào lớp học chưa
        Optional<StudentInClass> existingRecord = studentInClassRepository.findAll().stream()
                .filter(record -> record.getStudent().getUserId().equals(studentInClassDTO.getStudentId()) 
                        && record.getClassRoom().getClassId().equals(studentInClassDTO.getClassId()))
                .findFirst();
        
        if (existingRecord.isPresent()) {
            return existingRecord.get();
        }
        
        // Kiểm tra số lượng học sinh tối đa nếu có giới hạn
        if (classRoom.getMaxStudents() != null) {
            long currentStudentCount = studentInClassRepository.findByClassRoom_ClassId(classRoom.getClassId()).size();
            if (currentStudentCount >= classRoom.getMaxStudents()) {
                throw new IllegalStateException("Class has reached maximum student capacity");
            }
        }
        
        StudentInClass studentInClass = new StudentInClass();
        studentInClass.setStudent(student);
        studentInClass.setClassRoom(classRoom);
        
        return studentInClassRepository.save(studentInClass);
    }
    
    public void removeStudentFromClass(UUID studentId, UUID classId) {
        List<StudentInClass> records = studentInClassRepository.findAll().stream()
                .filter(record -> record.getStudent().getUserId().equals(studentId) 
                        && record.getClassRoom().getClassId().equals(classId))
                .collect(Collectors.toList());
        
        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Student not found in this class");
        }
        
        studentInClassRepository.deleteAll(records);
    }
    
    public List<StudentInClass> getStudentsInClass(UUID classId) {
        return studentInClassRepository.findByClassRoom_ClassId(classId);
    }
    
    public List<ClassRoom> getClassesForStudent(UUID studentId) {
        return studentInClassRepository.findAll().stream()
                .filter(record -> record.getStudent().getUserId().equals(studentId))
                .map(StudentInClass::getClassRoom)
                .collect(Collectors.toList());
    }
}
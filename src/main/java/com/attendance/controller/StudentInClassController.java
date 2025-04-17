package com.attendance.controller;

import com.attendance.dto.StudentInClassDTO;
import com.attendance.entities.ClassRoom;
import com.attendance.entities.StudentInClass;
import com.attendance.entities.User;
import com.attendance.service.StudentInClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classroom/students")
public class StudentInClassController {
    
    private final StudentInClassService studentInClassService;
    
    @Autowired
    public StudentInClassController(StudentInClassService studentInClassService) {
        this.studentInClassService = studentInClassService;
    }
    
    @PostMapping("/add")
    public ResponseEntity<StudentInClass> addStudentToClass(@RequestBody StudentInClassDTO studentInClassDTO) {
        StudentInClass result = studentInClassService.addStudentToClass(studentInClassDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeStudentFromClass(
            @RequestParam UUID studentId,
            @RequestParam UUID classId) {
        studentInClassService.removeStudentFromClass(studentId, classId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<User>> getStudentsInClass(@PathVariable UUID classId) {
        List<User> students = studentInClassService.getStudentsInClass(classId).stream()
                .map(StudentInClass::getStudent)
                .collect(Collectors.toList());
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClassRoom>> getClassesForStudent(@PathVariable UUID studentId) {
        List<ClassRoom> classes = studentInClassService.getClassesForStudent(studentId);
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Void> addMultipleStudentsToClass(
            @RequestParam UUID classId,
            @RequestBody List<UUID> studentIds) {
        for (UUID studentId : studentIds) {
            StudentInClassDTO dto = new StudentInClassDTO(studentId, classId);
            studentInClassService.addStudentToClass(dto);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
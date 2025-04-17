package com.attendance.controller;

import com.attendance.entities.ClassRoom;
import com.attendance.entities.ClassRoom.ClassStatus;
import com.attendance.service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/classroom")
public class ClassRoomController {
    private final ClassRoomService classRoomService;

    @Autowired
    public ClassRoomController(ClassRoomService classRoomService){
        this.classRoomService = classRoomService;
    }

    @PostMapping
    public ResponseEntity<ClassRoom> createClassRoom(@RequestBody ClassRoom classRoom){
        ClassRoom createdClassRoom = classRoomService.createClassRoom(classRoom);
        return new ResponseEntity<>(createdClassRoom, HttpStatus.CREATED);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<Optional<ClassRoom>> getClassRoomById(@PathVariable UUID classId) {
        Optional<ClassRoom> classRoom = classRoomService.getClassRoomById(classId);
        return new ResponseEntity<>(classRoom, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClassRoom>> getAllClassRooms(){
        List<ClassRoom> classRooms = classRoomService.getAllClassRoom();
        return new ResponseEntity<>(classRooms, HttpStatus.OK);
    }

    @PutMapping("/{classId}")
    public ResponseEntity<ClassRoom> updateClassRoom(@PathVariable UUID classId, @RequestBody ClassRoom classRoom) {
        classRoom.setClassId(classId);
        ClassRoom updatedClassRoom = classRoomService.updateClassRoom(classRoom);
        return new ResponseEntity<>(updatedClassRoom, HttpStatus.OK);
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClassRoom(@PathVariable UUID classId) {
        classRoomService.deleteClassRoom(classId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ClassRoom>> searchClassRooms(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester) {
        List<ClassRoom> classRooms = classRoomService.searchClassRooms(className, academicYear, semester);
        return new ResponseEntity<>(classRooms, HttpStatus.OK);
    }
    
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<ClassRoom>> getClassRoomsByTeacher(@PathVariable UUID teacherId) {
        List<ClassRoom> classRooms = classRoomService.findByTeacherId(teacherId);
        return new ResponseEntity<>(classRooms, HttpStatus.OK);
    }
    
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ClassRoom>> getClassRoomsByStatus(@PathVariable ClassStatus status) {
        List<ClassRoom> classRooms = classRoomService.findByStatus(status);
        return new ResponseEntity<>(classRooms, HttpStatus.OK);
    }
    
    @GetMapping("/by-date-range")
    public ResponseEntity<List<ClassRoom>> getClassRoomsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ClassRoom> classRooms = classRoomService.findByDateRange(startDate, endDate);
        return new ResponseEntity<>(classRooms, HttpStatus.OK);
    }
    
    @PatchMapping("/{classId}/status")
    public ResponseEntity<ClassRoom> updateClassRoomStatus(
            @PathVariable UUID classId,
            @RequestParam ClassStatus status) {
        ClassRoom updatedClassRoom = classRoomService.updateClassRoomStatus(classId, status);
        return new ResponseEntity<>(updatedClassRoom, HttpStatus.OK);
    }
}

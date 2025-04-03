package com.attendance.controller;

import com.attendance.entities.ClassRoom;
import com.attendance.service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return  new ResponseEntity<>(createdClassRoom, HttpStatus.CREATED);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<Optional<ClassRoom>> getUserById(@PathVariable UUID classId) {
        Optional<ClassRoom> classRoom = classRoomService.getClassRoomById(classId);
        return new ResponseEntity<>(classRoom, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClassRoom>> getAllUsers(){
        List<ClassRoom> classRooms = classRoomService.getAllClassRoom();
        return new ResponseEntity<>(classRooms, HttpStatus.OK);
    }

    @PutMapping("/{classId}")
    public ResponseEntity<ClassRoom> updateAttendance(@PathVariable UUID classId, @RequestBody ClassRoom classRoom) {
        classRoom.setClassId(classId);
        ClassRoom updatedClassRoom = classRoomService.updateClassRoom(classRoom);
        return new ResponseEntity<>(updatedClassRoom, HttpStatus.OK);
    }

    // Xóa Attendance
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID classId) {
        classRoomService.deleteClassRoom(classId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

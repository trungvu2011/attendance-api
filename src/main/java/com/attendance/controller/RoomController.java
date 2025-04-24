package com.attendance.controller;

import com.attendance.dto.RoomDTO;
import com.attendance.entities.Room;
import com.attendance.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomDTO> roomDTOs = rooms.stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(roomDTOs, HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable UUID roomId) {
        Optional<Room> room = roomService.getRoomById(roomId);
        return room.map(value -> new ResponseEntity<>(RoomDTO.fromEntity(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        Room room = roomDTO.toEntity();
        Room createdRoom = roomService.createRoom(room);
        RoomDTO createdRoomDTO = RoomDTO.fromEntity(createdRoom);
        return new ResponseEntity<>(createdRoomDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable UUID roomId, @RequestBody RoomDTO roomDTO) {
        Optional<Room> existingRoom = roomService.getRoomById(roomId);
        if (existingRoom.isPresent()) {
            Room room = roomDTO.toEntity();
            room.setRoomId(roomId);
            Room updatedRoom = roomService.updateRoom(room);
            RoomDTO updatedRoomDTO = RoomDTO.fromEntity(updatedRoom);
            return new ResponseEntity<>(updatedRoomDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        Optional<Room> existingRoom = roomService.getRoomById(roomId);
        if (existingRoom.isPresent()) {
            roomService.deleteRoom(roomId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
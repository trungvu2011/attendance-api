package com.attendance.dto;

import com.attendance.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private UUID roomId;
    private String name;
    private String building;

    public static RoomDTO fromEntity(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setName(room.getName());
        dto.setBuilding(room.getBuilding());
        return dto;
    }

    public Room toEntity() {
        Room room = new Room();
        room.setRoomId(this.roomId);
        room.setName(this.name);
        room.setBuilding(this.building);
        return room;
    }
}
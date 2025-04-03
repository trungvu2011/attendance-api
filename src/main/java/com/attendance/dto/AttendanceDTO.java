package com.attendance.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private UUID scheduleId;
    private String citizenId;
    private String imagePath;
    private Boolean faceMatch;
}
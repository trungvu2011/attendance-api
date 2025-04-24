package com.attendance.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private String ScheduleId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
}

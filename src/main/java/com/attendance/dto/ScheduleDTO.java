package com.attendance.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private UUID classId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

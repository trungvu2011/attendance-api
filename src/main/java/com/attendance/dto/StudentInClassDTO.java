package com.attendance.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentInClassDTO {
    private UUID studentId;
    private UUID classId;
}

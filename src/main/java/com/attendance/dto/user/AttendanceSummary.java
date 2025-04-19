package com.attendance.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummary {
    private long totalPresent;
    private long totalAbsent;
    private long totalLate;
}

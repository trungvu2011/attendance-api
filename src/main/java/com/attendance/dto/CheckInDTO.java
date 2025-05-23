package com.attendance.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDTO {
  private UUID candidateId;
  private UUID examId;
  private String citizenCardNumber;
  private LocalDateTime attendanceTime;
}

package com.attendance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID attendanceId;

    @ManyToOne
    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "studentId", referencedColumnName = "userId")
    private User student;

    private String citizenId;

    private Boolean faceMatch;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String imagePath;

    private java.time.LocalDateTime timestamp;

    public enum Status {
        PRESENT, ABSENT, LATE
    }
}

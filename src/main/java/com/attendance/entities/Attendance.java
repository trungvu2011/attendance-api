package com.attendance.entities;

import jakarta.persistence.*;
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
    @JoinColumn(name = "scheduleId")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "studentId")
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

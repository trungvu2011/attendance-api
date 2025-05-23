package com.attendance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ExamAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidateId", referencedColumnName = "userId")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "examId", referencedColumnName = "examID")
    private Exam exam;    private boolean citizenCardVerified;

    private boolean faceVerified;
    
    private LocalDateTime attendanceTime;
}

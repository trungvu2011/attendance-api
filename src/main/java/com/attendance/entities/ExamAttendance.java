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
public class ExamAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID attendanceId;

    @ManyToOne
    @JoinColumn(name = "candidateID", referencedColumnName = "candidateId")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "examId", referencedColumnName = "examId")
    private Exam exam;

    private boolean citizenCardVerified;

    private boolean faceVerified;
}

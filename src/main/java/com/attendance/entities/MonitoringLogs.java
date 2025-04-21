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
public class MonitoringLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidateId", referencedColumnName = "userId")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "examId", referencedColumnName = "examId")
    private Exam exam;

    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    private CameraEvent cameraEvent;
    
    private String faceUrl;
    
    public enum CameraEvent {
        OK,
        MULTIPLE_FACES,
        NOT_DETECTED
    }
}

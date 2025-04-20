package com.attendance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MonitoringLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID monitoringlogsId;

    @ManyToOne
    @JoinColumn(name = "candidateID", referencedColumnName = "candidateId")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "examId", referencedColumnName = "examId")
    private Exam exam;

    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    private CameraEvent cameraEvent;
    
    private URL imageURL;
    
    public enum CameraEvent {
        OK,
        MUTIPLE_FACE,
        NOT_DETECTED
    }
}

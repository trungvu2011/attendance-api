package com.attendance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID classId;

    private String className;
    
    private String description;
    
    private String academicYear;
    
    private String semester;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    private ClassStatus status;
    
    private Integer maxStudents;

    @ManyToOne
    @JoinColumn(name = "teacherId", referencedColumnName = "userId")
    private User teacher;
    
    public enum ClassStatus {
        ACTIVE,
        INACTIVE,
        COMPLETED,
        CANCELLED
    }
}

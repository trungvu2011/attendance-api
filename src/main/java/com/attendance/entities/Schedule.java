package com.attendance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @ManyToOne
    @JoinColumn(name = "classId")
    private  ClassRoom classRoom;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        UPCOMING, ONGOING, COMPLETED
    }
}

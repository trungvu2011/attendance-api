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
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID examId;

    private String name;

    private String subject;

    private String semester;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "roomId", referencedColumnName = "roomId")
    private Room room;
}

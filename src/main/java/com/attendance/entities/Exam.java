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
    private UUID examID;

    private String name;

    private String subject;

    private String semester;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleID")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "roomID", referencedColumnName = "roomID")
    private Room room;
}

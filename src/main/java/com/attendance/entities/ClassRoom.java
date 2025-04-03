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
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID classId;

    private String className;

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private User teacher;
}

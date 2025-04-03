package com.attendance.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CitizenCardData {

    @Id
    private UUID id;

    @Column(unique = true)
    private String citizenId;

    private String name;

    private String faceImage;

    private java.time.LocalDate dob;
}

package com.attendance.repositories;

import com.attendance.entities.CitizenCardData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CitizenCardRepository extends JpaRepository<CitizenCardData, UUID> {
    Optional<CitizenCardData> findByCitizenId(String citizenId);
}

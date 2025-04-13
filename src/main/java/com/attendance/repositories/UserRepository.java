package com.attendance.repositories;

import com.attendance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByCitizenId(String citizenId);

    Optional<User> findById(UUID userId);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}

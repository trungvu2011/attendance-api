package com.attendance.repositories;

import com.attendance.entities.StudentInClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentInClassRepository extends JpaRepository<StudentInClass, UUID> {
    List<StudentInClass> findByClassRoom_ClassId(UUID classId);
}

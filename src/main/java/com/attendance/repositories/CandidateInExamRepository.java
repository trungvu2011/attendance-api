package com.attendance.repositories;

import com.attendance.entities.CandidateInExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateInExamRepository extends JpaRepository<CandidateInExam, UUID> {
    List<CandidateInExam> findByCandidate_UserId(UUID candidateId);
    List<CandidateInExam> findByExam_ExamId(UUID examId);
    CandidateInExam findByCandidate_UserIdAndExam_ExamId(UUID candidateId, UUID examId);
}
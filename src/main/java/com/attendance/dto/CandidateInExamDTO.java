package com.attendance.dto;

import com.attendance.entities.CandidateInExam;
import com.attendance.entities.Exam;
import com.attendance.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateInExamDTO {
    private UUID id;
    private UUID candidateId;
    private String candidateName;
    private String candidateEmail;
    private UUID examId;
    private String examName;

    public static CandidateInExamDTO fromEntity(CandidateInExam candidateInExam) {
        CandidateInExamDTO dto = new CandidateInExamDTO();
        dto.setId(candidateInExam.getId());

        User candidate = candidateInExam.getCandidate();
        if (candidate != null) {
            dto.setCandidateId(candidate.getUserId());
            dto.setCandidateName(candidate.getName());
            dto.setCandidateEmail(candidate.getEmail());
        }

        Exam exam = candidateInExam.getExam();
        if (exam != null) {
            dto.setExamId(exam.getExamId());
            dto.setExamName(exam.getName());
        }

        return dto;
    }
}
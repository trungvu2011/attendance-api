package com.attendance.service;

import com.attendance.entities.CandidateInExam;
import com.attendance.entities.Exam;
import com.attendance.entities.User;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repositories.CandidateInExamRepository;
import com.attendance.repositories.ExamRepository;
import com.attendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CandidateInExamService {

    private final CandidateInExamRepository candidateInExamRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    @Autowired
    public CandidateInExamService(
            CandidateInExamRepository candidateInExamRepository,
            UserRepository userRepository,
            ExamRepository examRepository) {
        this.candidateInExamRepository = candidateInExamRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    public List<CandidateInExam> getAllCandidateInExams() {
        return candidateInExamRepository.findAll();
    }

    public Optional<CandidateInExam> getCandidateInExamById(UUID id) {
        return candidateInExamRepository.findById(id);
    }

    public List<CandidateInExam> getCandidatesByExamId(UUID examId) {
        return candidateInExamRepository.findByExam_ExamId(examId);
    }

    public List<CandidateInExam> getExamsByCandidate(UUID candidateId) {
        return candidateInExamRepository.findByCandidate_UserId(candidateId);
    }

    public CandidateInExam createCandidateInExam(UUID candidateId, UUID examId) {
        // Kiểm tra xem thí sinh đã được thêm vào kỳ thi này chưa
        CandidateInExam existing = candidateInExamRepository.findByCandidate_UserIdAndExam_ExamId(candidateId, examId);
        if (existing != null) {
            return existing; // Nếu đã có rồi thì trả về bản ghi hiện có
        }

        // Tìm thí sinh
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thí sinh với ID: " + candidateId));

        // Tìm kỳ thi
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kỳ thi với ID: " + examId));

        // Kiểm tra vai trò, chỉ cho phép người dùng có vai trò CANDIDATE
        if (candidate.getRole() != User.Role.CANDIDATE) {
            throw new IllegalArgumentException("Người dùng không phải là thí sinh");
        }

        // Tạo đối tượng CandidateInExam mới
        CandidateInExam candidateInExam = new CandidateInExam();
        candidateInExam.setCandidate(candidate);
        candidateInExam.setExam(exam);

        return candidateInExamRepository.save(candidateInExam);
    }

    public void deleteCandidateInExam(UUID id) {
        if (!candidateInExamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bản ghi với ID: " + id);
        }
        candidateInExamRepository.deleteById(id);
    }

    public void removeCandidateFromExam(UUID candidateId, UUID examId) {
        CandidateInExam candidateInExam = candidateInExamRepository.findByCandidate_UserIdAndExam_ExamId(candidateId,
                examId);
        if (candidateInExam == null) {
            throw new ResourceNotFoundException("Không tìm thấy thí sinh trong kỳ thi");
        }
        candidateInExamRepository.delete(candidateInExam);
    }
}
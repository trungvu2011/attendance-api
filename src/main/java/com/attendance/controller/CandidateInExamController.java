package com.attendance.controller;

import com.attendance.dto.CandidateInExamDTO;
import com.attendance.dto.CreateCandidateInExamDTO;
import com.attendance.entities.CandidateInExam;
import com.attendance.service.CandidateInExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/candidates-in-exam")
public class CandidateInExamController {

    private final CandidateInExamService candidateInExamService;

    @Autowired
    public CandidateInExamController(CandidateInExamService candidateInExamService) {
        this.candidateInExamService = candidateInExamService;
    }

    // Lấy tất cả các bản ghi thí sinh - kỳ thi
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CandidateInExamDTO>> getAllCandidateInExams() {
        List<CandidateInExam> records = candidateInExamService.getAllCandidateInExams();
        List<CandidateInExamDTO> dtos = records.stream()
                .map(CandidateInExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // Lấy chi tiết một bản ghi theo ID
    @GetMapping("/{id}")
    public ResponseEntity<CandidateInExamDTO> getCandidateInExamById(@PathVariable UUID id) {
        Optional<CandidateInExam> record = candidateInExamService.getCandidateInExamById(id);
        return record.map(value -> new ResponseEntity<>(CandidateInExamDTO.fromEntity(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Lấy danh sách thí sinh theo kỳ thi
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<CandidateInExamDTO>> getCandidatesByExamId(@PathVariable UUID examId) {
        List<CandidateInExam> records = candidateInExamService.getCandidatesByExamId(examId);
        List<CandidateInExamDTO> dtos = records.stream()
                .map(CandidateInExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // Lấy danh sách kỳ thi của một thí sinh
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CandidateInExamDTO>> getExamsByCandidate(@PathVariable UUID candidateId) {
        List<CandidateInExam> records = candidateInExamService.getExamsByCandidate(candidateId);
        List<CandidateInExamDTO> dtos = records.stream()
                .map(CandidateInExamDTO::fromEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // Thêm thí sinh vào kỳ thi
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CandidateInExamDTO> createCandidateInExam(@RequestBody CreateCandidateInExamDTO dto) {
        try {
            CandidateInExam createdRecord = candidateInExamService.createCandidateInExam(
                    dto.getCandidateId(), dto.getExamId());
            return new ResponseEntity<>(CandidateInExamDTO.fromEntity(createdRecord), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Xóa một bản ghi theo ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCandidateInExam(@PathVariable UUID id) {
        try {
            candidateInExamService.deleteCandidateInExam(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Xóa thí sinh khỏi kỳ thi
    @DeleteMapping("/candidate/{candidateId}/exam/{examId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeCandidateFromExam(
            @PathVariable UUID candidateId,
            @PathVariable UUID examId) {
        try {
            candidateInExamService.removeCandidateFromExam(candidateId, examId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
package com.attendance.controller;

import com.attendance.entities.MonitoringLogs;
import com.attendance.service.MonitoringLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringLogsController {

    private final MonitoringLogsService monitoringLogsService;

    @Autowired
    public MonitoringLogsController(MonitoringLogsService monitoringLogsService) {
        this.monitoringLogsService = monitoringLogsService;
    }

    @GetMapping
    public ResponseEntity<List<MonitoringLogs>> getAllLogs() {
        List<MonitoringLogs> logs = monitoringLogsService.getAllLogs();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<MonitoringLogs> getLogById(@PathVariable UUID logId) {
        Optional<MonitoringLogs> log = monitoringLogsService.getLogById(logId);
        return log.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<MonitoringLogs>> getLogsByExamId(@PathVariable UUID examId) {
        List<MonitoringLogs> logs = monitoringLogsService.getLogsByExamId(examId);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<MonitoringLogs>> getLogsByCandidateId(@PathVariable UUID candidateId) {
        List<MonitoringLogs> logs = monitoringLogsService.getLogsByCandidateId(candidateId);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createLog(
            @RequestParam UUID candidateId,
            @RequestParam UUID examId,
            @RequestParam MonitoringLogs.CameraEvent cameraEvent,
            @RequestParam String faceUrl) {
        
        try {
            MonitoringLogs log = monitoringLogsService.createLog(candidateId, examId, cameraEvent, faceUrl);
            return new ResponseEntity<>(log, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteLog(@PathVariable UUID logId) {
        Optional<MonitoringLogs> log = monitoringLogsService.getLogById(logId);
        if (log.isPresent()) {
            monitoringLogsService.deleteLog(logId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
package com.attendance.controller;

import com.attendance.config.DatabaseConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for system diagnostic information
 * Useful for monitoring the application's database connection status
 */
@RestController
@RequestMapping("/api/system")
public class SystemDiagnosticController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get database connection status and information
     * 
     * @return Database information including connection status, type, and version
     */
    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get database metadata
            String dbProductName = jdbcTemplate.queryForObject(
                    "SELECT DATABASE() as db", (rs, rowNum) -> rs.getString("db"));

            String dbVersion = jdbcTemplate.queryForObject(
                    "SELECT VERSION() as version", (rs, rowNum) -> rs.getString("version"));

            // Check if we're using H2 in-memory database
            boolean isUsingH2 = DatabaseConfig.isUsingInMemoryDatabase();

            // Build response
            response.put("status", "connected");
            response.put("databaseName", dbProductName != null ? dbProductName : "H2 in-memory");
            response.put("databaseVersion", dbVersion);
            response.put("usingFallbackDatabase", isUsingH2);

            if (isUsingH2) {
                response.put("warning",
                        "Using H2 in-memory database fallback. All data will be lost when the application restarts.");
                response.put("recommendation", "Please resolve MySQL connection issues for production use.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to get database status: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
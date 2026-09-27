package com.example.processor2.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.example.processor2.service.FileWriterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @GetMapping("/")
    public String dashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("generatedAt", Instant.now().toString());
        response.put("totalFiles", FileWriterService.STATS.size());
        response.put("files", FileWriterService.STATS);
        return ResponseEntity.ok(response);
    }
}

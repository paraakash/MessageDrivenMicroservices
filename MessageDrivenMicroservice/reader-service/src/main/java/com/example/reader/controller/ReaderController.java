package com.example.reader.controller;

import com.example.reader.service.CsvBatchSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reader")
public class ReaderController {

    private final CsvBatchSender csvBatchSender;

    public ReaderController(CsvBatchSender csvBatchSender) {
        this.csvBatchSender = csvBatchSender;
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerProcessing() {
        csvBatchSender.processCsvFiles();
        return ResponseEntity.ok("CSV batch processing triggered");
    }
}

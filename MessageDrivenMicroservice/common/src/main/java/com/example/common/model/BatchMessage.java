package com.example.common.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchMessage {
    private String sourceFile;
    private long batchNumber;
    private List<ReaderRecord> records;
    private Instant generatedAt;
}

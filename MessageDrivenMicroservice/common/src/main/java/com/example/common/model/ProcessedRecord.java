package com.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedRecord {
    private long id;
    private String name;
    private String city;
    private double amount;
    private double processedAmount;
    private String sourceFile;
    private long batchNumber;
    private String status;
    private String message;
}

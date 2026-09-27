package com.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderRecord {
    private long id;
    private String name;
    private String city;
    private double amount;
    private String sourceFile;
    private long batchNumber;
}

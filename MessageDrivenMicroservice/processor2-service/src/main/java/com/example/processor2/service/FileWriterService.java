package com.example.processor2.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.common.model.ProcessedBatch;
import com.example.common.model.ProcessedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileWriterService {

    private static final Logger log = LoggerFactory.getLogger(FileWriterService.class);
    public static final Map<String, FileStats> STATS = new ConcurrentHashMap<>();

    private final Path outputDirectory;

    public FileWriterService(@Value("${processor2.output-directory:data/output}") String outputDirectory) {
        this.outputDirectory = Path.of(outputDirectory);
    }

    public Map<String, FileStats> getStats() {
        return STATS;
    }

    @RabbitListener(queues = "q2", containerFactory = "q2ListenerContainerFactory")
    public void consumeProcessedBatch(ProcessedBatch batch) {
        try {
            Files.createDirectories(outputDirectory);
            String fileName = batch.getSourceFile().replace(".csv", "-processed.json");
            Path outputFile = outputDirectory.resolve(fileName);

            StringBuilder content = new StringBuilder();
            content.append("[\n");
            for (int i = 0; i < batch.getRecords().size(); i++) {
                ProcessedRecord record = batch.getRecords().get(i);
                content.append("  {")
                        .append("\"id\": ").append(record.getId()).append(", ")
                        .append("\"name\": \"").append(record.getName()).append("\", ")
                        .append("\"city\": \"").append(record.getCity()).append("\", ")
                        .append("\"amount\": ").append(record.getAmount()).append(", ")
                        .append("\"processedAmount\": ").append(record.getProcessedAmount()).append(", ")
                        .append("\"status\": \"").append(record.getStatus()).append("\", ")
                        .append("\"message\": \"").append(record.getMessage()).append("\"")
                        .append("}");
                if (i < batch.getRecords().size() - 1) {
                    content.append(",");
                }
                content.append("\n");
            }
            content.append("]\n");

            Files.writeString(outputFile, content);
            FileStats stats = STATS.computeIfAbsent(fileName, key -> new FileStats());
            stats.setFileName(fileName);
            stats.setRecordsWritten(batch.getRecords().size());
            stats.setBatchNumber(batch.getBatchNumber());
            stats.setCompleted(true);
            stats.setLastUpdated(System.currentTimeMillis());
            log.info("Wrote processed output file {} with {} records", fileName, batch.getRecords().size());
        } catch (IOException e) {
            log.error("Failed to write processed file for {}", batch.getSourceFile(), e);
        }
    }

    public static class FileStats {
        private String fileName;
        private int recordsWritten;
        private long batchNumber;
        private boolean completed;
        private long lastUpdated;

        public FileStats() {
        }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public int getRecordsWritten() { return recordsWritten; }
        public void setRecordsWritten(int recordsWritten) { this.recordsWritten = recordsWritten; }
        public long getBatchNumber() { return batchNumber; }
        public void setBatchNumber(long batchNumber) { this.batchNumber = batchNumber; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    }
}

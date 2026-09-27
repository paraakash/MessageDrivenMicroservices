package com.example.reader.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.example.common.config.QueueNames;
import com.example.common.model.BatchMessage;
import com.example.common.model.ReaderRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CsvBatchSender {

    private final RabbitTemplate rabbitTemplate;
    private final Path inputDirectory;
    private final Path outputDirectory;
    private final int batchSize;
    private final AtomicLong batchCounter = new AtomicLong(0);

    public CsvBatchSender(RabbitTemplate rabbitTemplate,
                         @Value("${reader.input-directory:data/input}") String inputDirectory,
                         @Value("${reader.output-directory:data/output}") String outputDirectory,
                         @Value("${reader.batch-size:10}") int batchSize) {
        this.rabbitTemplate = rabbitTemplate;
        this.inputDirectory = Path.of(inputDirectory);
        this.outputDirectory = Path.of(outputDirectory);
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${reader.poll-interval-ms:5000}")
    public void produceBatches() {
        processCsvFiles();
    }

    public void processCsvFiles() {
        try {
            Files.createDirectories(inputDirectory);
            Files.createDirectories(outputDirectory);

            List<Path> csvFiles = Files.list(inputDirectory)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".csv"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();

            if (csvFiles.isEmpty()) {
                return;
            }

            for (Path csvFile : csvFiles) {
                List<ReaderRecord> records = readCsv(csvFile);
                if (records.isEmpty()) {
                    continue;
                }

                for (int i = 0; i < records.size(); i += batchSize) {
                    List<ReaderRecord> batch = new ArrayList<>(records.subList(i, Math.min(i + batchSize, records.size())));
                    long batchNumber = batchCounter.incrementAndGet();
                    batch.forEach(record -> record.setBatchNumber(batchNumber));
                    BatchMessage message = new BatchMessage(csvFile.getFileName().toString(), batchNumber, batch, Instant.now());
                    rabbitTemplate.convertAndSend(QueueNames.EXCHANGE, QueueNames.Q1, message);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read CSV files", e);
        }
    }

    private List<ReaderRecord> readCsv(Path csvFile) throws IOException {
        List<ReaderRecord> records = new ArrayList<>();
        List<String> lines = Files.readAllLines(csvFile);

        if (lines.size() <= 1) {
            return records;
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isBlank()) {
                continue;
            }

            String[] tokens = line.split(",");
            if (tokens.length < 4) {
                continue;
            }

            ReaderRecord record = new ReaderRecord();
            record.setId(i);
            record.setName(tokens[0].trim());
            record.setCity(tokens[1].trim());
            record.setAmount(Double.parseDouble(tokens[2].trim()));
            record.setSourceFile(csvFile.getFileName().toString());
            record.setBatchNumber(0L);
            records.add(record);
        }

        return records;
    }
}

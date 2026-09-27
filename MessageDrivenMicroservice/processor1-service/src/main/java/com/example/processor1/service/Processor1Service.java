package com.example.processor1.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.example.common.config.QueueNames;
import com.example.common.model.BatchMessage;
import com.example.common.model.ProcessedBatch;
import com.example.common.model.ProcessedRecord;
import com.example.common.model.ReaderRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Processor1Service {

    private static final Logger log = LoggerFactory.getLogger(Processor1Service.class);

    private final RabbitTemplate rabbitTemplate;
    private final Executor asyncExecutor;

    public Processor1Service(RabbitTemplate rabbitTemplate,
                            @Qualifier("asyncExecutor") Executor asyncExecutor) {
        this.rabbitTemplate = rabbitTemplate;
        this.asyncExecutor = asyncExecutor;
    }

    @RabbitListener(queues = "q1", containerFactory = "simpleListenerContainerFactory")
    public void receiveBatch(BatchMessage message) {
        log.info("Received batch {} for file {} with {} records", message.getBatchNumber(), message.getSourceFile(), message.getRecords().size());

        List<CompletableFuture<ProcessedRecord>> futures = new ArrayList<>();
        for (ReaderRecord record : message.getRecords()) {
            futures.add(CompletableFuture.supplyAsync(() -> processRecord(record), asyncExecutor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .thenAccept(processed -> {
                    ProcessedBatch processedBatch = new ProcessedBatch(
                            message.getSourceFile(),
                            message.getBatchNumber(),
                            processed,
                            Instant.now()
                    );
                    rabbitTemplate.convertAndSend(QueueNames.EXCHANGE, QueueNames.Q2, processedBatch);
                    log.info("Published processed batch {} for file {} to q2", message.getBatchNumber(), message.getSourceFile());
                })
                .exceptionally(ex -> {
                    log.error("Failed to process batch {}", message.getBatchNumber(), ex);
                    return null;
                });
    }

    private ProcessedRecord processRecord(ReaderRecord record) {
        ProcessedRecord processedRecord = new ProcessedRecord();
        processedRecord.setId(record.getId());
        processedRecord.setName(record.getName());
        processedRecord.setCity(record.getCity());
        processedRecord.setAmount(record.getAmount());
        processedRecord.setSourceFile(record.getSourceFile());
        processedRecord.setBatchNumber(record.getBatchNumber());
        processedRecord.setProcessedAmount(Double.parseDouble(String.format("%.2f", record.getAmount() * 1.15)));
        processedRecord.setStatus("SUCCESS");
        processedRecord.setMessage("Processed after 15% uplift");
        return processedRecord;
    }
}

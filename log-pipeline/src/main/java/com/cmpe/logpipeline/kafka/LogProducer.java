package com.cmpe.logpipeline.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Service
public class LogProducer {
    private static final Logger logger = LoggerFactory.getLogger(LogProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String kafkaTopic;

    @Value("${log.file.path}")
    private String logFilePath;

    private long lastFileSize = 0;  // Tracks last read file position

    public LogProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000) // Reads and sends new logs every 5 seconds
    public void sendLogsToKafka() {
        File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            logger.warn("Log file not found: {}", logFilePath);
            return;
        }

        try (RandomAccessFile reader = new RandomAccessFile(logFile, "r")) {
            long fileLength = logFile.length();

            // If file was rotated or truncated, reset file offset
            if (fileLength < lastFileSize) {
                logger.warn("Log file was rotated or truncated. Resetting file offset.");
                lastFileSize = 0;
            }

            reader.seek(lastFileSize); // Move to last read position
            String logLine;
            while ((logLine = reader.readLine()) != null) {
                kafkaTemplate.send(kafkaTopic, logLine);
                logger.info("Sent log to Kafka: {}", logLine);
            }

            lastFileSize = reader.getFilePointer(); // Update last read position
        } catch (IOException e) {
            logger.error("Error reading log file", e);
        }
    }
}

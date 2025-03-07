package com.cmpe.logpipeline.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class ApacheLogGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ApacheLogGenerator.class);
    private static final Random random = new Random();

    private static final String[] STATUS_CODES = {"200", "404", "500", "403"};
    private static final String[] METHODS = {"GET", "POST", "PUT", "DELETE"};
    private static final String[] URLS = {"/home", "/login", "/dashboard", "/admin", "/favicon.ico"};
    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/537.36 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/537.36"
    };
    private static final String[] REFERRERS = {"-", "https://www.google.com/", "https://www.facebook.com/", "https://www.example.com/"};

    @Value("${log.file.path}")
    private String logFilePath;

    @Scheduled(fixedRate = 5000)  // Generates logs every 5 seconds
    public void generateLogs() {
        try {
            File logFile = new File(logFilePath);
            logFile.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(logFile, true)) {
                for (int i = 0; i < 5; i++) {  // Generate multiple log lines
                    String logEntry = generateApacheLog();
                    writer.write(logEntry + "\n");
                    logger.info("Generated log: " + logEntry);
                }

                // Inject an anomaly log randomly (1 in 10 chance)
                if (random.nextInt(10) == 0) {
                    String anomalyLog = generateSQLInjectionLog();
                    writer.write(anomalyLog + "\n");
                    logger.warn("Injected anomaly: " + anomalyLog);
                }
            }
        } catch (IOException e) {
            logger.error("Error writing to log file", e);
        }
    }

    private String generateApacheLog() {
        String ipAddress = getRandomIPAddress();
        String timestamp = getCurrentTimestamp();
        String method = METHODS[random.nextInt(METHODS.length)];
        String url = URLS[random.nextInt(URLS.length)];
        String statusCode = STATUS_CODES[random.nextInt(STATUS_CODES.length)];
        int bytesSent = random.nextInt(5000) + 500;
        String referrer = REFERRERS[random.nextInt(REFERRERS.length)];
        String userAgent = USER_AGENTS[random.nextInt(USER_AGENTS.length)];

        return String.format("%s - - [%s] \"%s %s HTTP/1.1\" %s %d \"%s\" \"%s\"",
                ipAddress, timestamp, method, url, statusCode, bytesSent, referrer, userAgent);
    }

    private String generateSQLInjectionLog() {
        String ipAddress = getRandomIPAddress();
        String timestamp = getCurrentTimestamp();
        String referrer = REFERRERS[random.nextInt(REFERRERS.length)];
        String userAgent = USER_AGENTS[random.nextInt(USER_AGENTS.length)];

        return String.format("%s - - [%s] \"GET /login?username=admin' OR '1'='1' -- HTTP/1.1\" 200 1024 \"%s\" \"%s\"",
                ipAddress, timestamp, referrer, userAgent);
    }

    private String getRandomIPAddress() {
        return random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss +0000"));
    }
}

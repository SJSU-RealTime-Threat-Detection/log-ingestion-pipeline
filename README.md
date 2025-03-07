# log-ingestion-pipeline

## Description
This project manages real-time log collection from various sources using Kafka for efficient log streaming.
It streams the logs to a downstream service [anomaly-data-preprocesssing](https://github.com/SJSU-RealTime-Threat-Detection/anomaly-data-preprocesssing) for ML pre-processing.

## Prerequisites
Before running the application, ensure you have:
- **Java 17+**
- **Apache Kafka**
- **Maven**
- **Spring Boot**

---

## Running the Project Locally

### **1. Start Zookeeper**
```sh
cd <KAFKA_HOME>
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```

### **2. Start Kafka Server**
```sh
cd <KAFKA_HOME>
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### **3. Verify Topic Creation**
```sh
cd <KAFKA_HOME>
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```
You should see:
```sh
apache-logs
```

### **4. Run the Spring Boot Application**
```sh
cd log-ingestion-pipeline
mvn spring-boot:run
```

## Configuration

Modify application.properties for custom Kafka settings:
```sh
spring.kafka.bootstrap-servers=localhost:9092
kafka.topic.name=apache-logs
log.file.path=[Insert file path]
```

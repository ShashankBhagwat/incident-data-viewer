package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;

public class SqsProcessorService {

    public static void process(JsonNode root) {

        root.get("Records").forEach(record -> {
            String body = record.get("body").asText();

            System.out.println("✅ Processing SQS Message: " + body);

            // - Parse message
            // - Call DB
            // - Store into Lightsail MySQL
        });

        System.out.println("✅ All SQS messages processed successfully");
    }
}

package com.feb.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//@Component - commenting for local testing without kafka container up 
public class KafkaConsumer {
	private final ObjectMapper objectMapper = new ObjectMapper();
	@KafkaListener(topics = "dbserver1.public.topic", groupId = "learning-group")
	public void listen(String message) {
		try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode payload = root.get("payload");
            
            if (payload != null && payload.has("after") && !payload.get("after").isNull()) {
                String op = payload.get("op").asText();
                JsonNode after = payload.get("after");
                
                String name = after.get("name").asText();
                System.out.println("Operation: " + op + " | Topic Name: " + name);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }
	
}

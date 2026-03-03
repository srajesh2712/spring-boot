package com.feb.demo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
	@Autowired
	private final KafkaTemplate<String, String> kafkaTemplate = null;

    
	
	public void sendMessage(String teamName, String messageValue) {
        // Use the KafkaTemplate send(topic, key, value) method
		kafkaTemplate.send("test-topic", teamName, messageValue);
        System.out.println(">>> Producer sent goal for: " + teamName);
    }
}

package com.feb.demo.controllers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class KStreamsQueryController {

	private final StreamsBuilderFactoryBean factoryBean;

	public KStreamsQueryController(StreamsBuilderFactoryBean factoryBean) {
		this.factoryBean = factoryBean;
	}

	@GetMapping("/data/{key}")
	public List<String> getData(@PathVariable String key) {
		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
		ReadOnlyWindowStore<String,Long> store = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType("goal-counts", 
				QueryableStoreTypes.windowStore()));
		Instant now = Instant.now();
	    Instant from = now.minus(Duration.ofMinutes(30));
	    
	    List<String> results = new ArrayList<>();

	    // 3. Use .fetch(key, timeFrom, timeTo) to get the iterator
	    try (WindowStoreIterator<Long> iterator = store.fetch(key, from, now)) {
	        while (iterator.hasNext()) {
	            KeyValue<Long, Long> next = iterator.next();
	            // next.key is the start timestamp of the window
	            results.add("Timestamp: " + next.key + " | Goals: " + next.value);
	        }
	    }
	    return results;
		
	}
	
	@GetMapping("/data/fetchall")
	public List<String> fetchAll(@RequestParam("fromMinutes") int fromMinutes, 
	        @RequestParam("toMinutes") int toMinutes) {
		KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
		ReadOnlyWindowStore<String,Long> store = kafkaStreams
				.store(StoreQueryParameters.fromNameAndType("goal-counts", 
				QueryableStoreTypes.windowStore()));
		 
	    List<String> results = new ArrayList<>();
		Instant now = Instant.now();
	    Instant from = now.minus(Duration.ofMinutes(fromMinutes));
	    Instant to = now.minus(Duration.ofMinutes(toMinutes));
	    // 3. Use .fetch(key, timeFrom, timeTo) to get the iterator
	    try (KeyValueIterator<Windowed<String>, Long> iterator = store.fetchAll(from, to)) {
	        while (iterator.hasNext()) {
	            KeyValue<Windowed<String>, Long> next = iterator.next();
	            // next.key is the start timestamp of the window
	            results.add("Timestamp: " + next.key+" "+next.key.key() +" "+next.key.window().start()+ " | Goals: " + next.value);
	        }
	    }
	    return results;
		
	}
	
	
}

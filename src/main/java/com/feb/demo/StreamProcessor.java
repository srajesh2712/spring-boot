package com.feb.demo;

import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StreamProcessor {

	@Bean
	public KStream<String, String> kStream(StreamsBuilder streamBuilder) {
		KStream<String, String> stream = streamBuilder.stream("test-topic",
				Consumed.with(Serdes.String(), Serdes.String())); // Consume as String/String
		stream.groupByKey(Grouped.with(Serdes.String(), Serdes.String()))

				.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1) //size
						,Duration.ofMinutes(10) //grace 
						))
				.count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("goal-counts")
					       .withKeySerde(Serdes.String())
					       .withValueSerde(Serdes.Long()))
				.toStream().peek((windowedKey, count) -> {
					System.out.println("MATCH UPDATE >> " + "Team: " + windowedKey.key() + " | Total Goals: " + count
							+ " | Window: " + windowedKey.window().startTime());
				})
				.to("match-results", 
			            Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class, 5 * 60 * 1000L), Serdes.Long()));

		return stream;
	}
}

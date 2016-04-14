package com.comcast.headwaters.kafka.monitoring;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import headwaters.core.MonitoringEvent;

/**
 * This is an example of the Headwaters data consumer code. It is kept to its
 * most simple form in order to demonstrate the core feature of the program. For
 * instance, we hard-coded the configuration settings (which obviously is not
 * ideal). <br />
 * It uses as an example the {@link MonitoringEvent}, chosen here again for
 * simplicity. But it works the same way for any schema defined in Headwaters.
 */

public class Consumer implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);
	private KafkaConsumer<String, MonitoringEvent> consumer;
	private final E2emonitoringConfig config;
	 

	/**
	 * Builds and configure the Kafka Consumer
	 * 
	 * @param topic
	 *            The name of the topic to consume data to
	 */
	public Consumer(final E2emonitoringConfig config) {
		consumer = new KafkaConsumer<>(config.getProducerConsumerConfig());
		this.config = config;
		

	}

	/**
	 * Starts the producer, which emits a new Monitoring event every second. The
	 * value conveyed in the monitoring event is the time (in microseconds) it
	 * took to send the previous monitoring event.
	 */
	@Override
	public void run() {
		LOG.info("Starting Consumer.");
		long SendPrevious = 0;
		long RoundtripTime = 0;
		// Consuming the data from Kafka. The Kafka record contains the topic,
		// the key and the value, using the type defined
		// in the configuration in the constructor above. So in this case, the
		// key is a String, and the value a
		// MonitoringEvent.
		consumer.subscribe(Arrays.asList(config.getKafkaTopic()));
		try {
			while (true) {
				final long startTime = System.currentTimeMillis();
				ConsumerRecords<String, MonitoringEvent> records = consumer.poll(100);
				final long endTime = System.currentTimeMillis();
				for (ConsumerRecord<String, MonitoringEvent> record :records ) {
					SendPrevious = (endTime - startTime);
					SdpMonitoringStats.get().addConsumeTime(SendPrevious);
					RoundtripTime = (endTime - record.value().getHeader().getTimestamp().getValue());
					if ("kafka".equals(record.value().getMonitoringEventId())) {
					SdpMonitoringStats.get().addRoundtripTime(RoundtripTime);
					} else if ("Http".equals(record.value().getMonitoringEventId())){
					SdpMonitoringStats.get().addHttproundtripTime(RoundtripTime);
					}
					LOG.debug("Kafka roundtrip Time " + RoundtripTime + "milliseconds" + record.value());
				}
			}
		} catch (Exception e) {
			LOG.warn("Fatal Error: ", e);
		}
	}

	/*
	 * Stops Consumer
	 */
	public void stop() {
		try {
			consumer.close();
		} catch (Exception e) {
			LOG.warn("Failed to close kafka consumer", e);
		}
	}

}

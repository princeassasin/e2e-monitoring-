package com.comcast.headwaters.kafka.monitoring;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.headwaters.kafka.monitoring.E2emonitoringConfig;

public class run {
	public static final Logger LOG = LoggerFactory.getLogger(run.class);

	public static void main(String[] args) throws IOException {

		/**
		 * This is the entry point to run the producer & consumer code.
		 * 
		 * @param args
		 *            Program argument (ignored)
		 */
		final E2emonitoringConfig configuration = new E2emonitoringConfig();
	    if (!configuration.build(args)) {
	      configuration.printHelp();
	      return;
	    }
	    final Producer producer = new Producer(configuration);
	    final Consumer consumer = new Consumer(configuration);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void start() {
				producer.stop();
				consumer.stop();
			}
		});
		final Thread producerThread = new Thread(producer);
		final Thread consumerThread = new Thread(consumer);

		producerThread.start();
		consumerThread.start();
		
		try {
			producerThread.join();
			consumerThread.join();
		} catch (InterruptedException e) {
			LOG.info("Shutting down.");
		}
	}
}

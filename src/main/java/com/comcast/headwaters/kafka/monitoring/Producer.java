package com.comcast.headwaters.kafka.monitoring;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;

import headwaters.CoreHeader;
import headwaters.core.MonitoringEvent;
import headwaters.datatype.Timestamp;
import headwaters.datatype.UUID;

/**
 * This is an example of the Headwaters data producer code. It is kept to its most simple form in order to demonstrate
 * the core feature of the program. For instance, we hard-coded the configuration settings (which obviously is not
 * ideal). <br />
 * It uses as an example the {@link MonitoringEvent}, chosen here again for simplicity. But it works the same way for
 * any schema defined in Headwaters.
 */
public  class Producer implements Runnable{
  private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
  private KafkaProducer<String, MonitoringEvent> producer;
  private final E2emonitoringConfig config;
  public final String monitoringId;
  public final String hostname;
  

  
  /**
   * Builds and configure the Kafka Producer
   * 
   * @param topic
   *          The name of the topic to produce data to
   */
  public Producer(final E2emonitoringConfig config) {
	  producer = new KafkaProducer<>(config.getProducerConsumerConfig());
		this.config = config;
		monitoringId = "LabWeekDemo-";
		 String thisHostname = "";
		    try {
		      thisHostname = InetAddress.getLocalHost().getHostName();
		    } catch (UnknownHostException e) {
		      thisHostname = "unknown";
		    }
		    hostname = thisHostname;
  }

  /**
   * Starts the producer, which emits a new Monitoring event every second. The value conveyed in the monitoring event is
   * the time (in microseconds) it took to send the previous monitoring event.
   */
  @Override
  public void run() {
      LOG.info("Starting Producer");
      long sendPreviousTime = 0;
      while (true) {
        try {
          final long startTime = System.currentTimeMillis();
          // Sending the data to Kafka. The Kafka record contains the topic, the key and the value, using the type defined
          // in the configuration in the constructor above. So in this case, the key is a String, and the value a
          // MonitoringEvent.
          producer.send(new ProducerRecord<>( config.getKafkaTopic(),String.valueOf(System.currentTimeMillis()), createMonitoringEvent(sendPreviousTime, "kafka")));
          final long endTime = System.currentTimeMillis();
          sendPreviousTime = (endTime - startTime);
          SdpMonitoringStats.get().addSendTime(sendPreviousTime);
          LOG.debug("Sent message in " + sendPreviousTime + " microseconds");
        } catch (Exception e) {
          sendPreviousTime = -1;
          LOG.warn("Error while sending monitoring event: ", e);
        }
        try {
            final long startTime = System.currentTimeMillis();
            // Sending the data to Http-Collector, in the configuration-file the URL for Http-collector is provided 
            HttpPost post = new HttpPost(config.getHttpCollector());
            post.setHeader(HttpHeaders.CONTENT_TYPE, "text/json");
            post.setEntity(EntityBuilder.create().setText(createMonitoringEvent(startTime, "Http").toString()).build());
            final CloseableHttpResponse result = HttpClientBuilder.create().build().execute(post);
            LOG.debug("Received {} from HTTP Collector", result.getStatusLine().getStatusCode());
            result.close();
            final long endTime = System.currentTimeMillis();
            sendPreviousTime = (endTime - startTime);
            SdpMonitoringStats.get().addHttpSendTime(sendPreviousTime);
            LOG.debug("Sent Http-collector message in " + sendPreviousTime + " microseconds");
            
          } catch (Exception e) {
            sendPreviousTime = -1;
            LOG.warn("Error while sending Http-collector event: ", e);
          }
        
        try {
        	Thread.sleep(config.getThreadSleep());
        } catch (InterruptedException e) {
        	LOG.debug("", e);
        }
        
      }
  	
  }

  /**
   * Stops the producer
   */
  public void stop() {
    try {
      producer.close();
    } catch (Exception e) {
      LOG.warn("Failed to close kafka producer", e);
    }
  }

  /**
   * Creates a new monitoring event
   * 
   * @param totalStartime
   *          The context value in this case is the time (in microseconds) it took to send the previous message
   * @return The newly created monitoring event
   */
  public MonitoringEvent createMonitoringEvent(final long totalStartime, String destination) {
    final CoreHeader header = new CoreHeader();
    header.setHostname(hostname);
    header.setTimestamp(new Timestamp(System.currentTimeMillis()));
    header.setUuid(new UUID(java.util.UUID.randomUUID().toString()));
    final MonitoringEvent event = new MonitoringEvent();
    event.setMonitoringEventId(monitoringId+destination);
    event.setHeader(header);
    event.setContextValue(totalStartime);
    return event;
  
  }

}

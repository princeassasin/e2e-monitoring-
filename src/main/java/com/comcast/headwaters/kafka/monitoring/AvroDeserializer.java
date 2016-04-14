package com.comcast.headwaters.kafka.monitoring;

import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.comcast.headwaters.serde.AvroSerDe;
import com.comcast.headwaters.serde.AvroSerDeBuilder;
import headwaters.core.MonitoringEvent;


public class AvroDeserializer implements Deserializer<MonitoringEvent> {
	private AvroSerDe deserializer = null;
	private static final Logger LOG = LoggerFactory.getLogger(AvroDeserializer.class);

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> config, boolean isKey) {
		if (isKey) {
			return;
		}
		try {
			
			final AvroSerDeBuilder builder = new AvroSerDeBuilder().setSchema(MonitoringEvent.SCHEMA$);
			deserializer = builder.build();
		}
			catch (Exception e) {
			LOG.warn("Failed to create avro serde for " ,e);
			deserializer = null;
		}
	}

	@Override
	public MonitoringEvent deserialize(String topic, byte[] data) {
		
		try {
			return deserializer.deserializeSpecific(data);
		} catch (IOException e) {
			LOG.warn("Error while Deserializing message: ", e);

			return null;

		}
	}
	
	
}
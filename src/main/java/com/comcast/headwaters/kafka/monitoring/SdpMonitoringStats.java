package com.comcast.headwaters.kafka.monitoring;

import java.lang.management.ManagementFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import com.comcast.ccp.realtime.value.RealtimeValue;
import com.comcast.ccp.realtime.value.RealtimeValueBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdpMonitoringStats implements SdpMonitoringStatsMBean{

	private static final Logger LOG = LoggerFactory.getLogger(SdpMonitoringStats.class);
	private static final int DEFAULT_RT_GRANULARITY = 500;
	private static final int MAX_STATS_PERIOD = 60;
	public static final int DEFAULT_STAT_PERIOD = 10;
	private final RealtimeValue<Long> msgSendTime;	
	private final RealtimeValue<Long> msgHttpSendTime;	
	private final RealtimeValue<Long> msgConsummeTime;
	private final RealtimeValue<Long> msgRoundtripTime;
	private final RealtimeValue<Long> msgHttproundtripTime;
	public SdpMonitoringStats(){
		msgSendTime = new RealtimeValueBuilder<Long>().setThreadSafe(true).setGranularity(DEFAULT_RT_GRANULARITY).setMaxTimespan(MAX_STATS_PERIOD).build();
		msgHttpSendTime = new RealtimeValueBuilder<Long>().setThreadSafe(true).setGranularity(DEFAULT_RT_GRANULARITY).setMaxTimespan(MAX_STATS_PERIOD).build();
		msgConsummeTime = new RealtimeValueBuilder<Long>().setThreadSafe(true).setGranularity(DEFAULT_RT_GRANULARITY).setMaxTimespan(MAX_STATS_PERIOD).build();
		msgRoundtripTime = new RealtimeValueBuilder<Long>().setThreadSafe(true).setGranularity(DEFAULT_RT_GRANULARITY).setMaxTimespan(MAX_STATS_PERIOD).build();
		msgHttproundtripTime = new RealtimeValueBuilder<Long>().setThreadSafe(true).setGranularity(DEFAULT_RT_GRANULARITY).setMaxTimespan(MAX_STATS_PERIOD).build();
	}
	
	
	  private static SdpMonitoringStats instance;
	  private static boolean isRegistered = false;

	  public static SdpMonitoringStats get() {
	    synchronized (SdpMonitoringStats.class) {
	      if (instance == null) {
	        instance = new SdpMonitoringStats();
	      }
	      if (isRegistered == false) {
	        try {
	          ManagementFactory.getPlatformMBeanServer().registerMBean(instance, getObjectName());
	          isRegistered = true;
	          LOG.info("Registered JMX object: {} ", getObjectName());
	        } catch (final Exception e) {
	          LOG.error("Failed to register JMX statistics", e);
	        }
	      }
	    }
	    return instance;
	  }

	  public static void clear() {
	    synchronized (SdpMonitoringStats.class) {
	      if (instance != null && isRegistered) {
	        try {
	          ManagementFactory.getPlatformMBeanServer().unregisterMBean(getObjectName());
	          isRegistered = false;
	          LOG.info("Unregistered JMX object: {} ", getObjectName());
	        } catch (final Exception e) {
	          LOG.error("Failed to unregister JMX statistics", e);
	        }
	      }
	      instance = null;
	    }
	  }

	  private static ObjectName getObjectName() throws MalformedObjectNameException {
	    return new ObjectName("com.comcast.headwaters.kafka.monitoring:type=SdpMonitoringStats");
	  }
	  	  
	  public void addSendTime(final long nbMicroSec) {
		    msgSendTime.addValue(nbMicroSec);
		  }
	  public void addHttpSendTime(final long nbMicroSec) {
		  msgHttpSendTime.addValue(nbMicroSec);
		  }
	  public void addConsumeTime(final long nbMicroSec) {
		    msgConsummeTime.addValue(nbMicroSec);
		  }
	  public void addRoundtripTime(final long nbMicroSec) {
		    msgRoundtripTime.addValue(nbMicroSec);
		  }
	  public void addHttproundtripTime(final long nbMicroSec) {
		    msgHttproundtripTime.addValue(nbMicroSec);
		  }
		@Override
	public Long getMinSendDuration() {
		return msgSendTime.getMin(DEFAULT_STAT_PERIOD);
	}
	@Override
	public Long getMaxSendDuration() {
		return msgSendTime.getMax(DEFAULT_STAT_PERIOD);
	}
	@Override
	public Double getAvgSendDuration() {
		return msgSendTime.getAvg(DEFAULT_STAT_PERIOD);
	}
	@Override
	public Long getMinConsumeDuration() {
		return msgConsummeTime.getMin(DEFAULT_STAT_PERIOD);
		
	}

	@Override
	public Long getMinHttpSendDuration() {
		return msgHttpSendTime.getMin(DEFAULT_STAT_PERIOD);
	}

	@Override
	public Long getMaxHttpSendDuration() {
		return msgHttpSendTime.getMax(DEFAULT_STAT_PERIOD);
	}

	@Override
	public Double getAvgHttpSendDuration() {
		return msgHttpSendTime.getAvg(DEFAULT_STAT_PERIOD);
	}

	@Override
	public Long getMaxConsumeDuration() {
		return msgConsummeTime.getMax(DEFAULT_STAT_PERIOD);
		
	}

	@Override
	public Double getAvgConsumeDuration() {
		return msgConsummeTime.getAvg(DEFAULT_STAT_PERIOD);
	}

	@Override
	public Long getMinRoundtripTime() {
		return msgRoundtripTime.getMin(DEFAULT_STAT_PERIOD);
	}

	@Override
	public Long getMaxRoundtripTime() {
		return msgRoundtripTime.getMax(DEFAULT_STAT_PERIOD);
	}

	@Override
	public Double getAvgRoundtripTime() {
		return msgRoundtripTime.getAvg(DEFAULT_STAT_PERIOD);
	}
	@Override
	public Long getMinHttproundtripTime() {
		return msgHttproundtripTime.getMin(DEFAULT_STAT_PERIOD);
	}
	
	@Override
	public Long getMaxHttproundtripTime() {
		return msgHttproundtripTime.getMax(DEFAULT_STAT_PERIOD);
	}
	
	@Override
	public Double getAvgHttproundtripTime() {
		return msgHttproundtripTime.getAvg(DEFAULT_STAT_PERIOD);
	}

	  private static final String NEW_LINE = "\n";

	  @Override
	  public String toString() {
	    final StringBuilder builder = new StringBuilder();

	    // Start with a new line so that all the rows are formatted similarly.
	    builder.append(NEW_LINE);

	    // Create a row for processing time
	    builder.append(", Send:");
	    builder.append(getAvgSendDuration());
	    builder.append(NEW_LINE);

	    return builder.toString();
	  }



}

package com.comcast.headwaters.kafka.monitoring;



public interface SdpMonitoringStatsMBean {
	
	  // Average processing time in microseconds over the last 10 seconds
	  Long getMinSendDuration();

	  Long getMaxSendDuration();

	  Double getAvgSendDuration();
	  
	  Long getMinHttpSendDuration();
	  
	  Long getMaxHttpSendDuration();
	  
	  Double getAvgHttpSendDuration();
	  
	  Long getMinConsumeDuration();

	  Long getMaxConsumeDuration();

	  Double getAvgConsumeDuration();
	  
	  Long getMinRoundtripTime();
	  
	  Long getMaxRoundtripTime();
	  
	  Double getAvgRoundtripTime();
	 
	  Long getMinHttproundtripTime();
	  
	  Long getMaxHttproundtripTime();
	  
	  Double getAvgHttproundtripTime();
}

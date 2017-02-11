package com.kappaware.kfaker.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;


public class ProducerPropertiesHelper {

	// @formatter:off
	@SuppressWarnings("deprecation")
	static Set<String> validProducerProperties = new HashSet<String>(Arrays.asList(new String[] { 
		ProducerConfig.MAX_BLOCK_MS_CONFIG, 
		ProducerConfig.METADATA_FETCH_TIMEOUT_CONFIG, 
		ProducerConfig.METADATA_MAX_AGE_CONFIG, 
		ProducerConfig.BATCH_SIZE_CONFIG, 
		ProducerConfig.BUFFER_MEMORY_CONFIG, 
		ProducerConfig.ACKS_CONFIG, 
		ProducerConfig.TIMEOUT_CONFIG, 
		ProducerConfig.LINGER_MS_CONFIG, 
		ProducerConfig.SEND_BUFFER_CONFIG, 
		ProducerConfig.RECEIVE_BUFFER_CONFIG, 
		ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 
		ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 
		ProducerConfig.BLOCK_ON_BUFFER_FULL_CONFIG, 
		ProducerConfig.RETRIES_CONFIG, 
		ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 
		ProducerConfig.COMPRESSION_TYPE_CONFIG, 
		ProducerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, 
		ProducerConfig.METRICS_NUM_SAMPLES_CONFIG, 
		ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, 
		ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 
		ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 
		ProducerConfig.PARTITIONER_CLASS_CONFIG, 
		ProducerConfig.MAX_BLOCK_MS_CONFIG, 
		ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 
		ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
		ProducerConfig.CLIENT_ID_CONFIG, 
		CommonClientConfigs.SECURITY_PROTOCOL_CONFIG
	}));

	static Set<String> protectedProducerProperties = new HashSet<String>(Arrays.asList(new String[] { 
		ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
		ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
		ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
	}));
	// @formatter:on

	public static Properties buildProperties(List<String> propertyStrings, String brokers, boolean forceProperties) throws ConfigurationException {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

		for (String prop : propertyStrings) {
			String[] prp = prop.trim().split("=");
			if (prp.length != 2) {
				throw new ConfigurationException(String.format("Property must be as name=value. Found '%s'", prop));
			}
			String propName = prp[0].trim();
			if (forceProperties) {
				properties.put(propName, prp[1].trim());
			} else {
				if (validProducerProperties.contains(propName)) {
					properties.put(propName, prp[1].trim());
				} else if (protectedProducerProperties.contains(propName)) {
					throw new ConfigurationException(String.format("Usage of target property '%s' is reserved by this application!", propName));
				} else {
					throw new ConfigurationException(String.format("Invalid target property '%s'!", propName));
				}
			}
		}
		// If topic is not present, fail in 2 sec
		if (!properties.containsKey(ProducerConfig.MAX_BLOCK_MS_CONFIG)) {
			properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 2000);
		}
		return properties;
	}
}

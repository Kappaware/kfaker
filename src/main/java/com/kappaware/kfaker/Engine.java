/*
 * Copyright (C) 2017 BROADSoftware
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kappaware.kfaker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;
import com.github.javafaker.Faker;
import com.kappaware.kfaker.config.Configuration;
import com.kappaware.kfaker.config.ConfigurationException;

/**
 * This class generate kafka message of the form
 * <pre>
 * <code>
 * {
 *   
 * }  
 * </code>
 * </pre>   
 * @author Serge ALEXANDRE
 *
 */

public class Engine extends Thread {
	static Logger log = LoggerFactory.getLogger(Engine.class);

	private boolean running = true;
	private Configuration config;
	private KafkaProducer<String, String> producer;
	private JSON json;
	private long counter;
	private Map<String, Long> counterByRecipient = new HashMap<String, Long>();
	private Faker faker;

	Engine(Configuration config) throws ConfigurationException {
		this.config = config;
		Properties properties = config.getProducerProperties();
		if(!properties.containsKey(ProducerConfig.CLIENT_ID_CONFIG)) {
			properties.put(ProducerConfig.CLIENT_ID_CONFIG, config.getSender());
		}
		this.producer = new KafkaProducer<String, String>(properties, new StringSerializer(), new StringSerializer());
		this.json = JSON.std.without(JSON.Feature.PRETTY_PRINT_OUTPUT);
		this.counter = this.config.getInitialCounter();
		// Test target topic
		try {
			producer.partitionsFor(this.config.getTopic()).size();
		} catch (Exception e) {
			producer.close();
			throw new ConfigurationException(String.format("Unable to access topic '%s': %s.", this.config.getTopic(), e.toString()));
		}
		Random random = new Random(100);
		faker = new Faker(random);

	}

	abstract class MyCallback implements Callback {
		ProducerRecord<String, String> record;

		MyCallback(ProducerRecord<String, String> record) {
			this.record = record;
		}
	}

	@Override
	public void run() {
		while (running) {
			for (int i = 0; i < this.config.getBurstCount(); i++) {
				Message message = new Message();
				message.setRecipient(Recipients.getRecipient(this.counter));
				message.setSender(this.config.getSender());
				message.setOverallCounter(this.counter);
				Long rc = this.counterByRecipient.get(message.getRecipient());
				if (rc == null) {
					rc = 0L;
				}
				message.setRecipientCounter(rc);
				this.counterByRecipient.put(message.getRecipient(), rc + 1);
				message.setTimestamp(System.currentTimeMillis());
				message.setBody(faker.chuckNorris().fact());
				String m;
				try {
					m = json.asString(message);
				} catch (IOException e) {
					log.error(String.format("Unable to generate a JSON from an obkect of class '%s'", message.getClass().getCanonicalName()));
					break;
				}
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.config.getTopic(), message.getRecipient(), m);
				producer.send(record, new MyCallback(record) {
					@Override
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						//stats.addToProducerStats(this.record.key().getBytes(), metadata.partition(), metadata.offset());
						if (exception != null) {
							log.error(String.format("onCompletion()"), exception);
						} else {
							if (config.isMesson()) {
								log.info(String.format("part=%d offset=%d, timestamp=%s, key='%s', value='%s'", metadata.partition(), metadata.offset(), Misc.printIsoDateTime(record.timestamp()), record.key(), record.value()));
							}
						}
					}
				});
				counter++;
			}
			try {
				Thread.sleep(this.config.getPeriod());
			} catch (InterruptedException e) {
				log.debug("Interrupted in normal sleep!");
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// This case is normal in case of period = 0. In such case, interrupted flag was not cleared
			log.debug("Interrupted in end of thread processing!");
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			log.warn("Too many interruption in end of thread processing!");
		}
		this.producer.flush();
		this.producer.close();
		log.info(String.format("Next counter:%d", this.counter));
	}

	void halt() {
		this.running = false;
		this.interrupt();
		try {
			this.join();
		} catch (InterruptedException e) {
			log.error(String.format("Too many InteruptedException in shudown of engine"));
		}
	}
}

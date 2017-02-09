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

package com.kappaware.kfaker.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Parameters {
	static Logger log = LoggerFactory.getLogger(Parameters.class);

	private String brokers;
	private String topic;
	private List<String> properties;
	private boolean forceProperties;
	
	private boolean messon;

	private long initialCounter;
	private int burstCount;
	private long period;
	private String sender;

	static OptionParser parser = new OptionParser();
	static {
		parser.formatHelpWith(new BuiltinHelpFormatter(120,2));
	}

	static OptionSpec<String> BROKERS_OPT = parser.accepts("brokers", "Comma separated values of Target Kafka brokers").withRequiredArg().describedAs("br1:9092,br2:9092").ofType(String.class).required();
	static OptionSpec<String> TOPIC_OPT = parser.accepts("topic", "Target topic").withRequiredArg().describedAs("topic").ofType(String.class).required();
	static OptionSpec<String> PROPERTY_OPT = parser.accepts("property", "Producer property (May be specified several times)").withRequiredArg().describedAs("prop=val").ofType(String.class);
	static OptionSpec<?> FORCE_PROPERTIES_OPT = parser.accepts("forceProperties", "Force unsafe properties");

	static OptionSpec<?> MESSON_OPT = parser.accepts("messon", "Display all generated messages");

	static OptionSpec<Long> INITIAL_COUNTER_OPT = parser.accepts("initialCounter", "Initial counter value").withRequiredArg().describedAs("counter").ofType(Long.class).defaultsTo(0L);
	static OptionSpec<Integer> BURST_COUNT_OPT = parser.accepts("burstCount", "Burst count").withRequiredArg().describedAs("count").ofType(Integer.class).defaultsTo(1);
	static OptionSpec<Long> PERIOD_OPT = parser.accepts("period", "Period between two bursts (ms)").withRequiredArg().describedAs("period(ms)").ofType(Long.class).defaultsTo(1000L);
	static OptionSpec<String> SENDER_OPT = parser.accepts("sender", "Sender of the message").withRequiredArg().describedAs("sender name").ofType(String.class).defaultsTo("Chuck NORRIS");
	
	@SuppressWarnings("serial")
	private static class MyOptionException extends Exception {
		public MyOptionException(String message) {
			super(message);
		}
	}

	
	public Parameters(String[] argv) throws ConfigurationException {
		try {
			OptionSet result = parser.parse(argv);

			if (result.nonOptionArguments().size() > 0 && result.nonOptionArguments().get(0).toString().trim().length() > 0) {
				throw new MyOptionException(String.format("Unknow option '%s'", result.nonOptionArguments().get(0)));
			}
			// Mandatories parameters
			this.brokers = result.valueOf(BROKERS_OPT);
			this.topic = result.valueOf(TOPIC_OPT);
			this.properties = result.valuesOf(PROPERTY_OPT);
			this.forceProperties = result.has(FORCE_PROPERTIES_OPT);
			this.sender = result.valueOf(SENDER_OPT);
			
			this.messon = result.has(MESSON_OPT);

			this.initialCounter = result.valueOf(INITIAL_COUNTER_OPT);
			this.burstCount = result.valueOf(BURST_COUNT_OPT);
			this.period = result.valueOf(PERIOD_OPT);
		} catch (OptionException | MyOptionException t) {
			throw new ConfigurationException(usage(t.getMessage()));
		}
	}

	private static String usage(String err) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		if (err != null) {
			pw.print(String.format("\n\n * * * * * ERROR: %s\n\n", err));
		}
		try {
			parser.printHelpOn(pw);
		} catch (IOException e) {
		}
		pw.flush();
		pw.close();
		return baos.toString();
	}

	// --------------------------------------------------------------------------


	public String getBrokers() {
		return brokers;
	}


	public String getTopic() {
		return topic;
	}


	public List<String> getProperties() {
		return properties;
	}

	public boolean isForceProperties() {
		return forceProperties;
	}

	public long getInitialCounter() {
		return initialCounter;
	}

	public int getBurstCount() {
		return burstCount;
	}

	public long getPeriod() {
		return period;
	}

	public boolean isMesson() {
		return messon;
	}

	public String getSender() {
		return sender;
	}


}

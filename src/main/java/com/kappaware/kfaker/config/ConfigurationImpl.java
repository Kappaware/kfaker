/*
 * Copyright (C) 2016 BROADSoftware
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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ConfigurationImpl implements Configuration {
	static Logger log = LoggerFactory.getLogger(ConfigurationImpl.class);

	
	// @formatter:on

	private Parameters parameters;
	private Properties producerProperties;

	public ConfigurationImpl(Parameters parameters) throws ConfigurationException {
		this.parameters = parameters;
		this.producerProperties = ProducerPropertiesHelper.buildProperties(this.parameters.getProperties(), this.parameters.getBrokers(), this.parameters.isForceProperties());
	}

	// ----------------------------------------------------------


	@Override
	public String getTopic() {
		return parameters.getTopic();
	}

	@Override
	public Properties getProducerProperties() {
		return this.producerProperties;
	}

	@Override
	public long getInitialCounter() {
		return this.parameters.getInitialCounter();
	}

	@Override
	public int getBurstCount() {
		return parameters.getBurstCount();
	}

	@Override
	public long getPeriod() {
		return parameters.getPeriod();
	}

	
	@Override
	public boolean isMesson() {
		return parameters.isMesson();
	}

	@Override
	public String getSender() {
		return parameters.getSender();
	}


}

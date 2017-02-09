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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kappaware.kfaker.config.Configuration;
import com.kappaware.kfaker.config.ConfigurationException;
import com.kappaware.kfaker.config.ConfigurationImpl;
import com.kappaware.kfaker.config.Parameters;


public class Main {
	static Logger log = LoggerFactory.getLogger(Main.class);

	static public void main(String[] argv) throws Exception {

		log.info("kfaker start");

		try {
			Configuration config = new ConfigurationImpl(new Parameters(argv));
			Engine engine = new Engine(config);
			

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					log.debug("Shutdown hook called!");
					engine.halt();
					log.info("kfaker end");
					Misc.sleep(100); // To let message to be drained
				}
			});
			engine.start();
		} catch (ConfigurationException e) {
			log.error(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			log.error("Error on launch!", e);
			System.exit(2);
		}
	}
	
}

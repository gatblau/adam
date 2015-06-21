/**
 * Copyright (c) 2015 GATBLAU - www.gatblau.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gatblau.adam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * Utility class to read producer configuration and perform assertions.
 */
public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    public static final String BROKER_URI_KEY = "uri";
    public static final String EVENT_QUEUE_NAME = "queue";
    public static final String SYSTEM_NAME_KEY = "service";
    public static final String NODE_NAME_KEY = "node";
    private final Properties config = getConfig();
    private final DateFormatter dateFormat = new DateFormatter("dd.MM.yy.HH.mm.ss.SSS");
    private boolean active = true;

    public boolean isActive() {
       return active;
    }

    private Properties getConfig() {
        Properties prop = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream("/adam.properties");
            prop.load(in);
            in.close();
        }
        catch (Exception e) {
            // if the configuration file is not found, the publisher is disabled
            logger.info("Adam configuration file not found. Disabling event publishing.");
            active = false;
        }
        return prop;
    }

    private String getConfigValue(String key) {
        String value = config.getProperty(key);
        if (value == null || value.length() == 0) {
            logger.error(String.format("Property '%s' not found in adam.properties file. \r\n" +
                "Cannot create a connection with the broker. \r\n" +
                "Check that the property exists and has the correct value.", key));
        }
        return value;
    }

    void handle(String message, Exception ex) {
        logger.error(message, ex);
        throw new RuntimeException(message, ex);
    }

    void check(boolean assertion, String message) {
        if (!assertion) {
            logger.error(message);
            throw new RuntimeException(message);
        }
    }

    String getEventId() {
        // gets the name of the system where the event occurred
        String eventSource = getConfigValue(SYSTEM_NAME_KEY);
        String eventTime = dateFormat.toString(new Date());
        return String.format("%s.%s", eventSource, eventTime);
    }

    String getService() {
        return getConfigValue(SYSTEM_NAME_KEY);
    }

    String getNode() {
        return getConfigValue(NODE_NAME_KEY);
    }

    String getQueue() {
        return getConfigValue(EVENT_QUEUE_NAME);
    }

    String getURI() {
        return getConfigValue(BROKER_URI_KEY);
    }
}

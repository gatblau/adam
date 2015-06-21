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

import javax.jms.*;

/**
 * Wraps the JMS session and producer instances for sending messages.
 */
class Sender {
    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;
    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    Sender(Connection connection, Session session, MessageProducer producer) {
        this.connection = connection;
        this.session = session;
        this.producer = producer;
    }

    void send(EventInfo event) {
        TextMessage message = null;
        try {
            message = session.createTextMessage();
            message.setText(event.toJson());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Sending message: '%s'", event.toJson()));
            }
            producer.send(message);
            if (logger.isDebugEnabled()) {
                logger.debug("Message sent.");
            }
        }
        catch (JMSException e) {
            logger.error(String.format("Failed to send message '%s'", event.toJson()), e);
        }
    }

    public void close() {
        try {
            producer.close();
            session.close();
            connection.close();
        }
        catch (JMSException e) {
            logger.error("Failed to close org.gatblau.adam.Sender.", e);
        }
    }
}

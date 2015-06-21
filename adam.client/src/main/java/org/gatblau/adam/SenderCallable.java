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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.concurrent.Callable;

/**
 * Creates a JMS org.gatblau.adam.Sender asynchronously handling the connection to the message broker.
 */
class SenderCallable implements Callable<Sender> {
    private static final Logger logger = LoggerFactory.getLogger(SenderCallable.class);
    private String brokerUri;
    private String queueName;
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    SenderCallable(String brokerUri, String queueName) {
        this.brokerUri = brokerUri;
        this.queueName = queueName;
    }

    @Override
    public Sender call() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing Event Publisher Service.");
            logger.debug("Loading configuration properties.");
        }
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUri);
        try {
            connection = connectionFactory.createConnection();
        }
        catch (JMSException e) {
            logger.error(String.format("Failed to create a connection with the message broker at '%s'", brokerUri), e);
            throw e;
        }
        try {
            connection.start();
        }
        catch (JMSException e) {
            logger.error(String.format("Failed to start a connection with the message broker at '%s'", brokerUri), e);
            throw e;
        }
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        catch (JMSException e) {
            logger.error("Failed to create a session for the existing message broker connection.", e);
            throw e;
        }
        Queue queue = null;
        try {
            queue = session.createQueue(queueName);
        }
        catch (JMSException e) {
            logger.error(String.format("Failed to create queue named '%s'.", queueName), e);
            throw e;
        }
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating JMS producer for error events.");
            }
            producer = session.createProducer(queue);
        }
        catch (JMSException e) {
            logger.error(String.format("Failed to create message producer for queue '%s'", queueName), e);
            throw e;
        }
        return new Sender(connection, session, producer);
    }
}

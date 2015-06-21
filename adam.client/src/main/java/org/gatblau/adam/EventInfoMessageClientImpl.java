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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.util.concurrent.*;

/**
 * The default implementation to publish system event messages.
 */
@Singleton
public class EventInfoMessageClientImpl implements EventInfoMessageClient {
    private static final Logger logger = LoggerFactory.getLogger(EventInfoMessageClientImpl.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Util util = new Util();
    private Future<Sender> senderFuture;
    private boolean started;

    public EventInfoMessageClientImpl() {
    }

    @PostConstruct
    private void construct() {
        if (!started) {
            start();
        }
    }

    @PreDestroy
    private void destroy() {
        if (started) {
            stop();
        }
    }

    @Override
    public String getQueue() {
        return util.getQueue();
    }

    @Override
    public String getURI() {
        return util.getURI();
    }

    @Override
    public String getService() {
        return util.getService();
    }

    @Override
    public String getNode() {
        return util.getNode();
    }

    @Override
    public void start() {
        if (!util.isActive()) return;
        CreateSenderAsync();
        started = true;
    }

    @Override
    public void stop() {
        if (!util.isActive()) return;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Releasing Event Publisher resources.");
            }
            executor.shutdown();
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                logger.info("Awaiting completion of threads.");
            }
            if (getSender() != null) {
                getSender().close();
            }
            started = false;
        }
        catch (Exception ex) {
            logger.error("Failed to destroy Event org.gatblau.adam.Sender.", ex);
        }
    }

    @Override
    public boolean canPublish() {
        return senderFuture.isDone();
    }

    @Override
    public String publish(EventInfo event) {
        if (!util.isActive()) return "";
        util.check(event != null, "Event passed to the EventPublisher was null.");
        String eventId = event.getEventId();
        if (eventId == null || eventId.trim().length() == 0) {
            // if the event Id was not set, sets it with a unique Id based on
            // System Name and Time Stamp
            eventId = util.getEventId();
            event.setEventId(eventId);
        }
        // if no service has been specified, obtains the service from the adam.properties file
        String service = event.getService();
        if (service == null || service.trim().length() == 0) {
            service = util.getService();
            event.setService(service);
        }
        // if no node has been specified, obtains the node from the adam.properties file
        String node = event.getNode();
        if (node == null || node.trim().length() == 0) {
            node = util.getNode();
            event.setNode(node);
        }
        try {
            if (!canPublish()) {
                logger.warn(
                    "Cannot publish message. \r\n" +
                    "This is likely due to the connection to the message broker not established.");
                return eventId;
            }
            executor.submit(new SenderRunnable(senderFuture.get(), event));
        }
        catch (Exception ex) {
            util.handle("Failed to send event message.", ex);
        }
        return eventId;
    }

    private Sender getSender() throws InterruptedException {
        try {
            return senderFuture.get();
        }
        catch (ExecutionException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("org.gatblau.adam.Sender not available.", e);
            }
            return null;
        }
    }

    private void CreateSenderAsync() {
        // creates a org.gatblau.adam.Sender asynchronously
        senderFuture = executor.submit(
            new SenderCallable(
                getURI(),
                getQueue()
            ));
    }
}

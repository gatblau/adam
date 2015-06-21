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

/**
 * The contract for publishing system events.
 */
public interface EventInfoMessageClient {
    /**
     * Publishes the specified event.
     * @param event the event information to be published.
     *              If the event Id is not set, the publisher allocates a unique Id based on
     *              the Sender System Name (obtained from configuration) and the current Time Stamp.
     *              Automatic population of the event Id by the publisher is the preferred approach.
     * @return the unique Id for the published event.
     */
    String publish(EventInfo event);

    /**
     * Starts the client.
     */
    void start();

    /**
     * Stops the client and releases any acquired resources.
     */
    void stop();

    /**
     * Determines if the client is in a state that can publish messages.
     * @return a boolean indicating whether the client can publish messages.
     */
    boolean canPublish();

    /**
     * Gets the name of the Queue where event messages are published.
     * @return the queue name.
     */
    String getQueue();

    /**
     * Gets the URI where messages are published.
     * @return a URI string.
     */
    String getURI();

    /**
     * Gets the name of the service using the client to publish event messages.
     * @return the sender system name.
     */
    String getService();

    /**
     * Gets the name of the server or container that hosts the client.
     * @return the name of the network node.
     */
    String getNode();
}

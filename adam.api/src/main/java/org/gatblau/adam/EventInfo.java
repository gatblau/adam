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

import org.codehaus.jackson.map.ObjectMapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * The serializable object used to carry system event information.
 * Used by the RequestClient and the MessageClient.
 */
@XmlRootElement
public class EventInfo implements Serializable {
    private String eventId;
    private String processId;
    private String node;
    private Date time;
    private EventType type;
    private String code;
    private String description;
    private String service;
    private String info;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new instance of the EventInfo class.
     */
    public EventInfo(){
        time = new Date();
    }

    /**
     * Get the unique identifier for this event.
     * @return a String.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique identifier for this event.
     * @param eventId a String containing the unique identifier for this event.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Get the unique identifier for the business process the event is part of.
     * For example, it can help correlating events for the purpose of tracing or diagnosing a problem.
     * @return a String.
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * Sets the unique identifier for the business process the event is part of.
     * @param processId a String containing the unique identifier for this event.
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Gets the name of the node that hosts the service where the event occurred.
     * @return a String.
     */
    public String getNode() {
        return node;
    }

    /**
     * Sets the name of the node that hosts the service where the event occurred.
     * @param node a String.
     */
    public void setNode(String node) {
        this.node = node;
    }

    /**
     * Gets the time when this event was created.
     * @return a Date.
     */
    public Date getTime() {
        return time;
    }

    /**
     * Gets the type of this event.
     * @return an enumerated value.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Sets the type of the event.
     * @param type an enumerated value indicating the event type.
     */
    public void setType(EventType type) {
        this.type = type;
    }

    /**
     * Gets the code associated with the event.
     * @return a String.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code associated with the event.
     * @param code a String containing the event code.
     *             the code is used to group instances of a particular event for reporting purposes.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description of the event.
     * @return a String containing the event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for this event.
     * @param description a String containing a brief description for the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the name of the system where the event occurred.
     * @return a String.
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the name of the system where the event occurred.
     * @param service a String with the name of the application or service publishing the event.
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Gets the detailed event information.
     * @return a String.
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the detailed event information.
     * @param info a String with event information.
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Converts the event object into a serialized JSON string.
     * @return a serialized JSON string.
     */
    public String toJson() {
        String json = null;
        try {
            json = mapper.writeValueAsString(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * A factory method to create an EventInfo instance from a JSON string.
     * @param jsonString the JSON string to deserialize.
     * @return an EventInfo instance.
     * @throws IOException when the jsonString could not be de-serialized.
     */
    public static EventInfo fromJson(String jsonString) throws IOException {
        EventInfo event = new EventInfo();
        return mapper.readValue(jsonString, EventInfo.class);
    }
}

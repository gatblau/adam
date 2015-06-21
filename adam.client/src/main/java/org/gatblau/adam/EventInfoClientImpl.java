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

import org.gatblau.gemma.Specification;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

public class EventInfoClientImpl implements EventInfoClient {
    private String serviceURI;
    private Client client;
    private DateFormatter formatter = new DateFormatter();

    public EventInfoClientImpl() {
        this.client = ClientBuilder.newClient();
    }

    @Override
    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    @Override
    public boolean isAvailable() {
        Response response = client
            .target(serviceURI)
            .path("/")
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .get();
        checkStatus(response);
        return response.readEntity(String.class).equals("OK");
    }

    @Override
    public EventInfo getEvent(String eventId) {
        Response response = client
            .target(serviceURI)
            .path("event/{eventId}")
            .resolveTemplate("eventId", eventId)
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .get();
        checkStatus(response);
        return response.readEntity(EventInfo.class);
    }

    @Override
    public List<EventInfo> getEventsByProcess(String procId) {
        Response response = client
            .target(serviceURI)
            .path("event")
            .queryParam("procId", procId)
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .get();
        checkStatus(response);
        return response.readEntity(new GenericType<List<EventInfo>>(){});
    }

    @Override
    public Specification getSpec() {
        Response response = client
            .target(serviceURI)
            .path("spec")
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .get();
        checkStatus(response);
        return response.readEntity(Specification.class);
    }

    @Override
    public List<EventInfo> queryEvents(int offset, int max, EventType type, Date dateFrom, Date dateTo) {
        Response response = client
            .target(serviceURI)
            .path("event/query")
            .queryParam("offset", offset)
            .queryParam("max", max)
            .queryParam("type", type.toString())
            .queryParam("from", formatter.toString(dateFrom))
            .queryParam("to", formatter.toString(dateTo))
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .get();
        checkStatus(response);
        return response.readEntity(new GenericType<List<EventInfo>>(){});
    }

    @Override
    public int deleteAll() {
        Response response = client
            .target(serviceURI)
            .path("event")
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .delete();
        checkStatus(response);
        return Integer.parseInt(response.readEntity(String.class));
    }

    @Override
    public int delete(Date from, Date to) {
        Response response = client
            .target(serviceURI)
            .path("event")
            .queryParam("from", formatter.toString(from))
            .queryParam("to", formatter.toString(to))
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .delete();
        checkStatus(response);
        return Integer.parseInt(response.readEntity(String.class));
    }

    @Override
    public void delete(String eventId) {
        Response response = client
            .target(serviceURI)
            .path("event/{eventId}")
            .resolveTemplate("eventId", eventId)
            .request(MediaType.APPLICATION_JSON)
            .header("Content-type", "application/json")
            .delete();
        checkStatus(response);
    }

    private void checkStatus(Response response) {
        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            throw new RuntimeException(
                String.format("HTTP Request failed: %s.", response.getStatusInfo())
            );
        }
    }
}

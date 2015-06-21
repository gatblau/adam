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

import org.gatblau.gemma.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Contract(
    name = "ADAM's Web API",
    description = "Provides access to the event log via HTTP.",
    version = "v0.0.1",
    author = "GATBLAU",
    date = "20/06/2015",
    termsOfUse = "This service can be used under the terms of the apache license (http://www.apache.org/licenses/LICENSE-2.0)."
)
public class EventInfoServiceImpl implements EventInfoService {
    private static final String FEATURE_ROOT = "features/wapi/";
    private DateFormatter dateFormatter = new DateFormatter();

    @Inject
    private EventRepository eventLog;

    @Inject
    private ApiInspector inspector;

    @GET
    @Path("spec")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Method(feature = FEATURE_ROOT + "get_api_spec.feature")
    @Override
    public Response spec() {
        return Response
            .ok(inspector.inspect(EventInfoServiceImpl.class))
            .build();
    }

    @GET
    @Path("/")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Method(feature = FEATURE_ROOT + "check_svc_available.feature")
    @Override
    public Response isAvailable() {
        return Response.ok("OK").build();
    }

    @GET
    @Path("/event/query")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Method(
        feature = FEATURE_ROOT + "query_events.feature",
        params = {
            @Parameter(name = "offset", description = "The number of entries to offset the query result."),
            @Parameter(name = "max", description = "The maximum number of events to retrieve."),
            @Parameter(name = "type", description = "The type of events to retrieve. Use ALL as a wildcard to retrieve any type of event."),
            @Parameter(name = "from", description = "The date from which to retrieve events."),
            @Parameter(name = "to", description = "The date to which to retrieve events.")
        },
        examples = {
            @Example(path=FEATURE_ROOT + "examples/query_events-JSON.txt")
        })
    @Override
    public Response getEvents(@DefaultValue("0") @QueryParam("offset") int offset,
                              @DefaultValue("-1") @QueryParam("max") int maxItems,
                              @DefaultValue("ALL") @QueryParam("type") String eventType,
                              @DefaultValue("01-01-1900-00:00") @QueryParam("from") String fromDate,
                              @DefaultValue("01-01-2100-00:00") @QueryParam("to") String toDate) {
        List<EventLog> events = eventLog.getEvents(offset, maxItems, eventType, getDate(fromDate), getDate(toDate));
        return Response.ok().entity(new GenericEntity<List<EventInfo>>(map(events)){}).build();
    }

    @GET
    @Path("/event/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Method(
        feature = FEATURE_ROOT + "find_event_by_id.feature",
        params = {
            @Parameter(name = "eventId", description = "The unique identifier of the event to retrieve.")
        },
        examples = {
            @Example(path=FEATURE_ROOT + "examples/find_event_by_id-JSON.txt"),
            @Example(path=FEATURE_ROOT + "examples/find_event_by_id-XML.txt"),
        })
    @Override
    public Response getEvent(@PathParam("eventId") String eventId) {
        EventLog event = eventLog.getEvent(eventId);
        return Response.ok().entity(map(event)).build();
    }

    @DELETE
    @Path("/event")
    @Method(
        feature = FEATURE_ROOT + "delete_events_within_period.feature",
        params = {
            @Parameter(name = "from", description = "The date to delete events from."),
            @Parameter(name = "to", description = "The date to delete events to.")
        },
        examples = {
            @Example(path=FEATURE_ROOT + "examples/delete_events_within_period-JSON.txt"),
            @Example(path=FEATURE_ROOT + "examples/delete_events_within_period-XML.txt"),
        })
    @Override
    public Response deleteEvents(
        @DefaultValue("01-01-1900-00:00") @QueryParam("from") String fromDate,
        @DefaultValue("01-01-2100-00:00") @QueryParam("to") String toDate) {
        int count = eventLog.deleteEvents(getDate(fromDate), getDate(toDate));
        return Response.ok(count).build();
    }

    @DELETE
    @Path("/event/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Method(feature = FEATURE_ROOT + "delete_event_by_id.feature")
    @Override
    public Response deleteEvent(@PathParam("eventId") String eventId) {
        eventLog.deleteEvent(eventId);
        return Response.ok().build();
    }

    @GET
    @Path("/event")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Method(
        feature = FEATURE_ROOT + "find_events_in_process.feature",
        params = {
            @Parameter(name = "procId", description = "The identifier of the business process correlating the events to retrieve.")
        },
        examples = {
            @Example(path=FEATURE_ROOT + "examples/find_events_in_process-JSON.txt"),
            @Example(path=FEATURE_ROOT + "examples/find_events_in_process-XML.txt"),
        })
    @Override
    public Response getEventsByProcess(@QueryParam("procId") String processId) {
        List<EventLog> events = eventLog.getEventsByProcess(processId);
        return Response.ok().entity(new GenericEntity<List<EventInfo>>(map(events)) {
        }).build();
    }

    private Date getDate(String dateString){
        Date d = null;
        try {
            d = dateFormatter.fromString(dateString);
        }
        catch (Exception e) {
            e.printStackTrace();
            d = new Date();
        }
        return d;
    }

    private EventInfo map(EventLog event) {
        EventInfo info = new EventInfo();
        info.setCode(event.getCode());
        info.setDescription(event.getDescription());
        info.setEventId(event.getEventId());
        info.setInfo(event.getInfo());
        info.setNode(event.getNode());
        info.setProcessId(event.getProcessId());
        info.setService(event.getService());
        info.setType(event.getEventType());
        return info;
    }

    private List<EventInfo> map(List<EventLog> entries) {
        // return entries.stream().map((item) -> map(item)).collect(Collectors.toList());
        List<EventInfo> events = new ArrayList<>();
        for (EventLog entry : entries) {
            events.add(map(entry));
        }
        return events;
    }
}

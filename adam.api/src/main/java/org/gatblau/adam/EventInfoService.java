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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Provides access and administration for the entries recorded in the event log.
 */
@Path("/")
public interface EventInfoService {
    /**
     * Gets the specification for the service.
     * @return the service specification.
     */
    @GET
    @Path("spec")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    Response spec();

    /**
     * Checks that the service is running.
     *
     * @return a Response with an "OK" string entity if the service is available.
     *         If the service is not available an HTTP error code is retrieved in the Response.
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    Response isAvailable();

    /**
     * Gets a list of events in the log.
     *
     * @param offset    the number of entries to offset the result set.
     *                  Allows to paginate through the event log.
     *                  If omitted, a zero offset is assumed.
     * @param maxItems  The maximum number of events to retrieve.
     *                  For example if maxItems = 10 then the last 10 events recorded in the log will be retrieved.
     *                  The default value is -1 indicating no limit is applied to the result set.
     * @param eventType The type of event to retrieve.
     *                  See {@link EventType} for available event types.
     *                  The default value is "ALL" which retrieves all event types.
     * @param fromDate  The date and time from which to retrieve events.
     *                  The date string format is "dd-MM-yyyy-HH:mm".
     * @param toDate    The date and time up to event should be retrieved.
     *                  The date string format is "dd-MM-yyyy-HH:mm".
     * @return a Response containing an entity with a list of events in the log ordered by date
     *     in descending order.
     */
    @GET
    @Path("/event/query")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    Response getEvents(
        @DefaultValue("0") @QueryParam("offset") int offset,
        @DefaultValue("-1") @QueryParam("max") int maxItems,
        @DefaultValue("ALL") @QueryParam("type") String eventType,
        @DefaultValue("01-01-1900-00:00") @QueryParam("from") String fromDate,
        @DefaultValue("01-01-2100-00:00") @QueryParam("to") String toDate);

    /**
     * Gets the entry in the log that matches the specified event identifier.
     *
     * @param eventId The unique identifier for the event to retrieve.
     * @return a Response with an entity containing information for the specified event
     *  or null if not entry matches the identifier.
     */
    @GET
    @Path("/event/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    Response getEvent(@PathParam("eventId") String eventId);

    /**
     * Deletes all events from the log.
     * @param fromDate the date from which to delete events.
     * @param toDate the date to which to delete events.
     * @return a Response with an entity specifying the number of events deleted.
     */
    @DELETE
    @Path("/event")
    Response deleteEvents(@DefaultValue("01-01-1900-00:00") @QueryParam("from") String fromDate,
                          @DefaultValue("01-01-2100-00:00") @QueryParam("to") String toDate);

    /**
     * Deletes the event specified by the passed in identifier.
     *
     * @param eventId The unique identifier of the event to be deleted.
     * @return a Response with no entity.
     */
    @DELETE
    @Path("/event/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    Response deleteEvent(@PathParam("eventId") String eventId);

    /**
     * Gets the entry in the log that matches the specified event identifier.
     *
     * @param processId The unique identifier for the business process within which
     *                  the events to retrieve occurred.
     * @return a Response with an entity containing a list of events that were part of
     * a specific business process identified by the passed-in process identifier.
     */
    @GET
    @Path("/event")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    Response getEventsByProcess(@QueryParam("procId") String processId);
}
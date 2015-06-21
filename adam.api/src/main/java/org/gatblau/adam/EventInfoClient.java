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

import java.util.Date;
import java.util.List;

/**
 * The contract for accessing the system events in the event log.
 */
public interface EventInfoClient {
    /**
     * Sets the URI of the event log service.
     * It must be set before any CRUD operation can be perfromed against the event log.
     * @param serviceURI a string with the event log service URI.
     */
    void setServiceURI(String serviceURI);

    /**
     * Determines whether the evnt log service is available.
     * @return true if the service is available, false otherwise.
     */
    boolean isAvailable();

    /**
     * Gets the specification for the service.
     * @return the service specification.
     */
    Specification getSpec();

    /**
     * Deletes all events in the event log.
     * @return the number of deleted events.
     */
    int deleteAll();

    /**
     * Deletes events between two specified dates.
     * @param from the date from which to delete events.
     * @param to the date to which to delete events.
     * @return the number of events deleted.
     */
    int delete(Date from, Date to);

    /**
     * Deletes a specific event from the event log.
     * @param eventId the unique Id of the event to be deleted.
     */
    void delete(String eventId);

    /**
     * Gets an event by event Id.
     * @param eventId the Id of the event to get.
     * @return the specified event information.
     */
    EventInfo getEvent(String eventId);

    /**
     * Gets a list of events which shared the same business process Id.
     * @param processId the Id of the business process within which the event(s) occurred.
     * @return zero or more events for the specified process.
     */
    List<EventInfo> getEventsByProcess(String processId);

    /**
     * Gets a list of events by type and date range with the ability to limit the number
     * of events to retrieve by the query.
     * @param offset the number of entries to offset the result set.
     *               Allows to paginate through the event log.
     *               If omitted, a zero offset is assumed.
     * @param max the maximum number of events to retrieve.
     *            If omitted no limit is imposed in the resul set.
     * @param type the type of event to retrieve.
     *             If set to "ALL" retrieves any type of events.
     * @param dateFrom the from which to retrieve events.
     *                 If omitted no from date limit is applied.
     * @param dateTo the date up to which to retrieve events.
     *               If omitted no no to date limit is applied.
     * @return zero or more events for the specified process.
     */
    List<EventInfo> queryEvents(int offset, int max, EventType type, Date dateFrom, Date dateTo);
}

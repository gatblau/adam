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

import java.util.Date;
import java.util.List;

interface EventRepository {
    List<EventLog> getEvents(int offset, int limit, String eventType, Date fromDate, Date toDate);
    EventLog getEvent(String eventId);
    int deleteEvents(Date fromDate, Date toDate);
    void deleteEvent(String eventId);
    long create(EventLog event);
    List<EventLog> getEventsByProcess(String processId);
    void saveError(String s, Exception e);
}

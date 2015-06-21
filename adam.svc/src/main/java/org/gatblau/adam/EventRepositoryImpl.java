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

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Singleton
public class EventRepositoryImpl implements EventRepository {
    @PersistenceContext(unitName = PersistenceUnit.NAME)
    private EntityManager em;

    @Override
    public List<EventLog> getEvents(int offset, int limit, String eventType, Date fromDate, Date toDate) {
        TypedQuery<EventLog> query = null;
        if (!eventType.equals("ALL")){
            query = em.createNamedQuery(EventLog.FIND_EVENTS_BY_TYPE_AND_RANGE, EventLog.class);
            query.setParameter(EventLog.PARAM_EVENT_TYPE, eventType);
        }
        else {
            query = em.createNamedQuery(EventLog.FIND_EVENTS_BY_RANGE, EventLog.class);
        }
        query.setParameter(EventLog.PARAM_FROM_DATE, fromDate);
        query.setParameter(EventLog.PARAM_TO_DATE, toDate);
        if (limit > -1){
            return query
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
        return query.getResultList();
    }

    @Override
    public EventLog getEvent(String eventId) {
        TypedQuery<EventLog> query = em.createNamedQuery(EventLog.FIND_EVENT_BY_ID, EventLog.class);
        query.setParameter(EventLog.PARAM_EVENT_ID, eventId);
        return query.getSingleResult();
    }

    @Override
    public int deleteEvents(Date fromDate, Date toDate) {
        Query query = em.createNamedQuery(EventLog.DELETE_EVENTS_BY_DATE_RANGE);
        query.setParameter(EventLog.PARAM_FROM_DATE, fromDate);
        query.setParameter(EventLog.PARAM_TO_DATE, toDate);
        return query.executeUpdate();
    }

    @Override
    public void deleteEvent(String eventId) {
        Query query = em.createNamedQuery(EventLog.DELETE_EVENT_BY_ID);
        query.setParameter(EventLog.PARAM_EVENT_ID, eventId);
        query.executeUpdate();
    }

    @Override
    public long create(EventLog event) {
        EventLog log = em.merge(event);
        return log.getId();
    }

    @Override
    public List<EventLog> getEventsByProcess(String processId) {
        TypedQuery<EventLog> query = em.createNamedQuery(EventLog.FIND_EVENTS_BY_PROCESS, EventLog.class);
        query.setParameter(EventLog.PARAM_EVENT_PROCESS_ID, processId);
        return query.getResultList();
    }

    @Override
    public void saveError(String message, Exception e) {
        EventLog event = new EventLog();
        event.setCode("ADAM_FAILED_TO_PERSIST_EVENT");
        event.setDescription(message);
        event.setEventType(EventType.ERROR);
        event.setEventTime(new Date());
        event.setInfo(toInfo(e));
        create(event);
    }

    private String toInfo(Exception e) {
        StringBuilder builder = new StringBuilder();
        builder.append(e.getMessage());
        builder.append("\r\n");
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements) {
            builder.append(String.format(
                "Class: %s, Line: %s, '%s'",
                element.getClassName(),
                element.getLineNumber(),
                element.toString()
            ));
            builder.append("\r\n");
        }
        return builder.toString();
    }
}

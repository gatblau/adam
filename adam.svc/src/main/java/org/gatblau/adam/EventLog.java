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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NamedQueries(value= {
        @NamedQuery(
                name = "eventLog.findByEventTypeAndDateRange",
                query = "SELECT el FROM EventLog el " +
                        "WHERE el.eventType = :eventType " +
                        "AND el.eventTime >= :fromDate " +
                        "AND el.eventTime <= :toDate " +
                        "ORDER BY el.eventTime DESC"
        ),
        @NamedQuery(
            name = "eventLog.findByProcess",
            query = "SELECT el FROM EventLog el " +
                    "WHERE el.processId = :processId " +
                    "ORDER BY el.eventTime DESC"
        ),
        @NamedQuery(
                name = "eventLog.findByDateRange",
                query = "SELECT el FROM EventLog el " +
                        "WHERE el.eventTime >= :fromDate " +
                        "AND el.eventTime <= :toDate " +
                        "ORDER BY el.eventTime DESC"
        ),
        @NamedQuery(
                name = "eventLog.findById",
                query = "SELECT el FROM EventLog el " +
                        "WHERE el.eventId = :eventId"
        ),
        @NamedQuery(
                name = "eventLog.deleteById",
                query = "DELETE FROM EventLog l " +
                        "WHERE l.eventId = :eventId "
        ),
        @NamedQuery(
                name = "eventLog.deleteByDateRange",
                query = "DELETE FROM EventLog l " +
                        "WHERE l.eventTime >= :fromDate " +
                        "AND l.eventTime <= :toDate "
        )
})
@Entity
public class EventLog implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String FIND_EVENTS_BY_TYPE_AND_RANGE = "eventLog.findByEventTypeAndDateRange";
    public static final String FIND_EVENTS_BY_RANGE = "eventLog.findByDateRange";
    public static final String FIND_EVENT_BY_ID = "eventLog.findById";
    public static final String FIND_EVENTS_BY_PROCESS = "eventLog.findByProcess";
    public static final String DELETE_EVENT_BY_ID = "eventLog.deleteById";
    public static final String DELETE_EVENTS_BY_DATE_RANGE = "eventLog.deleteByDateRange";
    public static final String PARAM_FROM_DATE = "fromDate";
    public static final String PARAM_TO_DATE = "toDate";
    public static final String PARAM_EVENT_TYPE = "eventType";
    public static final String PARAM_EVENT_ID = "eventId";
    public static final String PARAM_EVENT_PROCESS_ID = "processId";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private String code;

    @Column(unique = true)
    private String eventId;

    @Column
    private String processId;

    @Column(nullable = false)
    private String description;

    @Column(length=8192)
    private String info;

    @Column
    private String service;

    @Column
    private String node;

    @Column(nullable = false)
    private Date eventTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (getId() != null) {
            return getId().equals(((EventLog) that).getId());
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String eventCode) {
        this.code = eventCode;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String eventDescription) {
        this.description = eventDescription;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String eventInfo) {
        this.info = eventInfo;
    }

    public String getService() {
        return service;
    }

    public void setService(String eventSource) {
        this.service = eventSource;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}

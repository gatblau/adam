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
import org.jboss.ejb3.annotation.ResourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "SystemEventsService")
@ResourceAdapter(value="activemq-rar.rar")
public class MessageConsumer implements MessageListener {
    final static ObjectMapper mapper = new ObjectMapper();
    final static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Inject
    private EventRepository repository;

    public MessageConsumer() {
    }

    @Override
    public void onMessage(Message message) {
        String unwrappedText = "";
        try {
            TextMessage msg = (TextMessage) message;
            unwrappedText = msg.getText();
            EventInfo info = mapper.readValue(unwrappedText, EventInfo.class);
            long id = repository.create(mapInfo(info));
            logger.info("Event saved with Id '{}'.", id);
        }
        catch(Exception e) {
            String errorMsg = String.format("Failed to process received message: '%s'.", unwrappedText);
            logger.error(errorMsg, e);
            repository.saveError(errorMsg, e);
        }
    }

    private EventLog mapInfo(EventInfo info) {
        EventLog log = new EventLog();
        log.setEventId(info.getEventId());
        log.setProcessId(info.getProcessId());
        log.setCode(info.getCode());
        log.setDescription(info.getDescription());
        log.setInfo(info.getInfo());
        log.setNode(info.getNode());
        log.setService(info.getService());
        log.setEventTime(info.getTime());
        log.setEventType(info.getType());
        return log;
    }
}
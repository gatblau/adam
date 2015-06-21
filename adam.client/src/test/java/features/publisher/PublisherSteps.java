package features.publisher;

import cucumber.api.java.en.And;
import features.Common;
import features.Vars;
import org.dbunit.dataset.ITable;
import org.gatblau.adam.EventInfo;
import org.gatblau.adam.EventInfoMessageClient;
import org.gatblau.adam.Util;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import static features.Vars.*;

/**
 * Steps to publish and event.
 * It is defined as a CDI singleton so that only one instance is created.
 */
@Singleton
public class PublisherSteps {

    @Inject
    private EventInfoMessageClient publisher;

    @Inject
    private Common common;

    public PublisherSteps() {
    }

    @PostConstruct
    public void initialize_test() {
//        publisher.start();
        common.check(publisher.canPublish(), 500, 3);
    }

    @PreDestroy
    public void destroy(){
        publisher.stop();
    }

    @And("^the event is published$")
    public void the_event_is_published() throws Throwable {
        EventInfo event = common.get(KEY_TEST_EVENT);
        common.put(KEY_TEST_EVENT_ID, publisher.publish(event));
    }

    @And("^an event entry is created in the data source$")
    public void an_event_entry_is_created_in_the_data_source() throws Throwable {
        common.put(
            KEY_TEST_EVENT_RECORD,
            common.retryGetRecord(
                3, // the number of attempts before raising an error.
                500, // the data source key.
                "SELECT * FROM EventLog WHERE eventId = '%s'",
                (Object) common.get(KEY_TEST_EVENT_ID) // the query and parameters.
            )
        );
    }

    @And("^an error entry is created in the data source$")
    public void an_error_entry_is_created_in_the_data_source() throws Throwable {
        ITable table = common.retryGetRecord(
            3, // the number of attempts before raising an error.
            500, // the data source key.
            "SELECT * FROM EventLog WHERE eventType = 'ERROR' AND code = 'ADAM_FAILED_TO_PERSIST_EVENT'"
        );
        if (table.getRowCount() == 0) {
            throw new RuntimeException("Could not find ERROR event in the data source.");
        }
        common.put(Vars.KEY_TEST_EVENT_RECORD, table);
    }

    @And("^the event parameters have been recorded in the data source$")
    public void the_event_parameters_have_been_recorded_in_the_data_source() throws Throwable {
        EventInfo event = common.get(Vars.KEY_TEST_EVENT);
        ITable table = common.get(Vars.KEY_TEST_EVENT_RECORD);
        if (!event.getCode().equals(table.getValue(0, "code"))) {
            throw new RuntimeException("'Code' value not persisted to datasource.");
        }
        if (!event.getDescription().equals(table.getValue(0, "description"))) {
            throw new RuntimeException("'Description' value not persisted to datasource.");
        }
        if (!event.getEventId().equals(table.getValue(0, "eventId"))) {
            throw new RuntimeException("'Event Id' value not persisted to datasource.");
        }
        if (!event.getInfo().equals(table.getValue(0, "info"))) {
            throw new RuntimeException("'Info' value not persisted to datasource.");
        }
        if (!event.getNode().equals(table.getValue(0, "node"))) {
            throw new RuntimeException("'Node' value not persisted to datasource.");
        }
        if (!event.getProcessId().equals(table.getValue(0, "processId"))) {
            throw new RuntimeException("'Process Id' value not persisted to datasource.");
        }
        if (!event.getService().equals(table.getValue(0, "service"))) {
            throw new RuntimeException("'Service' value not persisted to datasource.");
        }
        if (!event.getType().toString().equals(table.getValue(0, "eventType"))) {
            throw new RuntimeException("'Event Type' value not persisted to datasource.");
        }
    }

    @And("^a publisher config file exists$")
    public void a_publisher_config_file_exists() throws Throwable {
        common.put(Vars.KEY_CONFIGURATION, common.getProps());
    }

    @And("^the publisher config file defines the Message Broker URI$")
    public void the_publisher_config_file_defines_the_Message_Broker_URI() throws Throwable {
        common.checkConfigProperty(Util.BROKER_URI_KEY);
    }

    @And("^the publisher config file defines the name of the Message Queue where events should be sent$")
    public void the_publisher_config_file_defines_the_name_of_the_Message_Queue_where_events_should_be_sent() throws Throwable {
        common.checkConfigProperty(Util.EVENT_QUEUE_NAME);
    }

    @And("^an event with specific Event Id, Service Name and Node Name has been created$")
    public void an_event_with_specific_Event_Id_Service_Name_and_Node_Name_has_been_created() throws Throwable {
        common.put(Vars.KEY_TEST_EVENT, common.createTestEventExplicit());
    }

    @And("^the publisher config file defines the name of the Service publishing the event$")
    public void the_publisher_config_file_defines_the_name_of_the_Service_publishing_the_event() throws Throwable {
        common.checkConfigProperty(Util.SYSTEM_NAME_KEY);
    }

    @And("^the publisher config file defines the name of the Node where the Service publishing the event is running$")
    public void the_publisher_config_file_defines_the_name_of_the_Node_where_the_Service_publishing_the_event_is_running() throws Throwable {
        common.checkConfigProperty(Util.NODE_NAME_KEY);
    }

    @And("^an event not having Event Id, Service Name and Node Name has been created$")
    public void an_event_not_having_Event_Id_Service_Name_and_Node_Name_has_been_created() throws Throwable {
        common.put(Vars.KEY_TEST_EVENT, common.createTestEvent());
    }

    @And("^the message broker details are known$")
    public void the_message_broker_details_are_known() throws Throwable {
        common.put(Util.BROKER_URI_KEY, publisher.getURI());
        common.put(Util.EVENT_QUEUE_NAME, publisher.getQueue());
    }

    @And("^an malformed event has been created$")
    public void an_malformed_event_has_been_created() throws Throwable {
        common.put(KEY_TEST_EVENT_MALFORMED, "{ this is the malformed event }");
    }

    @And("^the event is published not using the ADAM client$")
    public void the_event_is_published_not_using_the_ADAM_client() throws Throwable {
        common.clearLog();
        common.sendJMS(
            common.get(Util.BROKER_URI_KEY).toString(),
            common.get(Util.EVENT_QUEUE_NAME).toString(),
            common.get(KEY_TEST_EVENT_MALFORMED).toString()
        );
    }

    @And("^the event type is an ERROR$")
    public void the_event_type_is_an_ERROR() throws Throwable {
        common.checkAttribute("eventType", "ERROR", common.get(KEY_TEST_EVENT_RECORD));
    }

    @And("^the event description indicates a failure when persisting the message$")
    public void the_event_description_indicates_a_failure_when_persisting_the_message() throws Throwable {
        common.checkAttribute("description", "Failed to process received message:", common.get(KEY_TEST_EVENT_RECORD));
    }

    @And("^the event information contains the error stack trace$")
    public void the_event_information_contains_the_error_stack_trace() throws Throwable {
        common.checkAttribute("info", "Unexpected character", common.get(KEY_TEST_EVENT_RECORD));
    }

    @And("^the event description contains the malformed message$")
    public void the_event_description_contains_the_malformed_message() throws Throwable {
        common.checkAttribute("description", common.get(KEY_TEST_EVENT_MALFORMED), common.get(KEY_TEST_EVENT_RECORD));
    }
}
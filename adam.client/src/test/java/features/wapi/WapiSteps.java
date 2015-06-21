package features.wapi;

import cucumber.api.java.en.And;
import features.Common;
import features.Vars;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.gatblau.adam.EventInfoClient;
import org.gatblau.adam.EventInfo;
import org.gatblau.adam.EventType;
import org.gatblau.gemma.Specification;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import static features.Vars.*;

@Singleton
public class WapiSteps {
    @Inject
    private Common common;

    @Inject
    private EventInfoClient client;

    @PostConstruct
    public void init_test() {
        client.setServiceURI(VALUE_TEST_SERVICE_URI);
    }

    @And("^an event exists in the data source$")
    public void an_event_exists_in_the_data_source() throws Throwable {
        common.loadAndSave(KEY_TEST_EVENT_RECORD, FILE_DATA_TEST_EVENT);
    }

    @And("^the event is requested by Id$")
    public void the_event_is_requested_by_Id() throws Throwable {
        IDataSet set = common.get(KEY_TEST_EVENT_RECORD);
        ITable table = set.getTable(set.getTableNames()[0]);
        String eventId = table.getValue(0, "eventId").toString();
        EventInfo event = null;
        try {
            common.remove(Vars.KEY_EXCEPTION);
            event = client.getEvent(eventId);
            common.put(KEY_TEST_EVENT, event);
        }
        catch (Exception e) {
            common.put(Vars.KEY_EXCEPTION, e);
        }
    }

    @And("^the event is retrieved$")
    public void the_event_is_retrieved() throws Throwable {
        EventInfo event = common.get(KEY_TEST_EVENT);
        if (event == null) {
            throw new RuntimeException("No event retrieved.");
        }
    }

    @And("^an event does not exist in the data source$")
    public void an_event_does_not_exist_in_the_data_source() throws Throwable {
        common.clearLog();
        common.load(KEY_TEST_EVENT_RECORD, FILE_DATA_TEST_EVENT);
    }

    @And("^a series of events exists in the data source for a given process$")
    public void a_series_of_events_exists_in_the_data_source_for_a_given_process() throws Throwable {
        common.createTestEvents();
    }

    @And("^the events are requested using the process Id$")
    public void the_events_are_requested_using_the_process_Id() throws Throwable {
        List<EventInfo> result = client.getEventsByProcess(VALUE_BIZ_PROC_ID);
        if (result.size() > 0) {
            common.put(KEY_SVC_EVENTS, result);
        }
    }

    @And("^the events are retrieved$")
    public void the_events_are_retrieved() throws Throwable {
        List<EventInfo> events = common.get(KEY_SVC_EVENTS);
        if (events == null || events.size() == 0) {
            throw new RuntimeException("No events have been retrieved from the service.");
        }
    }

    @And("^there are no events in the data source for a given process$")
    public void there_are_no_events_in_the_data_source_for_a_given_process() throws Throwable {
        common.remove(KEY_SVC_EVENTS);
        common.clearLog();
    }

    @And("^the events are not found$")
    public void the_events_are_not_found() throws Throwable {
        if (common.exists(KEY_SVC_EVENTS)) {
            throw new RuntimeException("Events have been retrieved from the service.");
        }
    }

    @And("^a request to delete all events is issued$")
    public void a_request_to_delete_all_events_is_issued() throws Throwable {
        client.deleteAll();
    }

    @And("^there are many events in the event log$")
    public void there_are_many_events_in_the_event_log() throws Throwable {
        common.createTestEvents();
    }

    @And("^the event log does not have any event$")
    public void the_event_log_does_not_have_any_event() throws Throwable {
        if (common.getEventCount() > 0) {
            throw new RuntimeException("The event log is not empty.");
        }
    }

    @And("^the event invocation succeeds$")
    public void the_event_invocation_succeeds() throws Throwable {
        if (common.exists(KEY_EXCEPTION)) {
            throw new RuntimeException("Service invocation failed.");
        }
    }

    @And("^the service invocation fails$")
    public void the_service_invocation_fails() throws Throwable {
        Exception e = common.get(KEY_EXCEPTION);
        if (e == null) {
            throw new RuntimeException("Service invocation succeeded.");
        }
    }

    @And("^a request to delete all events between two dates$")
    public void a_request_to_delete_all_events_between_two_dates() throws Throwable {
        client.delete(VALUE_DATE_FROM, VALUE_DATE_TO);
    }

    @And("^the event log does not have any event within the specified dates$")
    public void the_event_log_does_not_have_any_event_within_the_specified_dates() throws Throwable {
        common.checkNoEventsBetweenDates(VALUE_DATE_FROM, VALUE_DATE_TO);
    }

    @And("^the specification for the API is requested$")
    public void the_specification_for_the_API_is_requested() throws Throwable {
        common.put(KEY_SVC_SPEC, client.getSpec());
    }

    @And("^the specification is retrieved$")
    public void the_specification_is_retrieved() throws Throwable {
        Specification spec = common.get(KEY_SVC_SPEC);
        if (spec == null || spec.feature.isEmpty()) {
            throw new RuntimeException("Specification does not have features.");
        }
    }

    @And("^a request to the service root is made$")
    public void a_request_to_the_service_root_is_made() throws Throwable {
        common.put(KEY_SVC_AVAILABLE, client.isAvailable());
    }

    @And("^an OK message is received$")
    public void an_OK_message_is_received() throws Throwable {
        boolean available = common.get(KEY_SVC_AVAILABLE);
        if (!available) {
            throw new RuntimeException("Service is not available");
        }
    }

    @And("^a query is issued specifying event type, date range and limit$")
    public void a_query_is_issued_specifying_event_type_date_range_and_limit() throws Throwable {
        common.put(KEY_SVC_EVENTS, client.queryEvents(0, 3, EventType.ALL, VALUE_DATE_FROM, VALUE_DATE_TO));
    }

    @And("^a query result is obtained$")
    public void a_query_result_is_obtained() throws Throwable {
        List<EventInfo> events = common.get(KEY_SVC_EVENTS);
        if (events == null || events.size() == 0) {
            throw new RuntimeException("No events retrieved by query.");
        }
    }

    @And("^a request to delete the event is issued$")
    public void a_request_to_delete_the_event_is_issued() throws Throwable {
        IDataSet set = common.get(KEY_TEST_EVENT_RECORD);
        String eventId = set.getTable(set.getTableNames()[0]).getValue(0, "eventId").toString();
        common.put(KEY_TEST_EVENT_ID, eventId);
        client.delete(eventId);
    }

    @And("^the event is deleted from the data source$")
    public void the_event_is_deleted_from_the_data_source() throws Throwable {
        if (common.checkEventExist(common.get(KEY_TEST_EVENT_ID))) {
            throw new RuntimeException("Event not deleted.");
        }
    }
}

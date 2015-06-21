package features;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Vars {
    // property bag keys
    public static final String KEY_TEST_EVENT = "test_event_key";
    public static final String KEY_TEST_EVENT_ID = "test_event_id";
    public static final String KEY_CONFIGURATION = "cfg";
    public static final String KEY_TEST_EVENT_RECORD = "test_event_record";
    public static final String KEY_TEST_EVENT_MALFORMED = "malformed_event";
    public static final String KEY_EXCEPTION = "ex";
    public static final String KEY_SVC_EVENTS = "service_events";
    public static final String KEY_SVC_AVAILABLE = "svc_available";
    public static final String KEY_SVC_SPEC = "svc_spec";

    // files
    private static final String FILE_DATA_ROOT = "/data/";
    public static final String FILE_DATA_TEST_EVENT = FILE_DATA_ROOT + "event.xml";
    public static final String FILE_DATA_TEST_EVENTS = FILE_DATA_ROOT + "events.xml";
    public static final String FILE_DATA_EMPTY_LOG =  FILE_DATA_ROOT + "empty.xml";

    // values
    public static final Date VALUE_DATE_FROM = Date.from(LocalDate.of(2015, 1, 15).atStartOfDay(ZoneId.systemDefault()).toInstant());
    public static final Date VALUE_DATE_TO = Date.from(LocalDate.of(2015, 4, 15).atStartOfDay(ZoneId.systemDefault()).toInstant());
    public static final String VALUE_TEST_SERVICE_URI = "http://localhost:8080/adam/wapi";
    public static final String VALUE_BIZ_PROC_ID = "PROC_12";

    // queries
    public static final String QUERY_EVENTS_BETWEEN_DATES_COUNT =
        "SELECT COUNT(*) AS COUNT " +
        "FROM EVENTLOG " +
        "WHERE EVENTTIME < '%s' " +
        "AND EVENTTIME > '%s'";

    public static final String QUERY_EVENTS_COUNT =
        "SELECT COUNT(*) AS COUNT " +
        "FROM EVENTLOG";

    public static final String QUERY_EVENT_BY_ID =
        "SELECT * " +
        "FROM EVENTLOG " +
        "WHERE EVENTID = '%s'";
}

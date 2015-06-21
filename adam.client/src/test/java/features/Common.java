package features;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.gatblau.adam.DateFormatter;
import org.gatblau.adam.EventInfo;
import org.gatblau.adam.EventInfoMessageClient;
import org.gatblau.adam.EventType;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static features.Vars.*;

@Singleton
public class Common {
    @Inject
    private DatabaseDriver db;

    @Inject
    private EventInfoMessageClient publisher;

    private Map<String, Object> cache = new HashMap<>();
    private DateFormatter formatter = new DateFormatter();

    public void check(boolean condition, int attempts, int interval) {
        for (int i = 0; i < attempts; i++) {
            if (condition) {
                return;
            }
            else {
                wait(interval);
            }
        }

    }

    private void wait(int interval) {
        try {
            Thread.sleep(interval);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ITable retryGetRecord(int attempts, int interval, String query, Object... params) {
        ITable table = null;
        for (int i = 0; i < attempts; i++) {
            table = db.query(query, params);
            if (table.getRowCount() != 0) break;
            wait(interval);
        }
        if (table.getRowCount() == 0){
            throw new RuntimeException(
                String.format("No record found for query %s",
                    String.format(query, params)));
        }
        if (table.getRowCount() > 1){
            throw new RuntimeException(
                String.format("Multiple records found for query %s",
                    String.format(query, params)));
        }
        return table;
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public <T> T get(String key) {
        T value = (T) cache.get(key);
        if (value == null) {
            throw new RuntimeException(
                String.format("Value with key %s not found in cache.", key));
        }
        return value;
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public boolean exists(String key) {
        return cache.containsKey(key);
    }

    public Properties getProps() throws IOException {
        Properties prop = new Properties();
        InputStream in = EventInfoMessageClient.class.getResourceAsStream("/adam.properties");
        if (in == null) {
            throw new RuntimeException("File adam.properties not found.");
        }
        prop.load(in);
        return prop;
    }

    public void checkConfigProperty(String key){
        Properties props = get(KEY_CONFIGURATION);
        if (props.getProperty(key) == null) {
            throw new RuntimeException(String.format("Value for key %s not found in properties.", key));
        }
    }

    public EventInfo createTestEvent(){
        EventInfo event = new EventInfo();
        event.setCode("Error-01");
        event.setDescription("An error has occurred.");
        event.setInfo("Stack trace here...");
        event.setType(EventType.ERROR);
        event.setProcessId("Process-101");
        return event;
    }

    public EventInfo createTestEventExplicit() {
        EventInfo event = new EventInfo();
        event.setEventId("EVENT-01");
        event.setCode("Error-01");
        event.setDescription("An error has occurred.");
        event.setService("Service-ABC");
        event.setInfo("Stack trace here...");
        event.setType(EventType.ERROR);
        event.setProcessId("Process-101");
        event.setNode("Server-A");
        return event;
    }

    public void sendJMS(String brokerURI, String queueName, String msg) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURI);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage();
        message.setText(msg);
        producer.send(message);
        session.close();
        producer.close();
        connection.close();
    }

    public void checkAttribute(String attributeName, String attributeValue, ITable table) throws DataSetException {
        String value = table.getValue(0, attributeName).toString();
        if (!value.contains(attributeValue)) {
            throw new RuntimeException(String.format("'%s' is not '%s' but '%s'.", attributeName, attributeValue, value));
        }
    }

    public void clearLog() {
        db.setup(FILE_DATA_EMPTY_LOG);
    }

    public void load(String key, String dataFile) {
        IDataSet set = db.load(dataFile);
        put(key, set);
    }

    public void loadAndSave(String key, String dataFile) {
        IDataSet set = db.load(dataFile);
        put(key, set);
        db.save(set);
    }

    public void createTestEvents() {
        db.setup(FILE_DATA_TEST_EVENTS);
    }

    public int getEventCount() {
        ITable table = db.query(QUERY_EVENTS_COUNT);
        int count = 0;
        try {
            count = Integer.parseInt(table.getValue(0, "COUNT").toString());
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to retrieve event log count.", e);
        }
        return count;
    }

    public void checkNoEventsBetweenDates(Date from, Date to) {
        ITable table = db.query(
            String.format(
                QUERY_EVENTS_BETWEEN_DATES_COUNT,
                formatter.toString(from),
                formatter.toString(to))
        );
        int count = 0;
        try {
            count = Integer.parseInt(table.getValue(0, "COUNT").toString());
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to retrieve event log count.", e);
        }
        if (count > 0) {
            throw new RuntimeException("Events exist between specified dates.");
        }
    }

    public boolean checkEventExist(String eventId) {
        ITable table = db.query(String.format(QUERY_EVENT_BY_ID, eventId));
        return table.getRowCount() == 1;
    }
}

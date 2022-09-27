package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class EventInfo implements EventItemInfo {
    private final String name;
    private int id;
    private Date dateStart;
    private Date dateEnd;
    private int participants;
    private User organizer;
    private User chef;

    private ObservableList<ServiceInfo> services;

    public EventInfo(String name) {
        this.name = name;
        id = 0;
    }

    public static ObservableList<EventInfo> loadAllEventInfo() {
        ObservableList<EventInfo> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                EventInfo e = new EventInfo(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("date_start");
                e.dateEnd = rs.getDate("date_end");
                e.participants = rs.getInt("expected_participants");
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                int chefId = rs.getInt("chef_id");
                e.chef = User.loadUserById(chefId);
                all.add(e);
            }
        });

        for (EventInfo e : all) {
            e.services = ServiceInfo.loadServiceInfoForEvent(e.id);
        }
        return all;
    }

    public ObservableList<ServiceInfo> getServices() {
        return FXCollections.unmodifiableObservableList(this.services);
    }

    // STATIC METHODS FOR PERSISTENCE

    public String toString() {
        DateTimeFormatter eventDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return name + ": " + dateStart.toLocalDate().format(eventDateFormat) + "-" + dateEnd.toLocalDate().format(eventDateFormat) + ", " + participants + " pp. (" + organizer.getUserName() + ")";
    }
}

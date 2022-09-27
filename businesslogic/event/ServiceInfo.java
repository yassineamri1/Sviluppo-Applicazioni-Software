package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.menu.Menu;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceInfo implements EventItemInfo {
    private final String name;
    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int participants;
    private int menuId;
    private int userId;
    private Menu acceptedMenu;
    private int chefId;


    public ServiceInfo(String name) {
        this.name = name;
    }

    public static ObservableList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT s.*, e.organizer_id, e.chef_id " +
                "FROM Services as s JOIN Events AS e ON e.id = event_id WHERE event_id = " + event_id;
        PersistenceManager.executeQuery(query, rs -> {
            String s = rs.getString("name");
            ServiceInfo serv = new ServiceInfo(s);
            serv.id = rs.getInt("id");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            serv.startDate = LocalDateTime.parse(rs.getString("start_date"), formatter);
            serv.endDate = LocalDateTime.parse(rs.getString("end_date"), formatter);
            serv.participants = rs.getInt("expected_participants");
            serv.userId = rs.getInt("organizer_id");
            serv.chefId = rs.getInt("chef_id");
            int approvedMenuId = rs.getInt("approved_menu_id");

            if (approvedMenuId != 0) {
                CatERing.getInstance().getMenuManager().getAllMenus();
                serv.menuId = approvedMenuId;
                serv.acceptedMenu = CatERing.getInstance().getMenuManager().getMenuById(approvedMenuId);
            }

            result.add(serv);
        });

        return result;
    }

    public boolean hasSheet() {
        String query = "SELECT count(id) as number_of_task FROM Tasks WHERE service_id = " + id;
        final int[] numberOfTask = {0};

        PersistenceManager.executeQuery(query, rs -> numberOfTask[0] = rs.getInt("number_of_task"));

        return numberOfTask[0] > 0;
    }

    public boolean hasMenu() {
        return menuId != 0;
    }

    // STATIC METHODS FOR PERSISTENCE

    public String toString() {
        String approved = (hasMenu()) ? "(confermato)" : "(non confermato)";
        DateTimeFormatter serviceDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH:mm");
        return name + ": " + startDate.format(serviceDateFormat) + " (" + startDate.format(hourFormat) + "-" + endDate.format(hourFormat) + "), " + participants + " pp, " + approved + ".";
    }

    public int getMenuId() {
        return this.menuId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }


    public static ServiceInfo getServiceById(int id) {
        AtomicReference<ServiceInfo> service = new AtomicReference<>();
        String query = "SELECT s.*, e.organizer_id, e.chef_id " +
                "FROM Services as s JOIN Events AS e ON e.id = event_id WHERE s.id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            String s = rs.getString("name");
            ServiceInfo serv = new ServiceInfo(s);
            serv.id = rs.getInt("id");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            serv.startDate = LocalDateTime.parse(rs.getString("start_date"), formatter);
            serv.endDate = LocalDateTime.parse(rs.getString("end_date"), formatter);
            serv.participants = rs.getInt("expected_participants");
            serv.userId = rs.getInt("organizer_id");
            serv.chefId = rs.getInt("chef_id");
            int approvedMenuId = rs.getInt("approved_menu_id");

            if (approvedMenuId != 0) {
                CatERing.getInstance().getMenuManager().getAllMenus();
                serv.menuId = approvedMenuId;
                serv.acceptedMenu = CatERing.getInstance().getMenuManager().getMenuById(approvedMenuId);
            }

            service.set(serv);
        });

        return service.get();
    }

    public Menu getAcceptedMenu() {
        return acceptedMenu;
    }

    public int getChefId() {
        return chefId;
    }
}

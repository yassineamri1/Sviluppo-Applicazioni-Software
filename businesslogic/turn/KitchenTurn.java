package businesslogic.turn;

import businesslogic.user.User;
import persistence.PersistenceManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class KitchenTurn extends Turn implements Comparable<KitchenTurn> {

    public static ArrayList<KitchenTurn> getAllKitchenTurns() {

        ArrayList<KitchenTurn> kitchenTurns = new ArrayList<>();

        String query = "SELECT * FROM KitchenTurn";
        PersistenceManager.executeQuery(query, rs -> {
            KitchenTurn kitchenTurn = new KitchenTurn();
            kitchenTurn.completed = rs.getBoolean("completed");
            kitchenTurn.id = rs.getInt("id");
            kitchenTurn.usersAvailable = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            kitchenTurn.start = LocalDateTime.parse(rs.getString("start"), formatter);
            kitchenTurn.end = LocalDateTime.parse(rs.getString("end"), formatter);

            kitchenTurns.add(kitchenTurn);
        });

        kitchenTurns.forEach((kitchenTurn -> {
            String query2 = "select user_id  from KitchenTurnAvailability WHERE kitchen_turn_id =" + kitchenTurn.id;
            PersistenceManager.executeQuery(query2, rs -> {
                kitchenTurn.usersAvailable.add(User.loadUserById(rs.getInt("user_id")));
            });
        }));

        return kitchenTurns;
    }

    public static KitchenTurn getKitchenTurnById(int id) {

        if (id == 0) return null;

        KitchenTurn kitchenTurn = new KitchenTurn();

        String query = "SELECT * FROM KitchenTurn WHERE id=" + id;
        PersistenceManager.executeQuery(query, rs -> {
            kitchenTurn.completed = rs.getBoolean("completed");
            kitchenTurn.id = rs.getInt("id");
            kitchenTurn.usersAvailable = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            kitchenTurn.start = LocalDateTime.parse(rs.getString("start"), formatter);
            kitchenTurn.end = LocalDateTime.parse(rs.getString("end"), formatter);

            String query2 = "select user_id  from KitchenTurnAvailability WHERE kitchen_turn_id =" + kitchenTurn.id;
            PersistenceManager.executeQuery(query2, rt -> {
                kitchenTurn.usersAvailable.add(User.loadUserById(rt.getInt("user_id")));
            });
        });
        return kitchenTurn;
    }

    public static void setKitchenTurnAsCompleted(int id) {
        String query = "UPDATE KitchenTurn SET completed = 1 WHERE id = " + id;
        PersistenceManager.executeUpdate(query);
    }

    public static void setKitchenTurnAsUncompleted(int id) {
        String query = "UPDATE KitchenTurn SET completed = 0 WHERE id = " + id;
        PersistenceManager.executeUpdate(query);
    }

    public static double getAvailableTimeForAUser(int userId, int kitchenTurnId) {
        String query = "SELECT SUM(time) as sum FROM Tasks WHERE kitchen_turn_id = " + kitchenTurnId + " AND user_id =" + userId;
        final double[] totalTaskDuration = new double[1];
        PersistenceManager.executeQuery(query, rs -> {
            totalTaskDuration[0] = rs.getDouble("sum");
        });

        query = "SELECT TIMESTAMPDIFF(MINUTE,start,end) as duration FROM KitchenTurn WHERE id =" + kitchenTurnId;

        final double[] kitchenTurnDuration = new double[1];

        PersistenceManager.executeQuery(query, rs -> {
            kitchenTurnDuration[0] = rs.getDouble("duration");
        });

        return kitchenTurnDuration[0] - totalTaskDuration[0];
    }

    @Override
    public int compareTo(KitchenTurn o) {
        return start.compareTo(o.start);
    }
}

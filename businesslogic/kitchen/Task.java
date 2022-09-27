package businesslogic.kitchen;

import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.turn.KitchenTurn;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Task implements Comparable<Task> {

    private int id;
    private KitchenProcedure kitchenProcedure;
    private User cook;
    private int serviceId;
    private Double quantity;
    private boolean completed;
    private Double time;
    private Integer position;
    private boolean extraMenu;
    private KitchenTurn kitchenTurn;

    public static void deleteTask(int task_id, int serviceId, int position) {
        String query = "UPDATE Tasks SET position = (position - 1) WHERE service_id = " + serviceId + " AND position > " + position;
        PersistenceManager.executeUpdate(query);

        query = "DELETE FROM Tasks WHERE id = " + task_id;
        PersistenceManager.executeUpdate(query);
    }

    public static Task create(KitchenProcedure kitchenProcedure, ServiceInfo service, boolean extraMenu) {
        Task task = new Task();

        task.completed = false;
        task.quantity = 0.0;
        task.time = 0.0;
        task.serviceId = service.getId();
        task.extraMenu = extraMenu;
        task.kitchenProcedure = kitchenProcedure;
        task.position = 0;
        return task;
    }

    public static int saveTask(int kitchenProcedureId, int serviceId, boolean extraMenu) {

        String itemInsert = "INSERT INTO Tasks (kitchen_procedure_id, service_id, completed, extra_menu, position)\n" +
                "SELECT "
                + kitchenProcedureId +
                ", "
                + serviceId +
                " ,"
                + "0" +
                " ,"
                + (extraMenu ? 1 : 0) +
                ", COALESCE(MAX(position), -1) + 1 FROM Tasks WHERE service_id = " + serviceId;

        PersistenceManager.executeUpdate(itemInsert);

        return PersistenceManager.getLastId();
    }

    public static ObservableList<Task> getTasksByServiceId(int serviceId) {
        List<Task> list = new ArrayList<>();
        String query = "SELECT * FROM Tasks WHERE service_id = " + serviceId;
        PersistenceManager.executeQuery(query, rs -> {
                    Task task = new Task();
                    task.id = rs.getInt("id");
                    task.completed = rs.getBoolean("completed");
                    task.quantity = rs.getDouble("quantity");
                    task.time = rs.getDouble("time");
                    task.cook = User.loadUserById(rs.getInt("user_id"));
                    task.serviceId = rs.getInt("service_id");
                    task.position = rs.getInt("position");
                    task.extraMenu = rs.getBoolean("extra_menu");
                    task.kitchenTurn = KitchenTurn.getKitchenTurnById(rs.getInt("kitchen_turn_id"));

                    task.kitchenProcedure = KitchenProcedure.loadKitchenProcedureById(rs.getInt("kitchen_procedure_id"));
                    list.add(task);
                }
        );

        return FXCollections.observableArrayList(list);
    }

    public static int upTask(int taskId, int position, int serviceId) {

        if (position == 0) {
            return position;
        }

        int newPostion = position - 1;

        final Integer[] taskToChangeId = new Integer[1];

        String query = "SELECT id FROM Tasks WHERE position = " + newPostion + " and service_id = " + serviceId;
        PersistenceManager.executeQuery(query, rs -> taskToChangeId[0] = rs.getInt("id"));

        query = "UPDATE Tasks SET position = " + newPostion + " WHERE id= " + taskId;
        PersistenceManager.executeUpdate(query);


        query = "UPDATE Tasks SET position = " + position + " WHERE id= " + taskToChangeId[0];
        PersistenceManager.executeUpdate(query);

        return newPostion;
    }

    public static void setTaskPosition(int task_id, int position, int serviceId) {
        String query = "UPDATE Tasks SET position = position + 1 WHERE service_id = " + serviceId + " and position >=" + position;
        PersistenceManager.executeUpdate(query);
        query = "UPDATE Tasks SET position = " + position + " WHERE service_id = " + serviceId + " and id =" + task_id;
        PersistenceManager.executeUpdate(query);
    }

    public static int downTask(int taskId, int position, int serviceId) {

        final Integer[] max = new Integer[1];

        String query = "SELECT MAX(position) as max FROM Tasks WHERE service_id = " + serviceId;
        PersistenceManager.executeQuery(query, rs -> max[0] = rs.getInt("max"));

        if (max[0].equals(position)) {
            return position;
        }

        int newPostion = position + 1;

        final Integer[] taskToChangeId = new Integer[1];

        query = "SELECT id FROM Tasks WHERE position = " + newPostion + " and service_id = " + serviceId;
        PersistenceManager.executeQuery(query, rs -> taskToChangeId[0] = rs.getInt("id"));

        query = "UPDATE Tasks SET position = " + newPostion + " WHERE id= " + taskId;
        PersistenceManager.executeUpdate(query);


        query = "UPDATE Tasks SET position = " + position + " WHERE id= " + taskToChangeId[0];
        PersistenceManager.executeUpdate(query);

        return newPostion;
    }

    public void assignCook(double quantity, double time, int task, int cook, int kitchenTurn) {
        String query = "UPDATE Tasks SET  time = " + time + ", quantity = " + quantity + ", kitchen_turn_id = " + kitchenTurn + ", user_id = " + cook + " WHERE id = " + task;
        PersistenceManager.executeUpdate(query);

        this.quantity = quantity;
        this.time = time;
        this.cook = User.loadUserById(cook);
        this.kitchenTurn = KitchenTurn.getKitchenTurnById(kitchenTurn);
    }

    public void removeCook(int taskId) {
        String query = "UPDATE Tasks SET kitchen_turn_id = null, user_id = null WHERE id = " + taskId;
        PersistenceManager.executeUpdate(query);

        this.cook = null;
        this.kitchenTurn = null;
    }

    public static void updateTask(double quantity, double time, int id) {
        String query = "UPDATE Tasks SET  time = " + time + ", quantity = " + quantity + " WHERE id = " + id;
        PersistenceManager.executeUpdate(query);
    }

    public void editTask(double quantity, double time) {
        this.quantity = quantity;
        this.time = time;
    }

    @Override
    public String toString() {
        String extra = (extraMenu) ? ", (Fuori Menù)" : "";
        String cookString = (cook != null) ? ", (Assegnato a " + cook.getUserName() + ")" : "";
        String completed = this.isCompleted() ? "Completato" : "Non completato";
        return kitchenProcedure.toString() + ", Porzioni: " + quantity + ", Tempo Previsto: " + time + " minuti" + cookString + extra + "Stato: " + completed +  ".";
    }

    public User getCook() {
        return cook;
    }

    public KitchenProcedure getKitchenProcedure() {
        return kitchenProcedure;
    }

    public boolean isExtraMenu() {
        return extraMenu;
    }

    public int getServiceId() {
        return serviceId;
    }

    @Override
    public int compareTo(Task o) {
        return position.compareTo(o.position);
    }

    public Integer getPosition() {
        return position;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getId() {
        return id;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTime() {
        return time;
    }

    public KitchenTurn getKitchenTurn() {
        return kitchenTurn;
    }

    public static Task loadById(int taskId) {
        AtomicReference<Task> t = new AtomicReference<>();
        String query = "SELECT * FROM Tasks WHERE id = " + taskId;
        PersistenceManager.executeQuery(query, rs -> {
                    Task task = new Task();
                    task.id = rs.getInt("id");
                    task.completed = rs.getBoolean("completed");
                    task.quantity = rs.getDouble("quantity");
                    task.time = rs.getDouble("time");
                    task.cook = User.loadUserById(rs.getInt("user_id"));
                    task.serviceId = rs.getInt("service_id");
                    task.position = rs.getInt("position");
                    task.extraMenu = rs.getBoolean("extra_menu");
                    task.kitchenTurn = KitchenTurn.getKitchenTurnById(rs.getInt("kitchen_turn_id"));

                    task.kitchenProcedure = KitchenProcedure.loadKitchenProcedureById(rs.getInt("kitchen_procedure_id"));
                    t.set(task);
                }
        );

        return t.get();
    }
}

package businesslogic.user;

import javafx.collections.FXCollections;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User implements Comparable<User> {

    private static final Map<Integer, User> loadedUsers = FXCollections.observableHashMap();
    private final Set<Role> roles;
    private int id;
    private String username;

    public User() {
        id = 0;
        username = "";
        this.roles = new HashSet<>();
    }

    public static User loadUserById(int uid) {

        if (uid == 0) return null;

        if (loadedUsers.containsKey(uid)) return loadedUsers.get(uid);

        User load = new User();
        String userQuery = "SELECT * FROM Users WHERE id='" + uid + "'";
        PersistenceManager.executeQuery(userQuery, rs -> {
            load.id = rs.getInt("id");
            load.username = rs.getString("username");
        });
        if (load.id > 0) {
            loadedUsers.put(load.id, load);
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + load.id;
            PersistenceManager.executeQuery(roleQuery, handleRole(load));
        }
        return load;
    }

    private static ResultHandler handleRole(User load) {
        return new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String role = rs.getString("role_id");
                switch (role.charAt(0)) {
                    case 'c':
                        load.roles.add(Role.CUOCO);
                        break;
                    case 'h':
                        load.roles.add(Role.CHEF);
                        break;
                    case 'o':
                        load.roles.add(Role.ORGANIZZATORE);
                        break;
                    case 's':
                        load.roles.add(Role.SERVIZIO);
                }
            }
        };
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String userQuery = "SELECT * FROM Users";

        PersistenceManager.executeQuery(userQuery, rs -> {
            User u = new User();
            u.id = rs.getInt("id");
            u.username = rs.getString("username");
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + u.id;
            PersistenceManager.executeQuery(roleQuery, handleRole(u));
            users.add(u);
        });
        return users;
    }

    public static User loadUser(String username) {
        User u = new User();
        String userQuery = "SELECT * FROM Users WHERE username='" + username + "'";
        PersistenceManager.executeQuery(userQuery, rs -> {
            u.id = rs.getInt("id");
            u.username = rs.getString("username");
        });
        if (u.id > 0) {
            loadedUsers.put(u.id, u);
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + u.id;
            PersistenceManager.executeQuery(roleQuery, handleRole(u));
        }
        return u;
    }

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    public boolean isChef() {
        return roles.contains(Role.CHEF);
    }

    public boolean isCook() {
        return roles.contains(Role.CUOCO);
    }

    public boolean isOrganizer() {
        return roles.contains(Role.ORGANIZZATORE);
    }
    // STATIC METHODS FOR PERSISTENCE

    public String getUserName() {
        return username;
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        String result = username;
        if (roles.size() > 0) {
            result += ": ";

            for (User.Role r : roles) {
                result += r.toString() + "";
            }
        }
        return result;
    }

    public enum Role {SERVIZIO, CUOCO, CHEF, ORGANIZZATORE}


}

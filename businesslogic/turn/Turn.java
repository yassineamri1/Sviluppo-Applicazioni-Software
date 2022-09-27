package businesslogic.turn;

import businesslogic.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class Turn {
    protected int id;
    protected List<User> usersAvailable;
    protected boolean completed;
    protected LocalDateTime start;
    protected LocalDateTime end;

    public int getId() {
        return id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String completed = (this.completed) ? "Al completo" : "Non completo";
        StringBuilder availableCooks = new StringBuilder();
        for (User cook: this.usersAvailable) {
            availableCooks.append(cook + "; ");
        }
        return "\nInizio: " + start.format(formatter) + " Fine: " + end.format(formatter) + " [" + completed + "] , Disponibili: " + availableCooks;
    }

    public boolean isEnded() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(end);
    }

    public boolean isUserAvailable(User user) {
        for (User u : this.usersAvailable) {
            if (u.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }
}

package businesslogic.kitchen;

import businesslogic.event.ServiceInfo;
import businesslogic.turn.KitchenTurn;
import businesslogic.user.User;

import java.util.ArrayList;
import java.util.Comparator;

public class Sheet {
    public ArrayList<Task> tasks;
    private ServiceInfo service;

    public Sheet create(ServiceInfo service) {
        tasks = new ArrayList<>();
        this.service = service;
        return this;
    }

    public static Sheet loadByServiceInfo(ServiceInfo service) {
        Sheet sheet = (new Sheet()).create(service);
        sheet.tasks.addAll(Task.getTasksByServiceId(service.getId()));
        return sheet;
    }


    public ServiceInfo getService() {
        return service;
    }

    public void deleteTask(Task task) {
        for (Task t : tasks) {
            if (t.getId() == task.getId()) {
                tasks.remove(t);
                return;
            }
        }
    }

    public void assignCook(User cook, Task task, KitchenTurn kitchenTurn, double quantity, double time) {
        for (Task t : tasks) {
            if (t.getId() == task.getId()) {
                task.assignCook(quantity, time, task.getId(), cook.getId(), kitchenTurn.getId());
                return;
            }
        }
    }


    public void removeCook(Task task) {
        for (Task t : tasks) {
            if (t.getId() == task.getId()) {
                task.removeCook(task.getId());
                return;
            }
        }
    }

    @Override
    public String toString() {
        ArrayList<Task> tasks = (ArrayList<Task>) this.tasks.clone();
        tasks.sort(Comparator.comparing(Task::getPosition));
        return "Sheet{" +
                "tasks=" + tasks +
                ",\n service=" + service +
                '}';
    }
}

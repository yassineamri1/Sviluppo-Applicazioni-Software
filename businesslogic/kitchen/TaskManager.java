package businesslogic.kitchen;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.turn.KitchenTurn;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;


public class TaskManager {

    private ArrayList<KitchenTaskEventReceiver> eventReceivers = new ArrayList<>();

    private Sheet currentSheet;

    public void assignCook(double quantity, double time, Task task, User cook, KitchenTurn kitchenTurn, boolean splitTask) throws UseCaseLogicException {
        if ((CatERing.getInstance().getTurnManager().cookAvailableTime(cook, kitchenTurn) - time) < 0)
            throw new CookDoesentHaveTimeException();
        if (!cook.isCook()) throw new UseCaseLogicException();
        if (task.isCompleted()) throw new UseCaseLogicException();
        if (kitchenTurn.isCompleted() || kitchenTurn.isEnded()) throw new UseCaseLogicException();
        if (!kitchenTurn.isUserAvailable(cook)) throw new UseCaseLogicException();

        if (task.getCook() == null || !splitTask) {
            currentSheet.assignCook(cook, task, kitchenTurn, quantity, time);
            return;
        }

        if (task.getCook() != null) {
            int newTaskId = Task.saveTask(task.getKitchenProcedure().getId(), task.getServiceId(), task.isExtraMenu());
            Task newTask = Task.loadById(newTaskId);
            currentSheet.assignCook(cook, newTask, kitchenTurn, quantity, time);
            currentSheet.assignCook(task.getCook(), task, task.getKitchenTurn(), task.getQuantity() - quantity, task.getTime() - time);
            Task.setTaskPosition(newTaskId, task.getPosition() + 1, task.getServiceId());
            return;
        }

        throw new UseCaseLogicException();
    }


    public void deleteTask(Task task) throws UseCaseLogicException {

        if (!task.isExtraMenu()) {
            throw new UseCaseLogicException();
        }
        currentSheet.deleteTask(task);
        notifyDeletedExtraMenuTask(task);
    }

    public void removeCookFromTask(Task task) throws UseCaseLogicException {
        if (task.isCompleted()) throw new UseCaseLogicException();
        if (task.getCook() == null) return;

        currentSheet.removeCook(task);
    }

    public void editTask(Task task, double quantity, double time) throws UseCaseLogicException {
        if (task.isCompleted()) throw new UseCaseLogicException();
        if (task.getCook() != null && (CatERing.getInstance().getTurnManager().cookAvailableTime(task.getCook(), task.getKitchenTurn()) + task.getTime() - time) < 0)
            throw new CookDoesentHaveTimeException();
        task.editTask(quantity, time);
        notifyEditedTask(task);
    }


    public void addExtraMenuTask(KitchenProcedure kitchenProcedure) throws UseCaseLogicException {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        ServiceInfo service = currentSheet.getService();

        if (!service.hasMenu() || currentUser.getId() != service.getChefId()) {
            throw new UseCaseLogicException();
        }

        createAllTask(kitchenProcedure, service, true);

    }

    public Sheet createSheet(ServiceInfo service) throws UseCaseLogicException {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();

        if (!service.hasMenu() || currentUser.getId() != service.getChefId()) {
            throw new UseCaseLogicException();
        }

        currentSheet = (new Sheet()).create(service);

        service.getAcceptedMenu().getFreeItems().forEach(
                x -> createAllTask(
                        KitchenProcedure.loadKitchenProcedureById(x.getItemRecipe().getId()), service, false)
        );

        service.getAcceptedMenu().getSections().forEach(
                x -> x.getItems().forEach(
                        y -> createAllTask(
                                KitchenProcedure.loadKitchenProcedureById(y.getItemRecipe().getId()), service, false)
                )
        );


        notifySheetCreated(currentSheet);
        return currentSheet;
    }

    private void createAllTask(KitchenProcedure kc, ServiceInfo service, boolean extraMenu) {

        kc.getIngredients().values().forEach(x -> {
            createAllTask(x, service, extraMenu);
        });


        Task task = Task.create(kc, service, extraMenu);
        this.currentSheet.tasks.add(task);
        if (extraMenu) {
            notifyAddedExtraMenuTask(task);
        }
    }


    public int upTask(Task task) {
        return Task.upTask(task.getId(), task.getPosition(), task.getServiceId());
    }

    public int downTask(Task task) {
        return Task.downTask(task.getId(), task.getPosition(), task.getServiceId());
    }

    public Sheet getCurrentSheet() {
        return currentSheet;
    }

    public void setCurrentSheet(Sheet currentSheet) {
        this.currentSheet = currentSheet;
    }

    public void addEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public void removeEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }

    public void notifySheetCreated(Sheet sheet) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateSheetCreated(sheet);
        }
    }

    private void notifyAddedExtraMenuTask(Task task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateAddedExtraMenuTask(task);
        }
    }

    private void notifyDeletedExtraMenuTask(Task task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateDeletedExtraMenuTask(task);
        }
    }

    private void notifyEditedTask(Task task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateEditedTask(task);
        }
    }
}

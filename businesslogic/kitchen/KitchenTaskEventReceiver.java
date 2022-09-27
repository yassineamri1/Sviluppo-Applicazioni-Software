package businesslogic.kitchen;


public interface KitchenTaskEventReceiver {
    public void updateSheetCreated(Sheet sheet);

    public void updateAddedExtraMenuTask(Task task);

    public void updateDeletedExtraMenuTask(Task task);

    public void updateEditedTask(Task task);
}

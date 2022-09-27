package businesslogic;

import businesslogic.event.EventManager;
import businesslogic.kitchen.TaskManager;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.KitchenProcedureManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.turn.TurnManager;
import businesslogic.user.UserManager;
import persistence.KitchenTaskPersistence;
import persistence.MenuPersistence;

public class CatERing {
    private static CatERing singleInstance;
    private final MenuManager menuMgr;
    private final RecipeManager recipeMgr;
    private final UserManager userMgr;
    private final EventManager eventMgr;
    private final TaskManager taskManager;
    private final KitchenProcedureManager kitchenProcedureManager;
    private final TurnManager turnManager;
    private final MenuPersistence menuPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        menuPersistence = new MenuPersistence();
        menuMgr.addEventReceiver(menuPersistence);
        taskManager = new TaskManager();
        taskManager.addEventReceiver(new KitchenTaskPersistence());
        kitchenProcedureManager = new KitchenProcedureManager();
        turnManager = new TurnManager();
    }

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() {
        return eventMgr;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public KitchenProcedureManager getKitchenProcedureManager() {
        return kitchenProcedureManager;
    }
}

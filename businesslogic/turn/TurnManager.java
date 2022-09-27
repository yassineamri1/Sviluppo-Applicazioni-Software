package businesslogic.turn;

import businesslogic.user.User;

import java.util.ArrayList;

public class TurnManager {

    public ArrayList<KitchenTurn> getAllKitchenTurns() {
        return KitchenTurn.getAllKitchenTurns();
    }

    public double cookAvailableTime(User user, KitchenTurn kitchenTurn) {
        return KitchenTurn.getAvailableTimeForAUser(user.getId(), kitchenTurn.getId());
    }

    public void setAsCompleted(KitchenTurn kitchenTurn) {
        KitchenTurn.setKitchenTurnAsCompleted(kitchenTurn.getId());
    }

    public void setAsUncompleted(KitchenTurn kitchenTurn) {
        KitchenTurn.setKitchenTurnAsUncompleted(kitchenTurn.getId());
    }
}

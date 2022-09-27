package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KitchenProcedureManager {

    public ObservableList<KitchenProcedure> getKitchenProcedure() {
        return FXCollections.unmodifiableObservableList(KitchenProcedure.loadAllKitchenProcedure());
    }
}

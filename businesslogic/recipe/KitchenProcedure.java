package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;

import java.util.HashMap;
import java.util.Map;

public abstract class KitchenProcedure {

    public static final String RECIPE = "R";
    public static final String PREPARATION = "P";

    private static final Map<Integer, KitchenProcedure> all = new HashMap<>();

    protected int id;
    protected String name;
    protected Map<Integer, Preparation> ingredients = new HashMap<>();

    public static ObservableList<KitchenProcedure> loadAllKitchenProcedure() {
        String query = "SELECT * FROM KitchenProcedure ";
        PersistenceManager.executeQuery(query, rs -> {
            String type = rs.getString("type");
            int id = rs.getInt("id");

            if (all.containsKey(id)) return;

            if (type.equals(RECIPE)) {
                Recipe rec = getRecipeById(id);
                all.put(rec.id, rec);
                return;
            }

            Preparation rec = getPreparationById(id);
            all.put(rec.id, rec);
        });

        return FXCollections.observableArrayList(all.values());
    }

    public static KitchenProcedure loadKitchenProcedureById(int id) {
        if (all.containsKey(id)) return all.get(id);
        final String[] type = {""};
        String query = "SELECT * FROM KitchenProcedure WHERE id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            type[0] = rs.getString("type");
        });
        if (type[0].equals(RECIPE)) {
            Recipe rec = getRecipeById(id);
            all.put(rec.id, rec);
            return rec;
        }

        Preparation rec = getPreparationById(id);
        all.put(rec.id, rec);
        return rec;
    }

    private static Recipe getRecipeById(int id) {
        Recipe rec = new Recipe();
        String query = "SELECT * FROM Recipes WHERE id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            rec.name = rs.getString("name");
            rec.id = id;
        });

        query = "SELECT p.name, p.id FROM `Ingredients` AS i JOIN `Preparation` AS p ON preparation_id = p.id WHERE kitchen_procedure_id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            if (!rs.isFirst()) {
                return;
            }

            do {
                Preparation subPreparation = getPreparationById(rs.getInt("id"));
                rec.ingredients.put(subPreparation.id, subPreparation);
            } while (rs.next());
        });

        return rec;
    }

    private static Preparation getPreparationById(int id) {
        Preparation preparation = new Preparation();
        String query = "SELECT * FROM Preparation WHERE id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            preparation.name = rs.getString("name");
            preparation.id = id;
        });

        query = "SELECT p.name, p.id FROM `Ingredients` AS i JOIN `Preparation` AS p ON preparation_id = p.id WHERE kitchen_procedure_id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            if (!rs.isFirst()) {
                return;
            }

            do {
                Preparation subPreparation = getPreparationById(rs.getInt("id"));
                preparation.ingredients.put(subPreparation.id, subPreparation);
            } while (rs.next());
        });

        return preparation;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name;
    }

    public abstract String getType();

    public Map<Integer, Preparation> getIngredients() {
        return ingredients;
    }


}

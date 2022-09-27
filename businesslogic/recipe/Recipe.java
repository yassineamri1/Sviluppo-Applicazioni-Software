package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Recipe extends KitchenProcedure {
    private static final Map<Integer, Recipe> all = new HashMap<>();

    public Recipe() {

    }

    public Recipe(String name) {
        id = 0;
        this.name = name;
    }

    public static ObservableList<Recipe> loadAllRecipes() {
        String query = "SELECT * FROM Recipes";
        PersistenceManager.executeQuery(query, rs -> {
            int id = rs.getInt("id");
            if (all.containsKey(id)) {
                Recipe rec = all.get(id);
                rec.name = rs.getString("name");
            } else {
                Recipe rec = new Recipe(rs.getString("name"));
                rec.id = id;
                all.put(rec.id, rec);
            }
        });
        ObservableList<Recipe> ret = FXCollections.observableArrayList(all.values());
        Collections.sort(ret, Comparator.comparing(KitchenProcedure::getName));
        return ret;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Recipe> getAllRecipes() {
        return FXCollections.observableArrayList(all.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (all.containsKey(id)) return all.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM Recipes WHERE id = " + id;
        PersistenceManager.executeQuery(query, rs -> {
            rec.name = rs.getString("name");
            rec.id = id;
            all.put(rec.id, rec);
        });
        return rec;
    }

    public String getType() {
        return KitchenProcedure.RECIPE;
    }


}

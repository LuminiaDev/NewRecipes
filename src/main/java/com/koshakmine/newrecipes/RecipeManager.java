package com.koshakmine.newrecipes;

import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.item.Item;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RecipeManager {
    private static final Gson PARSER = new Gson();

    public static void registerShapedRecipe(NewRecipes mainClass, String recipe_path) {
        try (var jarFile = new JarFile(mainClass.getFile())) {
            var serverRecipeManager = mainClass.getServer().getCraftingManager();
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith(recipe_path) && !entry.isDirectory()) {
                    InputStream inputStream = mainClass.getClass().getClassLoader().getResourceAsStream(name);
                    assert inputStream != null;
                    var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    var recipe = parseShapedRecipe(PARSER.fromJson(inputStreamReader, Map.class));
                    if(recipe != null) {
                        serverRecipeManager.registerRecipe(527, recipe);
                        serverRecipeManager.registerRecipe(649, recipe);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerShapelessRecipe(NewRecipes mainClass, String recipe_path) {
        try (var jarFile = new JarFile(mainClass.getFile())) {
            var serverRecipeManager = mainClass.getServer().getCraftingManager();
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith(recipe_path) && !entry.isDirectory()) {
                    InputStream inputStream = mainClass.getClass().getClassLoader().getResourceAsStream(name);
                    assert inputStream != null;
                    var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    var recipe = parseShapelessRecipe(PARSER.fromJson(inputStreamReader, Map.class));
                    if(recipe != null) {
                        serverRecipeManager.registerRecipe(527, recipe);
                        serverRecipeManager.registerRecipe(649, recipe);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ShapelessRecipe parseShapelessRecipe(Map<?, ?> recipeMap) {
        if (recipeMap.containsKey("minecraft:recipe_shapeless")) {
            var shape = (Map<?, ?>) recipeMap.get("minecraft:recipe_shapeless");
            var identifier = (String) ((Map<?, ?>) shape.get("description")).get("identifier");
            List<Map<String, Object>> ingredientsMap = (List<Map<String, Object>>) shape.get("ingredients");
            Collection<Item> ingredients = new ArrayList<>();
            for (Map<String, Object> ingredient : ingredientsMap) {
                String itemId = (String) ingredient.get("item");
                Item item = Item.fromString(itemId);

                if (ingredient.containsKey("data")) {
                    Number dataNumber = (Number) ingredient.get("data");
                    item.setDamage(dataNumber.intValue());
                }

                ingredients.add(item);
            }
            Item output = Item.fromString((String) ((Map<?, ?>) shape.get("result")).get("item"));
            if(((Map<?, ?>) shape.get("result")).get("data") != null) {
                output.setDamage(((Number) ((Map<?, ?>) shape.get("result")).get("data")).intValue());
            }
            if(((Map<?, ?>) shape.get("result")).get("count") != null) {
                output.setCount(((Number) ((Map<?, ?>) shape.get("result")).get("count")).intValue());
            }

            //Check for unimplemented items/blocks
            for (Item ingredient : ingredients) {
                if(ingredient.getId() == Item.AIR) {
                    return null;
                }
            }

            if(output.getId() == Item.AIR) return null;

            return new ShapelessRecipe(identifier, 1, output, ingredients);
        } else {
            return null;
        }
    }

    private static ShapedRecipe parseShapedRecipe(Map<?, ?> recipeMap) {
        if (recipeMap.containsKey("minecraft:recipe_shaped")) {
            var shape = (Map<?, ?>) recipeMap.get("minecraft:recipe_shaped");
            var identifier = (String) ((Map<?, ?>) shape.get("description")).get("identifier");
            var tags = (String) ((List<?>) shape.get("tags")).get(0);
            var pattern = ((List<?>) shape.get("pattern")).stream().map(a -> (String) a).toList().toArray(new String[]{});
            var ingredients = ((Map<?, ?>) shape.get("key")).entrySet().stream().map(e -> {
                var key = ((String) e.getKey()).toCharArray()[0];
                var value = (Map<?, ?>) e.getValue();
                String item = (String) value.get("item");
                if(item == null) item = "minecraft:air";
                return Map.entry(key, Item.fromString(item));
            }).collect(HashMap<Character, Item>::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
            var output = Item.fromString((String) ((Map<?, ?>) shape.get("result")).get("item"));
            if(((Map<?, ?>) shape.get("result")).get("data") != null) {
                output.setDamage(((Number) ((Map<?, ?>) shape.get("result")).get("data")).intValue());
            }
            if(((Map<?, ?>) shape.get("result")).get("count") != null) {
                output.setCount(((Number) ((Map<?, ?>) shape.get("result")).get("count")).intValue());
            }

            //Check for unimplemented items/blocks
            AtomicReference<String> gg = new AtomicReference<>("gg");
            ingredients.forEach(((character, item) -> {
                if(item.getId() == Item.AIR) {
                    gg.set(null);
                }
            }));
            if(gg.get() == null) return null;

            if(output.getId() == Item.AIR) return null;

            return new ShapedRecipe(identifier, 1, output, pattern, ingredients, List.of());
        } else {
            return null;
        }
    }
}

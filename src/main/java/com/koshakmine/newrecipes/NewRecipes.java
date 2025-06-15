package com.koshakmine.newrecipes;

import cn.nukkit.plugin.PluginBase;

public class NewRecipes extends PluginBase {
    @Override
    public void onLoad() {
        RecipeManager.registerShapedRecipe(this, "recipes/");
        RecipeManager.registerShapelessRecipe(this, "recipes/");
        this.getServer().getCraftingManager().rebuildPacket();
    }
}
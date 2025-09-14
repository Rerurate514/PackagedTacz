package com.rerurate.packagedtacz.recipes

import com.rerurate.packagedtacz.blocks.PackagedTaczBlocks
import com.tacz.guns.init.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import thelm.packagedauto.block.CrafterBlock
import java.util.function.Consumer

class ModRecipeProvider(output: PackOutput): RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe?>) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PackagedTaczBlocks.TACZ_MOLECULAR_ASSEMBLER.get())
            .pattern("iCi")
            .pattern("wgw")
            .pattern("iri")
            .define('i', Items.IRON_INGOT)
            .define('C', CrafterBlock.ITEM_INSTANCE)
            .define('w', Items.CRAFTING_TABLE)
            .define('g', ModBlocks.GUN_SMITH_TABLE.get())
            .define('r', Items.REPEATER)
            .unlockedBy("has_gun_smith_table", has(ModBlocks.GUN_SMITH_TABLE.get()))
            .save(consumer)
    }
}
package com.rerurate.packagedtacz

import com.rerurate.packagedtacz.recipes.ModRecipeProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = Packagedtacz.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModDataGenerators {
    @JvmStatic
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput

        generator.addProvider(event.includeServer(), ModRecipeProvider(packOutput))
    }
}
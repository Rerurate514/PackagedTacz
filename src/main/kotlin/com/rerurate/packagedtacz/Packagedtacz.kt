package com.rerurate.packagedtacz

import com.rerurate.packagedtacz.blockEntities.PackagedTaczBlockEntities
import com.rerurate.packagedtacz.blocks.PackagedTaczBlocks
import com.rerurate.packagedtacz.client.gui.screen.TaczMolecularAssemblerScreen
import com.rerurate.packagedtacz.items.PackagedTaczItems
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Packagedtacz.ID)
class Packagedtacz {
    companion object {
        const val ID = "packagedtacz"
        val LOGGER: Logger = LogManager.getLogger(ID)
    }

    init {
        //A1TICItems.initialize(MOD_BUS)
        //MOD_BUS.register(ModDataGenerators::class.java)
        MOD_BUS.register(this)
        PackagedTaczItems.initialize(MOD_BUS)
        PackagedTaczBlocks.initialize(MOD_BUS)
        PackagedTaczBlockEntities.initialize(MOD_BUS)
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ItemBlockRenderTypes.setRenderLayer(
                PackagedTaczBlocks.TACZ_MOLECULAR_ASSEMBLER.get(),
                RenderType.cutout()
            )
            MenuScreens.register(PackagedTaczBlocks.TACZ_MOLECULAR_ASSEMBLER_CONTAINER.get(), ::TaczMolecularAssemblerScreen)
        }
    }
}
package com.rerurate.packagedtacz

import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Packagedtacz.ID)
class Packagedtacz {
    companion object {
        const val ID = "Packagedtacz"
        val LOGGER: Logger = LogManager.getLogger(ID)
    }

    init {
        //A1TICItems.initialize(MOD_BUS)
        //MOD_BUS.register(ModDataGenerators::class.java)
    }
}
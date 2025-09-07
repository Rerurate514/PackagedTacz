package com.rerurate.packagedtacz.items

import appeng.items.materials.MaterialItem
import com.rerurate.packagedtacz.Packagedtacz
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object PackagedTaczItems {
    val TABS: DeferredRegister<CreativeModeTab?> = DeferredRegister.create<CreativeModeTab?>(
        Registries.CREATIVE_MODE_TAB,
        Packagedtacz.ID
    )

    val ITEMS: DeferredRegister<Item?> = DeferredRegister.create(
        ForgeRegistries.ITEMS,
        Packagedtacz.ID
    )

    fun initialize(bus: IEventBus?) {
        TABS.register(bus)
        ITEMS.register(bus)
    }

    fun basic(): Item {
        return MaterialItem(properties())
    }

    fun properties(): Item.Properties {
        return Item.Properties()
    }
}
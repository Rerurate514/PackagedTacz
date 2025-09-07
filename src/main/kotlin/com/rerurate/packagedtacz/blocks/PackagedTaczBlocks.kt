package com.rerurate.packagedtacz.blocks

import com.rerurate.packagedtacz.items.PackagedTaczItems
import com.rerurate.packagedtacz.Packagedtacz
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.network.IContainerFactory
import com.rerurate.packagedtacz.containers.TaczMolecularAssemblerContainer
import com.rerurate.packagedtacz.items.PackagedTaczItems.ITEMS
import com.rerurate.packagedtacz.items.PackagedTaczItems.TABS
import net.minecraft.network.chat.Component
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object PackagedTaczBlocks {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, Packagedtacz.ID)
    val CONTAINERS: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Packagedtacz.ID)

    val TACZ_MOLECULAR_ASSEMBLER: RegistryObject<Block> =
        registerBlock("tacz_molecular_assembler",
    Supplier {
            TaczMolecularAssembler(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .randomTicks()
                .strength(1.5F)
                .sound(SoundType.METAL))
    })

    val TACZ_MOLECULAR_ASSEMBLER_CONTAINER =
        CONTAINERS.register("tacz_molecular_assembler_container", Supplier {
            MenuType(
                IContainerFactory { windowId, inv, data ->
                    TaczMolecularAssemblerContainer(windowId, inv, data.readBlockPos())
                },
                FeatureFlags.VANILLA_SET
            )
        })

    private fun <T : Block> registerBlock(name: String, block: Supplier<T>): RegistryObject<T> {
        val returnObj: RegistryObject<T> = BLOCKS.register(name, block)
        registerBlockItem(name, returnObj)
        return returnObj
    }

    private fun <T : Block> registerBlockItem(name: String, block: RegistryObject<T>): RegistryObject<Item> {
        return PackagedTaczItems.ITEMS.register(name) { BlockItem(block.get(), Item.Properties()) }
    }

    fun initialize(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
        CONTAINERS.register(eventBus)
    }

    val CREATIVE_TAB: RegistryObject<CreativeModeTab?> = TABS.register(
        "main",
        {
            CreativeModeTab.builder()
                .title(Component.translatable("PackagedTacz"))
                .icon { ItemStack(TACZ_MOLECULAR_ASSEMBLER.get()) }
                .displayItems { _, output ->
                    for (entry in ITEMS.entries) {
                        output.accept(entry.get())
                    }
                }
                .build()
        })
}
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
import com.rerurate.packagedtacz.blockEntities.TaczMolecularAssemblerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

object PackagedTaczBlocks {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, Packagedtacz.ID)

    val TACZ_MOLECULAR_ASSEMBLER: RegistryObject<Block> =
        registerBlock("tacz_molecular_assembler",
            Supplier {
                object : BaseEntityBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.METAL)) {
                    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
                        return TaczMolecularAssemblerBlockEntity(pos, state)
                    }

                    override fun getRenderShape(state: BlockState): RenderShape {
                        return RenderShape.MODEL
                    }
                }
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
    }
}
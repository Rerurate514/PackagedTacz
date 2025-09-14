package com.rerurate.packagedtacz.blockEntities

import com.rerurate.packagedtacz.Packagedtacz
import com.rerurate.packagedtacz.blocks.PackagedTaczBlocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object PackagedTaczBlockEntities {
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(
        ForgeRegistries.BLOCK_ENTITY_TYPES,
        Packagedtacz.Companion.ID
    )

    fun initialize(bus: IEventBus?) {
        BLOCK_ENTITIES.register(bus)
    }

    val TACZ_MOLECULAR_ASSEMBLER: RegistryObject<BlockEntityType<TaczMolecularAssemblerBlockEntity>> =
        BLOCK_ENTITIES.register(
            "tacz_molecular_assembler",
            {
                BlockEntityType.Builder.of(
                    ::TaczMolecularAssemblerBlockEntity,
                    PackagedTaczBlocks.TACZ_MOLECULAR_ASSEMBLER.get()
                ).build(null)
            }
        )
}
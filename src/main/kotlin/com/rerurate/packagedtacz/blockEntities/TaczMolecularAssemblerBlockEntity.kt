package com.rerurate.packagedtacz.blockEntities

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class TaczMolecularAssemblerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(PackagedTaczBlockEntities.TACZ_MOLECULAR_ASSEMBLER.get(), pos, state) {

    var yourData: Int = 0

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("tacz_molecular_assembler_data", yourData)
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        yourData = tag.getInt("tacz_molecular_assembler_data")
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(): CompoundTag {
        return this.saveWithoutMetadata()
    }
}
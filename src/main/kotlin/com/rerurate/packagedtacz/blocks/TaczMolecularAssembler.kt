package com.rerurate.packagedtacz.blocks

import com.rerurate.packagedtacz.blockEntities.TaczMolecularAssemblerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import net.minecraft.server.level.ServerPlayer

class TaczMolecularAssembler(properties: BlockBehaviour.Properties) : BaseEntityBlock(properties) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return TaczMolecularAssemblerBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(
        blockState: BlockState,
        level: Level,
        pos: BlockPos,
        state: BlockState,
        isMoving: Boolean,
    ) {
        if (!blockState.`is`(state.block)) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is TaczMolecularAssemblerBlockEntity) {
                blockEntity.dropAllItems(level, pos)
            }
        }

        super.onRemove(blockState, level, pos, state, isMoving)
    }

    @Deprecated("Deprecated in Java")
    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is TaczMolecularAssemblerBlockEntity) {
                NetworkHooks.openScreen(player as ServerPlayer, blockEntity as MenuProvider, pos)
            }
        }
        return InteractionResult.SUCCESS
    }
}
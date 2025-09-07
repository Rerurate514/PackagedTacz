package com.rerurate.packagedtacz.blockEntities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import thelm.packagedauto.item.RecipeHolderItem
import net.minecraft.world.MenuProvider
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block
import com.rerurate.packagedtacz.containers.TaczMolecularAssemblerContainer

class TaczMolecularAssemblerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(PackagedTaczBlockEntities.TACZ_MOLECULAR_ASSEMBLER.get(), pos, state), MenuProvider {

    private val itemHandler = object : ItemStackHandler(18) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.item is RecipeHolderItem
        }
        override fun onContentsChanged(slot: Int) {
            setChanged()
            if (level != null && !level!!.isClientSide) {
                level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
            }
        }
    }
    private val itemHandlerOptional = LazyOptional.of { itemHandler as IItemHandler }

    private val materialsHandler = ItemStackHandler(9)
    private val materialsHandlerOptional = LazyOptional.of { materialsHandler as IItemHandler }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("recipes", itemHandler.serializeNBT())
        tag.put("materials", materialsHandler.serializeNBT())
        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        itemHandler.deserializeNBT(tag.getCompound("recipes"))
        materialsHandler.deserializeNBT(tag.getCompound("materials"))
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(): CompoundTag {
        return this.saveWithoutMetadata()
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) {
                return materialsHandlerOptional.cast()
            } else {
                return itemHandlerOptional.cast()
            }
        }
        return super.getCapability(cap, side)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerOptional.invalidate()
        materialsHandlerOptional.invalidate()
    }

    fun getRecipeHandler(): LazyOptional<IItemHandler> {
        return itemHandlerOptional
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.packagedtacz.tacz_molecular_assembler")
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return TaczMolecularAssemblerContainer(containerId, inventory, blockPos)
    }
}
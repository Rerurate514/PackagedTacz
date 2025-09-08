package com.rerurate.packagedtacz.blockEntities

import java.util.List;
import appeng.api.crafting.IPatternDetails
import appeng.api.implementations.IPowerChannelState
import appeng.api.implementations.blockentities.ICraftingMachine
import appeng.api.implementations.blockentities.PatternContainerGroup
import appeng.api.inventories.InternalInventory
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.KeyCounter
import appeng.api.util.AECableType
import appeng.blockentity.grid.AENetworkInvBlockEntity
import appeng.core.definitions.AEBlocks
import appeng.util.inv.AppEngInternalInventory
import com.rerurate.packagedtacz.blocks.PackagedTaczBlocks
import com.rerurate.packagedtacz.containers.TaczMolecularAssemblerContainer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import thelm.packagedauto.item.RecipeHolderItem

class TaczMolecularAssemblerBlockEntity(pos: BlockPos, state: BlockState) :
    MenuProvider,
    AENetworkInvBlockEntity(PackagedTaczBlockEntities.TACZ_MOLECULAR_ASSEMBLER.get(), pos, state),
    ICraftingMachine,
    IPowerChannelState
{
    private val internalInv = AppEngInternalInventory(this, 256, 64)
    private val gridInv = AppEngInternalInventory(this, 256, 64)
    private var isPowered = false

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

    override fun loadTag(tag: CompoundTag) {
        super.loadTag(tag)
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

    override fun getInternalInventory(): InternalInventory? {
        return this.internalInv;
    }

    override fun onChangeInventory(p0: InternalInventory?, p1: Int) {

    }

    override fun getCraftingMachineInfo(): PatternContainerGroup {
        val icon = AEItemKey.of(AEBlocks.MOLECULAR_ASSEMBLER)

        val name = if (hasCustomName()) {
            customName
        } else {
            AEBlocks.MOLECULAR_ASSEMBLER.asItem().description
        }

        val tooltip = listOf<Component>()

        return PatternContainerGroup(icon, name, tooltip)
    }

    override fun pushPattern(
        p0: IPatternDetails?,
        p1: Array<out KeyCounter?>?,
        p2: Direction?
    ): Boolean {
        return false
    }

    override fun acceptsPlans(): Boolean {
        return false
    }

    override fun isPowered(): Boolean {
        return isPowered;
    }

    override fun isActive(): Boolean {
        return this.isPowered;
    }

    override fun getCableConnectionType(dir: Direction?): AECableType? {
        return AECableType.SMART
    }
}
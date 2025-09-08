package com.rerurate.packagedtacz.containers

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import thelm.packagedauto.item.RecipeHolderItem
import com.rerurate.packagedtacz.blocks.PackagedTaczBlocks
import com.rerurate.packagedtacz.blockEntities.TaczMolecularAssemblerBlockEntity
import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.item.AmmoBoxItem
import com.tacz.guns.item.AmmoItem
import com.tacz.guns.item.AttachmentItem
import com.tacz.guns.item.DefaultTableItem
import com.tacz.guns.item.GunSmithTableItem
import com.tacz.guns.item.ModernKineticGunItem
import com.tacz.guns.item.TargetMinecartItem

class TaczMolecularAssemblerContainer(windowId: Int, inv: Inventory, private val pos: BlockPos) : AbstractContainerMenu(
    PackagedTaczBlocks.TACZ_MOLECULAR_ASSEMBLER_CONTAINER.get(), windowId) {

    private val level: Level = inv.player.level()
    private val blockEntity: TaczMolecularAssemblerBlockEntity? = level.getBlockEntity(pos) as? TaczMolecularAssemblerBlockEntity

    init {
        addPlayerInventory(inv)
        blockEntity?.getRecipeHandler()?.ifPresent { handler ->
            addSlot(RecipeSlot(handler, 0, 44, 25))
        }

        blockEntity?.getMaterialsHandler()?.ifPresent { handler ->
            for (col in 0..8) {
                addSlot(SlotItemHandler(handler, col, 8 + col * 18, 54))
            }
        }

        blockEntity?.getOutputsHandler()?.ifPresent { handler ->
            addSlot(OutputsSlot(handler, 0, 116, 25))
        }
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(ContainerLevelAccess.create(level, pos), player, PackagedTaczBlocks.TACZ_MOLECULAR_ASSEMBLER.get())
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    private fun addPlayerInventory(playerInventory: Inventory) {
        for (row in 0..2) {
            for (col in 0..8) {
                addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }
        for (col in 0..8) {
            addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
    }
}

class RecipeSlot(itemHandler: IItemHandler, slot: Int, x: Int, y: Int) : SlotItemHandler(itemHandler, slot, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean {
        return stack.item is RecipeHolderItem
    }
}

class OutputsSlot(itemHandler: IItemHandler, slot: Int, x: Int, y: Int) : SlotItemHandler(itemHandler, slot, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean {
        return stack.item is AmmoBoxItem || stack.item is AmmoItem || stack.item is AttachmentItem || stack.item is DefaultTableItem || stack.item is GunSmithTableItem || stack.item is ModernKineticGunItem || stack.item is TargetMinecartItem
    }
}
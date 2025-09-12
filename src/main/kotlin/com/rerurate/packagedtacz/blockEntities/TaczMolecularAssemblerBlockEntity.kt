package com.rerurate.packagedtacz.blockEntities

import com.rerurate.packagedtacz.helpers.StackHelper
import appeng.api.config.Actionable
import appeng.api.config.PowerMultiplier
import appeng.api.crafting.IPatternDetails
import appeng.api.implementations.IPowerChannelState
import appeng.api.implementations.blockentities.ICraftingMachine
import appeng.api.implementations.blockentities.PatternContainerGroup
import appeng.api.inventories.InternalInventory
import appeng.api.networking.IGridNode
import appeng.api.networking.IGridNodeListener
import appeng.api.networking.security.IActionSource
import appeng.api.networking.ticking.IGridTickable
import appeng.api.networking.ticking.TickRateModulation
import appeng.api.networking.ticking.TickingRequest
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.KeyCounter
import appeng.api.util.AECableType
import appeng.blockentity.grid.AENetworkInvBlockEntity
import appeng.core.definitions.AEBlocks
import appeng.util.inv.AppEngInternalInventory
import com.rerurate.packagedtacz.containers.TaczMolecularAssemblerContainer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import thelm.packagedauto.api.IPackageRecipeInfo
import thelm.packagedauto.api.IPackageRecipeList
import thelm.packagedauto.item.RecipeHolderItem
import java.util.logging.Logger
import kotlin.math.log

class TaczMolecularAssemblerBlockEntity(pos: BlockPos, state: BlockState) :
    MenuProvider,
    AENetworkInvBlockEntity(PackagedTaczBlockEntities.TACZ_MOLECULAR_ASSEMBLER.get(), pos, state),
    ICraftingMachine,
    IPowerChannelState,
    IGridTickable
{
    private val internalInv = AppEngInternalInventory(this, 256, 64)
    private var isPowered = false
    private var isBusy = false

    val logger = Logger.getLogger("TaczMolecularAssemblerBlockEntity")

    private val recipeHandler = object : ItemStackHandler(1) {
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
    private val recipeHandlerOptional = LazyOptional.of { recipeHandler as IItemHandler }

    private val materialsHandler = object : ItemStackHandler(9) {
        override fun getSlotLimit(slot: Int): Int {
            return 65535
        }
    }
    private val materialsHandlerOptional = LazyOptional.of { materialsHandler as IItemHandler }

    private val outputsHandler = ItemStackHandler(1)
    private val outputsHandlerOptional = LazyOptional.of { outputsHandler as IItemHandler }

    init {
        getMainNode().addService(IGridTickable::class.java, this)
        getMainNode().setIdlePowerUsage(0.0)
        logger.info("server lel")
    }

    fun getRecipeHandler(): LazyOptional<IItemHandler> {
        return recipeHandlerOptional
    }

    fun getMaterialsHandler(): LazyOptional<IItemHandler> {
        return materialsHandlerOptional
    }

    fun getOutputsHandler(): LazyOptional<IItemHandler> {
        return outputsHandlerOptional
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("recipes", recipeHandler.serializeNBT())
        tag.put("materials", materialsHandler.serializeNBT())
        tag.put("outputs", outputsHandler.serializeNBT())
        super.saveAdditional(tag)
    }

    override fun loadTag(tag: CompoundTag) {
        super.loadTag(tag)
        recipeHandler.deserializeNBT(tag.getCompound("recipes"))
        materialsHandler.deserializeNBT(tag.getCompound("materials"))
        outputsHandler.deserializeNBT(tag.getCompound("outputs"))
    }

    override fun getUpdateTag(): CompoundTag {
        return this.saveWithoutMetadata()
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return materialsHandlerOptional.cast()
        }
        return super.getCapability(cap, side)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        recipeHandlerOptional.invalidate()
        materialsHandlerOptional.invalidate()
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.packagedtacz.tacz_molecular_assembler")
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return TaczMolecularAssemblerContainer(containerId, inventory, blockPos)
    }

    override fun onMainNodeStateChanged(reason: IGridNodeListener.State?) {
        super.onMainNodeStateChanged(reason)

        if(reason != IGridNodeListener.State.GRID_BOOT) {
            var newState = false

            var grid = mainNode.grid
            if(grid != null) {
                newState = mainNode.isPowered && grid.energyService.extractAEPower(1.0, Actionable.SIMULATE,
                    PowerMultiplier.CONFIG) > 0.001
            }

            if(newState != isPowered) {
                isPowered = newState
                markForUpdate()
            }
        }
    }

    override fun getInternalInventory(): InternalInventory {
        return this.internalInv
    }

    override fun onChangeInventory(p0: InternalInventory?, p1: Int) {
        // No-op. Ticking logic handles this.
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
        patternDetails: IPatternDetails?,
        table: Array<out KeyCounter?>?,
        where: Direction?
    ): Boolean {
//        if (!this.acceptsPlans()) return false
//
//        var craftingResult = false
//
//        if (level != null && !level!!.isClientSide) {
//            craftingResult = serverTick()
//        }
//
//        return craftingResult
        return false
    }

    override fun acceptsPlans(): Boolean {
        return false
        //return !isBusy
    }

    override fun isPowered(): Boolean {
        return isPowered
    }

    override fun isActive(): Boolean {
        return this.isPowered
    }

    override fun getCableConnectionType(dir: Direction?): AECableType {
        return AECableType.SMART
    }

    override fun getTickingRequest(node: IGridNode): TickingRequest {
        return TickingRequest(1, 1, false, false)
    }

    override fun tickingRequest(node: IGridNode, ticksSinceLastCall: Int): TickRateModulation {
        if (level != null && !level!!.isClientSide) {
            serverTick()
        }

        return TickRateModulation.IDLE
    }

    fun serverTick(): Boolean {
        if (isPowered) {
            val recipeStack = recipeHandler.getStackInSlot(0)
            if (outputsHandler.getStackInSlot(0).isEmpty && recipeStack.item is RecipeHolderItem) {
                val recipeHolder = recipeStack.item as RecipeHolderItem
                val recipes: IPackageRecipeList? = recipeHolder.getRecipeList(recipeStack)
                val recipe: IPackageRecipeInfo? = recipes?.recipeList?.firstOrNull()
                if (recipe != null) {
                    val remainingItems = craft(recipe)
                    if (remainingItems.isEmpty()) {
                        //succeed crafting
                        return true
                    }
                }
            }
        }

        if (!outputsHandler.getStackInSlot(0).isEmpty) {
            importItemToME()
        }

        return false
    }

    fun ejectItem() {
        val outputStack = outputsHandler.getStackInSlot(0)
        if (!outputStack.isEmpty) {
            val direction = Direction.DOWN
            val targetPos = blockPos.relative(direction)
            Containers.dropItemStack(level, targetPos.x.toDouble(), targetPos.y.toDouble(), targetPos.z.toDouble(), outputStack)
            outputsHandler.setStackInSlot(0, ItemStack.EMPTY) // スロットをクリア
            setChanged()
        }
    }

    private fun importItemToME(){
        val outputStack = outputsHandler.getStackInSlot(0)
        if(outputStack.isEmpty){
            return
        }

        val grid = mainNode.grid ?: return

        val storageService = grid.storageService ?: return

        val remainingStack = storageService.inventory.insert(
            AEItemKey.of(outputStack),
            outputStack.count.toLong(),
            Actionable.MODULATE,
            IActionSource.empty()
        )

        logger.info(remainingStack.toString())

        outputsHandler.setStackInSlot(0, ItemStack.EMPTY)

        setChanged()
    }

    private fun craft(recipe: IPackageRecipeInfo): List<ItemStack> {
        val hasAllMaterials = StackHelper.hasMats(materialsHandler, recipe)

        if (hasAllMaterials) {
            for (inputStack in recipe.inputs) {
                removeStack(materialsHandler, inputStack)
            }

            val outputs = recipe.outputs
            if (outputs.isNotEmpty()) {
                outputsHandler.setStackInSlot(0, outputs[0].copy())
            }

            return emptyList()
        }

        return recipe.inputs
    }

    private fun removeStack(inventory: ItemStackHandler, stackToRemove: ItemStack) {
        var remainingToRemove = stackToRemove.count

        for (i in inventory.slots - 1 downTo 0) {
            if (remainingToRemove <= 0) break

            val currentStack = inventory.getStackInSlot(i)

            if (ItemStack.isSameItemSameTags(currentStack, stackToRemove)) {
                val countToRemove = minOf(currentStack.count, remainingToRemove)
                currentStack.shrink(countToRemove)
                remainingToRemove -= countToRemove

                if (currentStack.isEmpty) {
                    inventory.setStackInSlot(i, ItemStack.EMPTY)
                }
            }
        }
    }

    fun dropAllItems(level: Level, pos: BlockPos) {
        for (i in 0 until recipeHandler.slots) {
            val stack = recipeHandler.getStackInSlot(i)
            if (!stack.isEmpty) {
                Containers.dropItemStack(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }

        for (i in 0 until materialsHandler.slots) {
            val stack = materialsHandler.getStackInSlot(i)
            if (!stack.isEmpty) {
                Containers.dropItemStack(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }

        for (i in 0 until internalInv.size()) {
            val stack = internalInv.getStackInSlot(i)
            if (!stack.isEmpty) {
                Containers.dropItemStack(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }
    }
}

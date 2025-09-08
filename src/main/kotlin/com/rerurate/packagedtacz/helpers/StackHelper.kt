import net.minecraft.world.item.ItemStack
import thelm.packagedauto.api.IPackageRecipeInfo
import net.minecraftforge.items.ItemStackHandler

class StackHelper {
    companion object {
        fun hasMats(materialsHandler: ItemStackHandler, recipe: IPackageRecipeInfo): Boolean {
            val requiredInputs = recipe.inputs
            val remainingInputs = requiredInputs.map { it.copy() }.toMutableList()

            for (i in 0 until 9) {
                val currentStack = materialsHandler.getStackInSlot(i)
                if (currentStack.isEmpty) {
                    continue
                }

                val iterator = remainingInputs.iterator()
                while (iterator.hasNext()) {
                    val requiredStack = iterator.next()
                    if (ItemStack.isSameItemSameTags(currentStack, requiredStack)) {
                        val countToRemove = minOf(currentStack.count, requiredStack.count)
                        requiredStack.shrink(countToRemove)

                        if (requiredStack.isEmpty) {
                            iterator.remove()
                        }
                        break
                    }
                }
            }

            return remainingInputs.isEmpty()
        }
    }
}
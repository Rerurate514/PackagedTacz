package com.rerurate.packagedtacz.client.gui.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import com.rerurate.packagedtacz.Packagedtacz
import com.rerurate.packagedtacz.containers.TaczMolecularAssemblerContainer

class TaczMolecularAssemblerScreen(menu: TaczMolecularAssemblerContainer, playerInventory: Inventory, title: Component) : AbstractContainerScreen<TaczMolecularAssemblerContainer>(menu, playerInventory, title) {
    private val texture = ResourceLocation(Packagedtacz.ID, "textures/gui/tacz_molecular_assembler_gui.png")

    override fun init() {
        super.init()
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(guiGraphics)
        super.render(guiGraphics, mouseX, mouseY, partialTicks)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }
}
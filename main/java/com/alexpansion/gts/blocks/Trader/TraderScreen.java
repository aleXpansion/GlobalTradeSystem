package com.alexpansion.gts.blocks.Trader;

import com.alexpansion.gts.GTS;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TraderScreen extends ContainerScreen<TraderContainer>{

    private ResourceLocation GUI = new ResourceLocation(GTS.MOD_ID, "textures/gui/trader.png");

    public TraderScreen(TraderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn){
        super(screenContainer, inv, titleIn);
        this.ySize = 222;
    }

    @Override
    public void render(MatrixStack matrix,int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        //renderHoveredToolTip
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    @Override
    //drawGuiContainerForegroundLayer
    protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrix,relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
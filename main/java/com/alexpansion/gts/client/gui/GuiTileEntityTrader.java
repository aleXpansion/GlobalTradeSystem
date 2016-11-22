package com.alexpansion.gts.client.gui;

import com.alexpansion.gts.guicontainer.ContainerTileEntityTrader;
import com.alexpansion.gts.tileentity.TileEntityTrader;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiTileEntityTrader extends GuiContainer {

	private static final ResourceLocation TRADER_GUI_TEXTURE = new ResourceLocation("gts", "textures/gui/trader.png");
	private IInventory playerInv;
	private TileEntityTrader te;

	public GuiTileEntityTrader(IInventory playerInv, TileEntityTrader te) {
		super(new ContainerTileEntityTrader(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TRADER_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, 175, 222);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		try {
			this.fontRendererObj.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
			this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 130, 4210752);
			this.fontRendererObj.drawString("Purchases", 8, 80, 4210752);
			this.fontRendererObj.drawString("To Sell", 8, 44, 4210752);
			this.fontRendererObj.drawString(te.getItemInfo(), 32, 19, 4210752);
			this.fontRendererObj.drawString(te.getItemInfo2(), 32, 29, 4210752);
		} catch (NullPointerException e) {
			LogHelper.error("NPE in GuiTileEntityTrader.drawGuiContainerForegroundLayer");
			e.printStackTrace();
		}
	}

}

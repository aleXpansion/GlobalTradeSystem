package com.alexpansion.gts.client.gui;

import com.alexpansion.gts.guicontainer.ContainerItemCatalog;
import com.alexpansion.gts.inventory.InventoryCatalog;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiItemCatalog extends GuiContainer {

	private static final ResourceLocation CATALOG_GUI_TEXTURE = new ResourceLocation("gts","textures/gui/catalog.png");
	private IInventory playerInv;
	private InventoryCatalog catalog;
	
	public GuiItemCatalog(IInventory playerInvIn,InventoryCatalog catalogIn) {
		super(new ContainerItemCatalog(playerInvIn, catalogIn));
		this.playerInv = playerInvIn;
		this.catalog = catalogIn;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CATALOG_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, 175, 222);
        this.fontRendererObj.drawString(this.catalog.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8,130, 4210752);
    }

}

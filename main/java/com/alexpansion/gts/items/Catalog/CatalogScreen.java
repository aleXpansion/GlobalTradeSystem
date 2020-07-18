package com.alexpansion.gts.items.Catalog;

import java.util.ArrayList;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CatalogScreen extends ContainerScreen<CatalogContainer>{

    private ResourceLocation GUI = new ResourceLocation(GTS.MOD_ID, "textures/gui/catalog.png");

    private World world;

    public CatalogScreen(CatalogContainer container, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(container, playerInventory, titleIn);
        this.world = playerInventory.player.world;
        this.passEvents = true;
        this.ySize = 222;
        this.xSize = 176;
    }
    
    public void tick() {
        container.scrollTo(0.0F);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    @SuppressWarnings("resource")
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(Minecraft.getInstance().fontRenderer, "Value Stored: " + container.valueStack.getValue(), 32, 20, 0xffffff);
    }

    protected void init(){
        super.init();
        ValueManager vm = ValueManager.getVM(world);
        container.itemList.clear();
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ArrayList<Item> buyable = vm.getAllBuyableItems();
        for(Item i : buyable){
            stacks.add(new ItemStack(i));
        }
        container.itemList.addAll(stacks);
    }


    static class LockedSlot extends Slot{
        public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        public boolean canTakeStack(PlayerEntity playerIn){
            return true;
        }
    }
}
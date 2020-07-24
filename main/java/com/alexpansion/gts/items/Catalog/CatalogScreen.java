package com.alexpansion.gts.items.Catalog;

import java.util.ArrayList;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import com.mojang.blaze3d.matrix.MatrixStack;
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
    public void render(MatrixStack matrix,int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        //renderHoveredToolTip
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    @Override
    //drawGuiContainerBackgroundLayer
    protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrix,relX, relY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    @SuppressWarnings("resource")
    //drawGuiContainerForegroundLayer
    protected void func_230451_b_(MatrixStack matrix,int mouseX, int mouseY) {
        drawString(matrix,Minecraft.getInstance().fontRenderer, "Value Stored: " + container.valueStack.getValue(), 32, 20, 0xffffff);
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
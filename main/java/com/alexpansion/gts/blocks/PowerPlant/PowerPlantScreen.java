package com.alexpansion.gts.blocks.PowerPlant;

import com.alexpansion.gts.GTS;
import com.alexpansion.gts.value.ValueManager;
import com.alexpansion.gts.value.ValueManagerClient;
import com.alexpansion.gts.value.ValueWrapperEnergy;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PowerPlantScreen extends ContainerScreen<PowerPlantContainer> {

    private ResourceLocation GUI = new ResourceLocation(GTS.MOD_ID, "textures/gui/power_plant.png");

    public PowerPlantScreen(PowerPlantContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrix,int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        //renderHoveredToolTip
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("resource")
    //drawGuiContainerForegroundLayer
    protected void func_230451_b_(MatrixStack matrix,int mouseX, int mouseY) {
        drawString(matrix,Minecraft.getInstance().fontRenderer, "Energy: " + container.getEnergy() +"/", 10, 10, 0xffffff);
        ValueManagerClient vm = ValueManager.getClientVM();
        ValueWrapperEnergy wrapper = (ValueWrapperEnergy)vm.getBean().getWrapper("Energy","Forge");
        if(wrapper == null){
            return;
        }
        int value = (int)wrapper.getValue();
        String valueString = ""+value;
        if(value > 1000){
            valueString = value/1000 +"k";
        }
        int sold = wrapper.getSoldAmt();
        String soldString = ""+sold;
        if(sold > 10000000){
            soldString = sold/1000000 +"M";
        }else if(sold > 10000){
            soldString = sold/1000 +"k";
        }
        drawString(matrix,Minecraft.getInstance().fontRenderer, "Energy Value: " + valueString +"    Sold: "+soldString, 10, 50, 0xffffff);
        
    }

    @Override
    //drawGuiContainerBackgroundLayer
    protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrix,relX, relY, 0, 0, this.xSize, this.ySize);
    }

}
package com.alexpansion.gts.init;

import com.alexpansion.gts.block.*;
import com.alexpansion.gts.reference.Reference;
import com.alexpansion.gts.tileentity.TileEntitySeller;

import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

	public static final BlockSeller SELLER = new BlockSeller();
	
	public static void init(){
		
		GameRegistry.registerBlock(SELLER,"gts:seller");
	}
}

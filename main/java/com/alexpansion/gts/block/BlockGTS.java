package com.alexpansion.gts.block;

import com.alexpansion.gts.creativetab.CreativeTabGTS;
import com.alexpansion.gts.reference.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockGTS extends Block {

	public BlockGTS(Material material, String name) {
		super(material);
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabGTS.GTS_TAB);
	}

	public BlockGTS(String name) {
		this(Material.ROCK,name);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("tile.%s%s", Reference.MOD_ID.toLowerCase() + ":",
				getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

}

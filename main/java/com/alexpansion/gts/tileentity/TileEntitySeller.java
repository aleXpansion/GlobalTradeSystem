package com.alexpansion.gts.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.alexpansion.gts.block.BlockSeller;
import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.utility.GTSUtil;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntitySeller extends TileEntityLockableLoot implements ITickable, IInventory {
	private ItemStack[] chestContents = new ItemStack[27];
	/** The current angle of the lid (between 0 and 1) */
	public float lidAngle;
	/** The angle of the lid last tick */
	public float prevLidAngle;
	/** The number of players currently using this chest */
	public int numPlayersUsing;
	/** Server sync counter (once per 20 ticks) */
	private int ticksSinceSync;
	private String customName;

	public TileEntitySeller() {
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return 27;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Nullable
	public ItemStack getStackInSlot(int index) {
		return this.chestContents[index];
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and
	 * returns them in a new stack.
	 */
	@Nullable
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.chestContents, index, count);

		if (itemstack != null) {
			this.markDirty();
		}

		return itemstack;
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Nullable
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.chestContents, index);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		this.chestContents[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	/**
	 * Get the name of this object. For players this returns their username
	 */
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.chest";
	}

	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomName(String name) {
		this.customName = name;
	}

	public static void func_189677_a(DataFixer p_189677_0_) {
		p_189677_0_.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists("Chest", new String[] { "Items" }));
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.chestContents = new ItemStack[this.getSizeInventory()];

		if (compound.hasKey("CustomName", 8)) {
			this.customName = compound.getString("CustomName");
		}

		if (!this.checkLootAndRead(compound)) {
			NBTTagList nbttaglist = compound.getTagList("Items", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot") & 255;

				if (j >= 0 && j < this.chestContents.length) {
					this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
				}
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		if (!this.checkLootAndWrite(compound)) {
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < this.chestContents.length; ++i) {
				if (this.chestContents[i] != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("Slot", (byte) i);
					this.chestContents[i].writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}

			compound.setTag("Items", nbttaglist);
		}

		if (this.hasCustomName()) {
			compound.setString("CustomName", this.customName);
		}

		return compound;
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) != this ? false
				: player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
						(double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {

		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;

		if ((this.ticksSinceSync + i + j + k) % 10 == 0) {
			checkItems();
		}

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
			this.numPlayersUsing = 0;
			for (EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F),
							(double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F),
							(double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)))) {
				if (entityplayer.openContainer instanceof ContainerChest) {
					IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest
							&& ((InventoryLargeChest) iinventory).isPartOfLargeChest(this)) {
						++this.numPlayersUsing;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;
		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
			double d1 = (double) i + 0.5D;
			double d2 = (double) k + 0.5D;

			this.worldObj.playSound((EntityPlayer) null, d1, (double) j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN,
					SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
			float f2 = this.lidAngle;

			if (this.numPlayersUsing > 0) {
				this.lidAngle += 0.1F;
			} else {
				this.lidAngle -= 0.1F;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			if (this.lidAngle < 0.5F && f2 >= 0.5F) {
				double d3 = (double) i + 0.5D;
				double d0 = (double) k + 0.5D;

				this.worldObj.playSound((EntityPlayer) null, d3, (double) j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE,
						SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	public void openInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
			this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
		}

	}

	public void closeInventory(EntityPlayer player) {

		//GTSUtil.addSellableItemById(264, 8192);
		if (!player.isSpectator() && this.getBlockType() instanceof BlockSeller) {
			--this.numPlayersUsing;
			this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
			this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
		}

	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	/**
	 * invalidates a tile entity
	 */
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	public String getGuiID() {
		return "minecraft:chest";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerChest(playerInventory, this, playerIn);
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
	}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		this.fillWithLoot((EntityPlayer) null);

		for (int i = 0; i < this.chestContents.length; ++i) {
			this.chestContents[i] = null;
		}
	}

	public net.minecraftforge.items.IItemHandler getSingleChestHandler() {
		return super.getCapability(net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	private void checkItems() {
		HashMap<Item, Integer> totalSellables = new HashMap<Item, Integer>();
		for (ItemStack stack : chestContents) {
			if (stack != null) {
				Item item = stack.getItem();

				if (GTSUtil.canISell(item)) {
					int count = stack.stackSize;
					if (totalSellables.containsKey(item)) {
						int prevcount = totalSellables.get(item);
						totalSellables.put(item, prevcount + count);
					} else {
						totalSellables.put(item, count);
					}
				} else {
				}
			}
		}

		Iterator<Entry<Item, Integer>> iterator = totalSellables.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Item, Integer> pair = iterator.next();
			int count = pair.getValue();
			Item item = pair.getKey();
			sellItems(item, count);
		}

	}

	/*
	 * Attempts to sell count number of item. If the total value of those items
	 * is less than one credit, does nothing. If there isn't enough room for the
	 * resulting credits, does nothing.
	 */
	private void sellItems(Item item, int count) {
		double value = GTSUtil.getValue(item) * count;
		int totalValue = (int) Math.floor(value);
		if (totalValue < 1) {
			return;
		}
		count++;
		do {
			count--;
			value = GTSUtil.getValue(item) * count;
			totalValue = (int) Math.floor(value);
		} while (value - totalValue > GTSUtil.getValue(item));

		int countLeft = count;

		// try to insert the credits
		if (insertCredits(totalValue)) {

			LogHelper.info(count + " " + item.getUnlocalizedName() + " sold for " + totalValue
					+ " credits, value recieved: " + ((double) totalValue / count));
			LogHelper.info(item.getUnlocalizedName() + " is currenty valued at " + GTSUtil.getValue(item));
			GTSUtil.addValueSold(item, totalValue);

			// remove the sold items from the chest
			for (int i = 0; i < chestContents.length; i++) {
				if (chestContents[i] != null) {
					if (chestContents[i].getItem() == item) {
						if (chestContents[i].stackSize <= countLeft) {
							countLeft -= chestContents[i].stackSize;
							chestContents[i] = null;
							if (countLeft == 0) {
								return;
							}
						} else {
							chestContents[i].stackSize -= countLeft;
							return;
						}
					}
				}
			}
		} else {
			return;
		}
		LogHelper.error("Reached end of sellItems with " + countLeft + " left to sell.");
	}

	/*
	 * Attempts to insert creditCount credits into the inventory. If there's
	 * room, inserts the credits and returns true. If there isn't room, returns
	 * false without making any changes.
	 */
	private boolean insertCredits(int creditCount) {

		int limit = getInventoryStackLimit();
		int toInsert = creditCount;

		// make sure we have room for the credits
		for (ItemStack stack : chestContents) {
			if (stack == null) {
				toInsert -= getInventoryStackLimit();
			} else if (stack.getItem() == ModItems.CREDIT_CARD) {
				toInsert -= stack.getMaxDamage() - stack.getItemDamage();
			} else if (stack.getItem() == ModItems.CREDIT) {
				toInsert -= (limit - stack.stackSize);
			}
		}

		// return false if we don't have enough room
		if (toInsert > 0) {
			return false;
		}

		// insert the credits
		toInsert = creditCount;
		for (int i = 0; i < chestContents.length; i++) {
			if (chestContents[i] != null) {
				if (chestContents[i].getItem() == ModItems.CREDIT_CARD) {
					int cardCapacity = chestContents[i].getMaxDamage() - chestContents[i].getItemDamage();
					if (cardCapacity >= toInsert) {
						chestContents[i].setItemDamage(chestContents[i].getItemDamage() + toInsert);
						LogHelper.info("Card damage now set to: " + chestContents[i].getItemDamage());
						return true;
					}else if(cardCapacity < toInsert){
						toInsert-=cardCapacity;
						chestContents[i].setItemDamage(chestContents[i].getMaxDamage());
					}
				}
			}
		}
		for (int i = 0; i < chestContents.length; i++) {
			if (chestContents[i] != null) {
				if (chestContents[i].getItem() == ModItems.CREDIT) {
					if (limit - chestContents[i].stackSize >= toInsert) {
						chestContents[i].stackSize += toInsert;
						return true;
					} else {
						toInsert -= limit - chestContents[i].stackSize;
						chestContents[i].stackSize = limit;
					}
				}
			}
		}
		for (int i = 0; i < chestContents.length; i++) {
			if (chestContents[i] == null) {
				if (toInsert <= getInventoryStackLimit()) {
					chestContents[i] = new ItemStack(ModItems.CREDIT, toInsert);
					return true;
				} else {
					chestContents[i] = new ItemStack(ModItems.CREDIT, getInventoryStackLimit());
					toInsert -= getInventoryStackLimit();
				}
			}
		}

		// throw an error if we run out of room - should never run.
		LogHelper.error("insertCredits ran out of room after making sure it had enough room.");
		return false;

	}

}

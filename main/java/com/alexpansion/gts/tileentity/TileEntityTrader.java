package com.alexpansion.gts.tileentity;

import javax.annotation.Nullable;
import javax.security.auth.login.Configuration;

import com.alexpansion.gts.block.BlockTrader;
import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.item.IValueContainer;
import com.alexpansion.gts.item.ItemCreditCard;
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
import net.minecraft.world.World;

public class TileEntityTrader extends TileEntityLockableLoot implements ITickable, IInventory {
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
	private double change;

	public TileEntityTrader(World worldIn) {
		worldObj = worldIn;
		change = 0;
		if (!GTSUtil.areValuesLoaded()) {
			GTSUtil.loadValues(this.worldObj);
		}
	}

	public TileEntityTrader() {
		change = 0;
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

		// if ((this.ticksSinceSync + i + j + k) % 10 == 0) {
		checkItems();
		// buyItem();
		// sellItem();
		// }

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

		if (!player.isSpectator() && this.getBlockType() instanceof BlockTrader) {
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
		ItemStack lastStack = chestContents[chestContents.length - 1];
		Item toBuy = null;
		if (lastStack != null) {
			toBuy = lastStack.getItem();
		}

		buyItem(toBuy);
		sellItem(toBuy);

	}

	/**
	 * Attempts to insert creditCount credits into the inventory. If there's
	 * room, inserts the credits and returns true. If there isn't room, returns
	 * false without making any changes.
	 */
	private boolean insertCredits(int creditCount) {

		// LogHelper.info("Inserting "+creditCount+" credits");
		int limit = getInventoryStackLimit();
		int toInsert = creditCount;

		// make sure we have room for the credits
		for (ItemStack stack : chestContents) {
			if (stack == null) {
				toInsert -= 64;
			} else if (stack.getItem() instanceof ItemCreditCard) {
				toInsert -= ((ItemCreditCard) stack.getItem()).getValue(stack);
			} else if (stack.getItem() == ModItems.CREDIT) {
				toInsert -= stack.getMaxStackSize() - stack.stackSize;
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
				if (chestContents[i].getItem() instanceof IValueContainer) {
					try {
						ItemStack stack = chestContents[i];
						IValueContainer item = (IValueContainer) stack.getItem();
						int cardCapacity = item.getSpace(stack);
						if (cardCapacity >= toInsert) {
							item.addValue(chestContents[i], toInsert);
							return true;
						} else if (cardCapacity < toInsert) {
							toInsert -= cardCapacity;
							item.addValue(stack, cardCapacity);
						}
					} catch (ValueOverflowException e) {
						LogHelper.error("U02: ValueOverflowExeption after checking there was capacity.");
						LogHelper.error("Exception: " + e.getMessage());
						e.printStackTrace();
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
		LogHelper.error("U04: insertCredits ran out of room after making sure it had enough room.");
		return false;

	}

	private int getCreditCount() {
		int creditCount = 0;
		for (ItemStack stack : chestContents) {
			if (stack != null && stack.getItem() instanceof IValueContainer) {
				creditCount += ((IValueContainer) stack.getItem()).getValue(stack);
			}
		}
		// LogHelper.info("Trader contains "+creditCount+" credits");
		return creditCount;
	}

	private void removeCredits(int toRemove) {
		for (int i = 0; i < chestContents.length; i++) {
			ItemStack stack = chestContents[i];

			if (stack != null) {
				if (stack.getItem() == ModItems.CREDIT) {
					if (stack.stackSize <= toRemove) {
						toRemove -= stack.stackSize;
						chestContents[i] = null;
					} else if (stack.stackSize > toRemove) {
						chestContents[i].stackSize -= toRemove;
						return;
					}
				}
			}
		}
		for (int i = 0; i < chestContents.length; i++) {
			ItemStack stack = chestContents[i];

			if (stack != null) {
				try {
					if (stack.getItem() instanceof IValueContainer) {
						IValueContainer item = (IValueContainer) stack.getItem();
						int value = item.getValue(stack);
						if (value <= toRemove) {
							toRemove -= value;
							item.removeValue(stack, value);
						} else if (value > toRemove) {
							item.removeValue(stack, toRemove);
							;
							return;
						}
					}
				} catch (ValueOverflowException e) {
					LogHelper.error("U03: ValueOverflowExeption after checking there was sufficient value.");
					LogHelper.error("Exception: " + e.getMessage());
					e.printStackTrace();
				}

			}
		}
	}

	private void sellItem(Item ignore) {

		// for every stack in the chest
		for (int i = 0; i < chestContents.length - 1; i++) {

			// if it's neither null nor ignore
			if (chestContents[i] != null && chestContents[i].getItem() != ignore) {
				Item item = chestContents[i].getItem();
				if (GTSUtil.canISell(item)) {
					double itemValue = GTSUtil.getValue(item) * ConfigurationHandler.saleMultiplier;

					// if adding this value will bring change over 1, attempt to
					// insert credits as needed. Return if that fails
					if ((int) (change + itemValue) >= 1) {
						if (!insertCredits((int) (change + itemValue))) {
							return;
						} else {
							change = (change + itemValue) - (int) (change + itemValue);
						}
					} else {

						change += itemValue;
					}

					LogHelper.info("Selling " + item.getUnlocalizedName() + " for " + itemValue);
					// LogHelper.info("change is now at "+change);

					// actually remove the item
					if (chestContents[i].stackSize == 1) {
						chestContents[i] = null;
					} else {
						chestContents[i].stackSize = chestContents[i].stackSize - 1;
					}

					GTSUtil.addValueSold(item, itemValue, this.worldObj);
					return;
				}
			}
		}
	}

	private void buyItem(Item toBuy) {
		if (toBuy == null || !GTSUtil.canIBuy(toBuy)) {
			return;
		}
		double value = GTSUtil.getValue(toBuy);
		if (getCreditCount() + change < value) {
			return;
		}
		LogHelper.info("Buying " + toBuy.getUnlocalizedName() + " for " + value);
		for (int i = 0; i < chestContents.length - 1; i++) {
			if (chestContents[i] != null && chestContents[i].getItem() == toBuy) {
				if (chestContents[i].getItem() == null) {
					LogHelper.error("ItemStack with null item");
				} else {
					if (chestContents[i].stackSize < chestContents[i].getMaxStackSize()) {
						chestContents[i].stackSize++;
						GTSUtil.addValueSold(toBuy, 0 - value, this.worldObj);
						removeCredits(value);
						return;
					}
				}
			}
		}
		for (int i = 0; i < chestContents.length - 1; i++) {
			if (chestContents[i] == null) {
				chestContents[i] = new ItemStack(toBuy);
				removeCredits(value);
				return;
			}
		}
	}

	private void removeCredits(double value) {
		change -= value;
		if (change < 0) {
			int toRemove = (int) Math.ceil(0 - change);
			removeCredits(toRemove);
			change += toRemove;
		}
	}
}

package com.alexpansion.gts.tileentity;

import javax.annotation.Nullable;
import com.alexpansion.gts.block.BlockTrader;
import com.alexpansion.gts.exceptions.ValueOverflowException;
import com.alexpansion.gts.guicontainer.ContainerTileEntityTrader;
import com.alexpansion.gts.handler.ConfigurationHandler;
import com.alexpansion.gts.init.ModItems;
import com.alexpansion.gts.item.IValueContainer;
import com.alexpansion.gts.utility.GTSUtil;
import com.alexpansion.gts.utility.LogHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEntityTrader extends TileEntityLockableLoot implements ITickable, IInventory {
	private ItemStack[] chestContents = new ItemStack[29];
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
		return 29;
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
		return this.hasCustomName() ? this.customName : "container.trader";
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

		checkItems();

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
		if (index == 0) {
			return GTSUtil.canIBuy(stack.getItem());
		}
		if (index == 1) {
			return stack.getItem() instanceof IValueContainer;
		}
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
		return "gts:trader";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerTileEntityTrader(playerInventory, this);
	}

	public void clear() {

		for (int i = 0; i < this.chestContents.length; ++i) {
			this.chestContents[i] = null;
		}
	}

	private void checkItems() {
		ItemStack target = chestContents[0];
		Item toBuy = null;
		if (target != null) {
			toBuy = target.getItem();
		}

		buyItem(toBuy);
		sellItem(toBuy);
		consolidateValue();
		fillContainer();
	}

	private void consolidateValue() {
		for (int i = 2; i <= 10; i++) {
			if (chestContents[i] != null && chestContents[i].getItem() instanceof IValueContainer) {
				ItemStack stack = chestContents[i];
				IValueContainer item = (IValueContainer) stack.getItem();
				int count = 0;

				// make sure the item have a value of at least one
				if (item.getValue(stack) < 1) {
					return;
				}

				while (chestContents != null && item.getValue(stack) >= 1 && count < 50) {
					count++;
					// attempt to add a credit to the credit slot, return if it
					// fails
					if (!insertCredits(1)) {
						return;
					}

					// attempt to remove one value from the slot. We checked it
					// has one above, so we should never get an exception
					try {
						chestContents[i] = item.removeValue(chestContents[i], 1);
						if(chestContents[i] == null){
							return;
						}
					} catch (ValueOverflowException e) {
						LogHelper.error("ValueOverflowException in TileEntityTrader.consolidateValue");
						return;
					}

				}
				return;
			}
		}
	}

	private void fillContainer() {
		for (int i = 11; i <= 28; i++) {
			if (chestContents[i] != null && chestContents[i].getItem() instanceof IValueContainer) {
				ItemStack stack = chestContents[i];
				IValueContainer item = (IValueContainer) stack.getItem();
				int count = 0;

				while (getCreditCount() > 0 && item.getSpace(stack) > 0 && count < 50) {
					count++;
					try {
						removeCredits(1);
						item.addValue(stack, 1);
					} catch (ValueOverflowException e) {
						LogHelper.error("ValueOverflowException in TileEntityTrader.fillContainer");
					}
				}
				return;
			}
		}
	}

	/**
	 * Attempts to insert creditCount credits into the inventory. If there's
	 * room, inserts the credits and returns true. If there isn't room, returns
	 * false without making any changes.
	 */
	private boolean insertCredits(int toInsert) {

		// LogHelper.info("Inserting " + toInsert + " credits");
		ItemStack creditStack = chestContents[1];

		// if creditStack is empty, add a stack of credits. If we need to add
		// more that 64, return false
		if (creditStack == null) {
			if (toInsert > 64) {
				return false;
			} else {
				chestContents[1] = new ItemStack(ModItems.CREDIT);
				chestContents[1].stackSize = toInsert;
				return true;
			}

			// if creditStack has a valid valueContainer in it, add value.
			// Return false if there's not enough room.
		} else if (creditStack.getItem() instanceof IValueContainer) {
			IValueContainer item = (IValueContainer) creditStack.getItem();
			if (toInsert > item.getSpace(creditStack)) {
				return false;
			} else {
				try {
					item.addValue(creditStack, toInsert);
					return true;
				} catch (ValueOverflowException e) {
					LogHelper.error("ValueOverflowException in TileEntityTrader.insertCredits()");
					return false;
				}
			}

			// if it's neither empty nor has a valid container, return false.
		} else {
			return false;
		}

	}

	private int getCreditCount() {
		ItemStack creditStack = chestContents[1];
		if (creditStack != null && creditStack.getItem() instanceof IValueContainer) {
			return ((IValueContainer) creditStack.getItem()).getValue(creditStack);
		} else {
			return 0;
		}
	}

	/**
	 * Removes credits from the credit slot.
	 * 
	 * @param toRemove
	 * @throws ValueOverflowException
	 *             Thrown if there isn't enough value in the slot
	 */
	private void removeCredits(int toRemove) throws ValueOverflowException {
		ItemStack creditStack = chestContents[1];
		if(creditStack == null){
			throw new ValueOverflowException(null,toRemove);
		}
		if (creditStack.getItem() instanceof IValueContainer) {
			chestContents[1] = ((IValueContainer) creditStack.getItem()).removeValue(creditStack, toRemove);
		}
	}

	private void sellItem(Item ignore) {

		// for every stack in the selling row
		for (int i = 2; i <= 10; i++) {

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

		// if the toBuy item is either null or invalid for selling, return.
		if (toBuy == null || !GTSUtil.canIBuy(toBuy)) {
			return;
		}

		double value = GTSUtil.getValue(toBuy);

		// if we don't have enough credits to buy it, return
		if (getCreditCount() + change < value) {
			return;
		}

		//LogHelper.info("Buying " + toBuy.getUnlocalizedName() + " for " + value);

		// try to buy the item (because we already checked we can afford it, the
		// ValueOverFlowException should never be thrown)
		try {

			// for every slot in the purchases row
			for (int i = 11; i <= 28; i++) {

				// check that it's not null and contains the target item
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

			// looping through the purchase row again in case there wasn't a
			// valid stack with enough room
			for (int i = 11; i <= 28; i++) {
				if (chestContents[i] == null) {
					chestContents[i] = new ItemStack(toBuy);
					removeCredits(value);
					return;
				}
			}
		} catch (ValueOverflowException e) {
			LogHelper.error(
					"Error in TileEntityTrader.buyItem, ValueOverflowException was thrown after checking value.");
		}
	}

	private void removeCredits(double value) throws ValueOverflowException {
		change -= value;
		if (change < 0) {
			int toRemove = (int) Math.ceil(0 - change);
			removeCredits(toRemove);
			change += toRemove;
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}
}

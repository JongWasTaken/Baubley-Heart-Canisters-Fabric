package pw.smto.bhc.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import pw.smto.bhc.common.Registry;
import pw.smto.bhc.common.config.ConfigHandler;
import pw.smto.bhc.common.items.BaseHeartCanister;
import pw.smto.bhc.common.util.InventoryUtil;

public class BladeOfVitalityContainer extends ScreenHandler {
    public static final String HEART_AMOUNT = "heart_amount";
    public SimpleInventory itemStackHandler;

    public BladeOfVitalityContainer(int windowId, Inventory playerInventory, ItemStack stack) {
        super(Registry.ScreenHandlers.BLADE_OF_VITALITY_CONTAINER, windowId);
        this.itemStackHandler = InventoryUtil.createVirtualInventory(4, stack);

        //Heart Container Slots
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 0, 80, 5));//RED
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 1, 80, 25));//YELLOW
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 2, 80, 45));//GREEN
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 3, 80, 65));//BLUE

        //Add player inventory slots
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 56 + 86;
            if (row == getSlotFor(playerInventory, stack)) {
                addSlot(new BladeOfVitalityContainer.LockedSlot(playerInventory, row, x, y));
                continue;
            }

            addSlot(new Slot(playerInventory, row, x, y));
        }

        for (int row = 1; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + (56 + 10);
                addSlot(new Slot(playerInventory, col + row * 9, x, y));
            }
        }
    }

    public BladeOfVitalityContainer(int windowId, Inventory playerInventory, PacketByteBuf b) {
        super(Registry.ScreenHandlers.BLADE_OF_VITALITY_CONTAINER, windowId);
        var stack = b.readItemStack();
        this.itemStackHandler = InventoryUtil.createVirtualInventory(4, stack);

        //Heart Container Slots
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 0, 80, 5));//RED
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 1, 80, 25));//YELLOW
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 2, 80, 45));//GREEN
        this.addSlot(new BladeOfVitalityContainer.SlotPendant(this.itemStackHandler, 3, 80, 65));//BLUE

        //Add player inventory slots
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 56 + 86;
            if (row == getSlotFor(playerInventory, stack)) {
                addSlot(new BladeOfVitalityContainer.LockedSlot(playerInventory, row, x, y));
                continue;
            }

            addSlot(new Slot(playerInventory, row, x, y));
        }

        for (int row = 1; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + (56 + 10);
                addSlot(new Slot(playerInventory, col + row * 9, x, y));
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity playerIn) {
        ItemStack sword = playerIn.getMainHandStack();
        InventoryUtil.serializeInventory(this.itemStackHandler, sword);
        NbtCompound nbt = sword.getNbt();
        int[] hearts = new int[this.itemStackHandler.size()];
        for (int i = 0; i < hearts.length; i++) {
            ItemStack stack = this.itemStackHandler.getStack(i);
            if (!stack.isEmpty()) hearts[i] = stack.getCount() * 2;
        }
        nbt.putIntArray(HEART_AMOUNT, hearts);
        sword.setNbt(nbt);
        super.onClosed(playerIn);
    }
/*
    @Override
    public boolean handleSlotClick(PlayerEntity player, ClickType clickType, Slot slot, ItemStack stack, ItemStack cursorStack) {
        if (clickType == ClickType.LEFT) {
            int offsetButton = slot.id + 4; //Offset the pressed button by 4 to get the correct hotbar slot
            if (getSlot(offsetButton).getStack().getItem() instanceof ItemBladeOfVitality)
                return false;
        }
        return super.handleSlotClick(player, clickType, slot, stack, cursorStack);
    }


 */
    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index < this.itemStackHandler.size()) {
                if (!this.insertItem(slotStack, this.itemStackHandler.size(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.insertItem(slotStack, 0, this.itemStackHandler.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return stack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return InventoryUtil.hasItem(player, Registry.Items.BLADE_OF_VITALITY);
    }


    private static class SlotPendant extends Slot {
        public SlotPendant(Inventory inv, int index, int xPosition, int yPosition) {
            super(inv, index, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return super.canInsert(stack) && stack.getItem() instanceof BaseHeartCanister && ((BaseHeartCanister) stack.getItem()).type.ordinal() == this.getIndex();
        }

        @Override
        public int getMaxItemCount() {
            return ConfigHandler.general.heartStackSize.get();
        }
    }

    private static class LockedSlot extends Slot {
        public LockedSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerIn) {
            return false;
        }
    }

    public int getSlotFor(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.size(); ++i) {
            if (!inventory.getStack(i).isEmpty() && stackEqualExact(stack, inventory.getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areEqual(stack1, stack2);
    }
}

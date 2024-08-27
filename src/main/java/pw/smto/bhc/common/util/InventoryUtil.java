package pw.smto.bhc.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import pw.smto.bhc.common.BaubleyHeartCanisters;

public class InventoryUtil {

    private static final String ITEMLIST = BaubleyHeartCanisters.MOD_ID + "_itemlist";

    public static SimpleInventory createVirtualInventory(int slots, ItemStack stack) {
        SimpleInventory handler = new SimpleInventory(slots);
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        //BaubleyHeartCanisters.LOGGER.warn(nbt.toString());

        handler.clear();
        var nbtList = nbt.getList(ITEMLIST, NbtElement.COMPOUND_TYPE);
        for(int i = 0; i < nbtList.size(); ++i) {
            handler.setStack(i,ItemStack.fromNbt(nbtList.getCompound(i)));
        }

        return handler;
    }

    public static void serializeInventory(SimpleInventory itemHandler, ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();

        NbtList nbtList = new NbtList();
        for(int i = 0; i < itemHandler.size(); ++i) {
            nbtList.add(itemHandler.getStack(i).writeNbt(new NbtCompound()));
        }

        nbt.put(ITEMLIST, nbtList);
        //BaubleyHeartCanisters.LOGGER.warn(nbt.toString());
        stack.setNbt(nbt);
    }

    public static boolean hasItem(PlayerEntity player, Item item) {
        for (int i = 0; player.getInventory().size() > i; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if(stack.isOf(item)) return true;
        }
        return false;
    }
}

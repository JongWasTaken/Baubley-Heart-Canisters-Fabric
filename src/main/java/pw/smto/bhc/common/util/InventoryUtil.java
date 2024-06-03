package pw.smto.bhc.common.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import pw.smto.bhc.common.BaubleyHeartCanisters;
import pw.smto.bhc.common.Registry;

import java.util.Objects;

public class InventoryUtil {

    private static final String ITEMLIST = BaubleyHeartCanisters.MOD_ID + "_itemlist";

    private static final NbtCompound PLACEHOLDER = getPlaceholder();

    private static NbtCompound getPlaceholder() {
        var n = new NbtCompound();
        n.putInt("count", 0);
        n.putString("id", "minecraft:air");
        return n;
    }

    public static SimpleInventory createVirtualInventory(int slots, ItemStack stack) {
        SimpleInventory handler = new SimpleInventory(slots);
        NbtCompound nbt = stack.getComponents().contains(DataComponentTypes.CUSTOM_DATA) ? stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt() : new NbtCompound();
        handler.clear();
        var nbtList = nbt.getList(ITEMLIST, NbtElement.COMPOUND_TYPE);
        for(int i = 0; i < nbtList.size(); ++i) {
            if (nbtList.getCompound(i).equals(PLACEHOLDER)) {
                handler.setStack(i, ItemStack.EMPTY);
                continue;
            }
            try {
                handler.setStack(i, ItemStack.fromNbt(DynamicRegistryManager.EMPTY, nbtList.getCompound(i)).get());
            } catch (Exception ignored) {}
        }

        return handler;
    }

    public static void serializeInventory(SimpleInventory itemHandler, ItemStack stack) {
        NbtCompound nbt = stack.getComponents().contains(DataComponentTypes.CUSTOM_DATA) ? Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt() : new NbtCompound();

        NbtList nbtList = new NbtList();
        for(int i = 0; i < itemHandler.size(); ++i) {
            if (itemHandler.getStack(i).isEmpty()) {
                nbtList.add(PLACEHOLDER);
            }
            else {
                nbtList.add(itemHandler.getStack(i).encode(DynamicRegistryManager.EMPTY));
            }
        }

        nbt.put(ITEMLIST, nbtList);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static boolean hasAmulet(PlayerEntity player) {
        for (int i = 0; player.getInventory().size() > i; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if(stack.getItem() != Registry.Items.HEART_AMULET) continue;
        }
        return true;
    }
}

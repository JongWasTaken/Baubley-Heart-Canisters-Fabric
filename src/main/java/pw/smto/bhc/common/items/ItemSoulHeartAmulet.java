package pw.smto.bhc.common.items;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import pw.smto.bhc.common.Registry;
import pw.smto.bhc.common.container.SoulHeartAmuletContainer;

import java.util.List;
import java.util.UUID;

public class ItemSoulHeartAmulet extends TrinketItem implements ExtendedScreenHandlerFactory {

    public ItemSoulHeartAmulet() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND)
            return TypedActionResult.fail(player.getStackInHand(hand));

        if (!level.isClient() && !player.isSneaking()) {
            player.openHandledScreen(this);
            return TypedActionResult.pass(player.getStackInHand(hand));
        }
        return TypedActionResult.fail(player.getStackInHand(hand));
        //return super.use(level, player, hand);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);

        int extraHearts = 0;
        for (int i : getHeartCount(stack)) {
            extraHearts += i;
        }

        modifiers.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(uuid, "bhc:extra_health", extraHearts, EntityAttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    public int[] getHeartCount(ItemStack stack) {
        int[] array = new int[4];
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt.contains(SoulHeartAmuletContainer.HEART_AMOUNT)) {
                var t = nbt.getIntArray(SoulHeartAmuletContainer.HEART_AMOUNT);
                array[0] = t[0];
                array[1] = t[1];
                array[2] = t[2];
                array[3] = t[3];
            }
        }

        return array;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bhc.soul_heart_amulet");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        Hand hand = getHandForAmulet(player);
        return new SoulHeartAmuletContainer(syncId, playerInventory, hand != null ? player.getStackInHand(hand) : ItemStack.EMPTY);
    }
    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
        tooltip.add(Text.translatable("tooltip.bhc.heartamulet").setStyle(Style.EMPTY.withFormatting(Formatting.GOLD)));
    }

    public static Hand getHandForAmulet(PlayerEntity player) {
        if (player.getMainHandStack().getItem() == Registry.Items.SOUL_HEART_AMULET)
            return Hand.MAIN_HAND;
        else if (player.getOffHandStack().getItem() == Registry.Items.SOUL_HEART_AMULET)
            return Hand.OFF_HAND;
        return null;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeItemStack(player.getMainHandStack());
    }
}

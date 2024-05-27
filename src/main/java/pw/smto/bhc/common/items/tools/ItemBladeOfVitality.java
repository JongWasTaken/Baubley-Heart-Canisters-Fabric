package pw.smto.bhc.common.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import pw.smto.bhc.common.BaubleyHeartCanisters;
import pw.smto.bhc.common.Registry;
import pw.smto.bhc.common.container.BladeOfVitalityContainer;
import pw.smto.bhc.common.container.HeartAmuletContainer;
import pw.smto.bhc.common.util.HeartType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class ItemBladeOfVitality extends SwordItem implements ExtendedScreenHandlerFactory {

    public static final UUID DAMAGE_MODIFIER_ID = UUID.fromString("432ba3b0-c3bd-4f1c-b14c-76a0b32a386c");

    //ToDo: make an actual Tier for Blade of Vitality Easier to Customize
    public ItemBladeOfVitality() {
        super(ToolMaterials.NETHERITE, 3, -2.4F , new Item.Settings().maxDamage(-1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND)
            return TypedActionResult.fail(player.getStackInHand(hand));

        if (!level.isClient() && player.isSneaking()) {
            player.openHandledScreen(this);
        }

        return super.use(level, player, hand);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> RESULT = ImmutableMultimap.builder();
        RESULT.putAll(super.getAttributeModifiers(stack, slot));
        if(slot == EquipmentSlot.MAINHAND) {
            int[] heartCount = getHeartCount(stack);
            int heartTotal = IntStream.of(heartCount).sum();
            if (heartTotal > 0) {
                RESULT.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(DAMAGE_MODIFIER_ID, "Weapon modifier", heartTotal, EntityAttributeModifier.Operation.ADDITION));
            }
        }
        return RESULT.build();
    }

    public int[] getHeartCount(ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt.contains(BladeOfVitalityContainer.HEART_AMOUNT))
                return nbt.getIntArray(BladeOfVitalityContainer.HEART_AMOUNT);
        }

        return new int[HeartType.values().length];
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bhc.blade_of_vitality");
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
        tooltip.add(Text.translatable("tooltip.bhc.vitality_blade").setStyle(Style.EMPTY.withFormatting(Formatting.GOLD)));
    }


    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BladeOfVitalityContainer(syncId, playerInventory, player.getMainHandStack());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeItemStack(serverPlayerEntity.getMainHandStack());
    }
}

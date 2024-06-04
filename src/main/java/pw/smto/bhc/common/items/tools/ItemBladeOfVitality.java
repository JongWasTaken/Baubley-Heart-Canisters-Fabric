package pw.smto.bhc.common.items.tools;

import io.wispforest.owo.serialization.Endec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import pw.smto.bhc.common.container.BladeOfVitalityContainer;
import pw.smto.bhc.common.util.HeartType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class ItemBladeOfVitality extends SwordItem implements ExtendedScreenHandlerFactory<ItemBladeOfVitality.BladeOfVitalityData> {
    public ItemBladeOfVitality() {
        super(ToolMaterials.NETHERITE, new Item.Settings()
                .maxDamage(-1)
                .attributeModifiers(createAttributeModifiers()));
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

    private static AttributeModifiersComponent createAttributeModifiers() {
        var builder = AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",7, EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", 1.6 - 4, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                );

        return builder.build().withShowInTooltip(false);
    }

    private static AttributeModifiersComponent createCustomAttributeModifiers(int heartTotal) {
        var builder = AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",8, EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", 1.6, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                );
        if (heartTotal != 0) {
            builder.add(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(
                            ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", heartTotal, EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
            );
        }

        return builder.build().withShowInTooltip(false);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (!world.isClient) {
                int heartTotal = IntStream.of(getHeartCount(stack)).sum();
                if (heartTotal > 0) {
                    stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, createCustomAttributeModifiers(heartTotal));
                }
                else {
                    stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, createCustomAttributeModifiers(0));
                }
            }
        }
    }

    public int[] getHeartCount(ItemStack stack) {
        if (stack.getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
            NbtCompound nbt = Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
            if (nbt.contains(BladeOfVitalityContainer.HEART_AMOUNT))
                return nbt.getIntArray(BladeOfVitalityContainer.HEART_AMOUNT);
        }

        return new int[HeartType.values().length];
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bhc.blade_of_vitality");
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        // I hate this too, but for some reason 1.20.6 just does not want to display the tooltip correctly, so a hack it is
        tooltip.add(Text.translatable("tooltip.bhc.vitality_blade").setStyle(Style.EMPTY.withFormatting(Formatting.GOLD)));
        tooltip.add(Text.literal(" "));
        tooltip.add(Text.literal("When in Main Hand:").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
        tooltip.add(Text.literal(" 8 Attack Damage").setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GREEN)));
        tooltip.add(Text.literal(" 1.6 Attack Speed").setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GREEN)));
        tooltip.add(Text.literal(" +"+ IntStream.of(getHeartCount(stack)).sum() +" Attack Damage").setStyle(Style.EMPTY.withFormatting(Formatting.BLUE)));

    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BladeOfVitalityContainer(syncId, playerInventory, player.getMainHandStack());
    }

    public record BladeOfVitalityData(ItemStack stack) {
        public static final PacketCodec<RegistryByteBuf, BladeOfVitalityData> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.codec(ItemStack.CODEC),
                BladeOfVitalityData::stack,
                BladeOfVitalityData::new
        );
    }

    @Override
    public BladeOfVitalityData getScreenOpeningData(ServerPlayerEntity player) {
        return new BladeOfVitalityData(player.getMainHandStack());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}

package pw.smto.bhc.common.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemHeartPatch extends Item {

    protected final int amount;
    protected final int cooldown;
    protected final int durability;

    public ItemHeartPatch(int healAmount, int cooldown, int durability) {
        super(new Settings().maxDamage(durability));
        this.amount = healAmount;
        this.cooldown = cooldown;
        this.durability = durability;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isClient()) {
            ItemStack stack = playerIn.getStackInHand(handIn);
            worldIn.playSound((PlayerEntity) null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.NEUTRAL, 0.5F, 0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));
            playerIn.heal(amount);
            if (!playerIn.isCreative()) {
                playerIn.getItemCooldownManager().set(stack.getItem(), cooldown);
                stack.damage(1, playerIn, (p) -> {
                    p.sendToolBreakStatus(handIn);
                });
            }
            return new TypedActionResult<>(ActionResult.SUCCESS, playerIn.getStackInHand(handIn));
        }
       return new TypedActionResult<>(ActionResult.FAIL, playerIn.getStackInHand(handIn));
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
        tooltip.add(Text.translatable("tooltip.bhc.patch_amount").append(Text.literal("" + amount)).setStyle(Style.EMPTY.withFormatting(Formatting.RED)));
        tooltip.add(Text.translatable("tooltip.bhc.patch_durability").append(Text.literal("" + (this.durability - stack.getDamage())).setStyle(Style.EMPTY.withFormatting(Formatting.BLUE))));
    }

}

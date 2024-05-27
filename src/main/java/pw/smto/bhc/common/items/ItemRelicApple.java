package pw.smto.bhc.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRelicApple extends BaseItem {
    public ItemRelicApple(){
        super(20, 0.8F);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isClient() && entityLiving instanceof PlayerEntity player) {
            player.eatFood(worldIn,stack);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 60, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 60, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20 * 60, 1));
            player.heal(20);
        }
        return stack;
    }
}

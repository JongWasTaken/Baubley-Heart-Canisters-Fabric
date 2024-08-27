package pw.smto.bhc.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import pw.smto.bhc.common.util.HeartType;

public class ItemHeart extends Item {

    protected final HeartType type;

    public ItemHeart(HeartType type) {
        super(new Item.Settings().food(
                new FoodComponent.Builder().saturationModifier(0).alwaysEdible().hunger(0).build())
        );
        this.type = type;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }


    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 30;
    }


    @Override
    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isClient() && entityLiving instanceof PlayerEntity player) {
            player.heal(this.type.healAmount);
            if (!player.isCreative()) stack.decrement(1);
        }
        return stack;
    }


}

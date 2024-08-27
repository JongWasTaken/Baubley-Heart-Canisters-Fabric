package pw.smto.bhc.common.items;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
public class BaseItem extends Item {

    public BaseItem() {
        super(new Item.Settings());
    }

    public BaseItem(int maxCount) {
        super(new Item.Settings().maxCount(maxCount));
    }

    public BaseItem(int hunger, float saturation) {
        super(new Item.Settings().food(new FoodComponent.Builder().saturationModifier(saturation).alwaysEdible().hunger(hunger).build()));
    }
}

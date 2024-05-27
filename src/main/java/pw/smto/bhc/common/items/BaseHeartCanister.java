package pw.smto.bhc.common.items;

import net.minecraft.item.ItemStack;
import pw.smto.bhc.common.config.ConfigHandler;
import pw.smto.bhc.common.util.HeartType;

public class BaseHeartCanister extends BaseItem {

    public HeartType type;
    public BaseHeartCanister(HeartType type){
        super();
        this.type = type;
    }

    @Override
    public int getMaxCount() {
        return ConfigHandler.general.heartStackSize.get();
    }
}

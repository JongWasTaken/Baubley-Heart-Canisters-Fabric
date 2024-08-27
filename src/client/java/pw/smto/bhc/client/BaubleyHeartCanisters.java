package pw.smto.bhc.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import pw.smto.bhc.client.screens.*;
import pw.smto.bhc.common.Registry;

import java.util.Locale;

public class BaubleyHeartCanisters implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(Registry.ScreenHandlers.HEART_AMUlET_CONTAINER, HeartAmuletScreen::new);
        HandledScreens.register(Registry.ScreenHandlers.SOUL_HEART_AMUlET_CONTAINER, SoulHeartAmuletScreen::new);
        HandledScreens.register(Registry.ScreenHandlers.BLADE_OF_VITALITY_CONTAINER, BladeOfVitalityScreen::new);

        ModelPredicateProviderRegistry.register(Registry.Items.BLADE_OF_VITALITY, new Identifier(pw.smto.bhc.common.BaubleyHeartCanisters.MOD_ID,"easter_egg"), (ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) -> {
            String hoverName = itemStack.hasCustomName() ? itemStack.getName().getString().toLowerCase(Locale.ROOT) : "";
            float result = 0.0F;
            if (hoverName.contains("beautiful eyes")) {
                result = 0.1F;
            } else if (hoverName.contains("traverse")) {
                result = 0.2F;
            } else if (hoverName.contains("jamiscus")) {
                result = 0.3F;
            }
            return result;
        });
    }
}
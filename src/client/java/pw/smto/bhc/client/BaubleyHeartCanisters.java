package pw.smto.bhc.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import pw.smto.bhc.client.screens.*;
import pw.smto.bhc.common.Registry;

public class BaubleyHeartCanisters implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //ClientNetworking.init();
        HandledScreens.register(Registry.ScreenHandlers.HEART_AMUlET_CONTAINER, HeartAmuletScreen::new);
        HandledScreens.register(Registry.ScreenHandlers.SOUL_HEART_AMUlET_CONTAINER, SoulHeartAmuletScreen::new);
        HandledScreens.register(Registry.ScreenHandlers.BLADE_OF_VITALITY_CONTAINER, BladeOfVitalityScreen::new);
        //EasterEgg.secretCode();
    }
}
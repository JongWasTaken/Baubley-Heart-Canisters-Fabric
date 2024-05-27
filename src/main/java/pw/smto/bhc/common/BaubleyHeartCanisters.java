package pw.smto.bhc.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.LoggerFactory;
import pw.smto.bhc.common.config.BHCConfig;
import pw.smto.bhc.common.config.ConfigHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class BaubleyHeartCanisters implements ModInitializer {
    public static final String MOD_ID = "bhc";
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static BHCConfig config;

    private void jsonSetup() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File folder = Path.of(FabricLoader.getInstance().getConfigDir().toString(),"bhc").toFile();
        folder.mkdirs();
        File file = folder.toPath().resolve("drops.json").toFile();
        try {
            if (file.exists()) {
                config = gson.fromJson(new FileReader(file), BHCConfig.class);
                return;
            }
            config = new BHCConfig();
            config.addEntrytoMap("red", "hostile", 0.05);
            config.addEntrytoMap("yellow", "boss", 1.0);
            config.addEntrytoMap("green", "dragon", 1.0);
            config.addEntrytoMap("blue", "minecraft:warden", 1.0);
            String json = gson.toJson(config, BHCConfig.class);
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialize() {
        Registry.registerAll();
        EventHandler.init();
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, ConfigHandler.configSpec);
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.SERVER, ConfigHandler.serverConfigSpec);
        jsonSetup();
    }
}

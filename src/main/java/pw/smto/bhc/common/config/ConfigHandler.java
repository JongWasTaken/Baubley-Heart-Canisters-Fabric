package pw.smto.bhc.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;

public class ConfigHandler {

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> heartStackSize;
        public final ForgeConfigSpec.ConfigValue<Double> boneDropRate;
        public final ForgeConfigSpec.ConfigValue<Double> echoShardDropRate;
        public final ForgeConfigSpec.ConfigValue<Double> soulHeartReturnChance;

        General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            heartStackSize = builder
                    .comment("The maximum stacksize for heart canisters, also the maximum amount of full hearts you can get per heart container type")
                    .define("heartStackSize", 10);
            boneDropRate = builder
                    .comment("How often do wither bones Drop? (1.0 = 100 % and 0.0 means 0%)")
                    .define("boneDropRate", 0.15);
            echoShardDropRate = builder
                    .comment("How often do echo shards drop from Warden? (1.0 = 100 % and 0.0 means 0%)")
                    .define("echoShardDropRate", 0.5);
            soulHeartReturnChance = builder
                    .comment("Chance for the Soul Heart to return a Blue Heart Canister after being broken")
                    .define("soulHeartReturnChance", 1.0);
            builder.pop();
        }
    }

    public static class BHCServer {
        public final ForgeConfigSpec.ConfigValue<Boolean> allowStartingHeathTweaks;
        public final ForgeConfigSpec.ConfigValue<Integer> startingHealth;

        BHCServer(ForgeConfigSpec.Builder builder) {
            builder.push("Server");
            allowStartingHeathTweaks = builder
                    .comment("Allow Starting Health Tweaks")
                    .define("allowStartingHealthTweaks", false);
            startingHealth = builder
                    .comment("Starting Health of Player (Default:20)")
                    .define("startingHealth", 20);
            builder.pop();
        }
    }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final General general = new General(BUILDER);
    public static final BHCServer server = new BHCServer(SERVER_BUILDER);
    public static final IConfigSpec<ForgeConfigSpec> configSpec = BUILDER.build();
    public static final IConfigSpec<ForgeConfigSpec> serverConfigSpec = SERVER_BUILDER.build();
}

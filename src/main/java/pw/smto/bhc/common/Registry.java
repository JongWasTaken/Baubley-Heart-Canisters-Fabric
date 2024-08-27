package pw.smto.bhc.common;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pw.smto.bhc.common.container.*;
import pw.smto.bhc.common.items.*;
import pw.smto.bhc.common.items.tools.ItemBladeOfVitality;
import pw.smto.bhc.common.recipes.HeartAmuletRecipe;
import pw.smto.bhc.common.util.HeartType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static pw.smto.bhc.common.BaubleyHeartCanisters.MOD_ID;

public class Registry {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface HiddenFromItemGroup {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ItemGroupIcon {}
    private static final List<Item> ITEM_GROUP = new ArrayList<Item>();
    private static ItemStack ITEM_GROUP_ICON = net.minecraft.item.Items.GLASS.getDefaultStack();

    public static class Items implements ItemRegistryContainer {
        public static final Item RED_HEART_CANISTER = new BaseHeartCanister(HeartType.RED);
        public static final Item YELLOW_HEART_CANISTER = new BaseHeartCanister(HeartType.YELLOW);
        public static final Item GREEN_HEART_CANISTER = new BaseHeartCanister(HeartType.GREEN);
        public static final Item BLUE_HEART_CANISTER = new BaseHeartCanister(HeartType.BLUE);
        public static final Item SOUL_HEART_CANISTER = new BaseHeartCanister(HeartType.SOUL);

        public static final Item RED_HEART_MELTED = new BaseItem();
        public static final Item YELLOW_HEART_MELTED = new BaseItem();
        public static final Item GREEN_HEART_MELTED = new BaseItem();
        public static final Item BLUE_HEART_MELTED = new BaseItem();

        public static final Item RED_HEART_PATCH = new ItemHeartPatch(2, 5*20, 20);
        public static final Item YELLOW_HEART_PATCH = new ItemHeartPatch(6, 10*20, 25);
        public static final Item GREEN_HEART_PATCH = new ItemHeartPatch(10, 20*20, 30);
        public static final Item BLUE_HEART_PATCH = new ItemHeartPatch(20, 30*20, 50);

        public static final Item RED_HEART = new ItemHeart(HeartType.RED);
        public static final Item YELLOW_HEART = new ItemHeart(HeartType.YELLOW);
        public static final Item GREEN_HEART = new ItemHeart(HeartType.GREEN);
        public static final Item BLUE_HEART = new ItemHeart(HeartType.BLUE);
        public static final Item CANISTER = new BaseItem();
        public static final Item BLADE_OF_VITALITY = new ItemBladeOfVitality();

        public static final Item WITHER_BONE = new BaseItem();
        public static final Item RELIC_APPLE = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(20).saturationModifier(0.8F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 60, 1), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 60, 1), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20 * 60, 1), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 2), 1.0F).build()));
        @ItemGroupIcon
        public static final Item HEART_AMULET = new ItemHeartAmulet();
        public static final Item SOUL_HEART_AMULET = new ItemSoulHeartAmulet();
        public static final Item SOUL_HEART_CRYSTAL = new BaseItem();



        @Override
        public void postProcessField(String namespace, Item value, String identifier, Field field) {
            if (!field.isAnnotationPresent(HiddenFromItemGroup.class)){
                ITEM_GROUP.add(value);
            }
            if (field.isAnnotationPresent(ItemGroupIcon.class)){
                ITEM_GROUP_ICON = value.getDefaultStack();
            }
        }
    }

    public static class ScreenHandlers implements AutoRegistryContainer<ScreenHandlerType<?>> {
        public static final ScreenHandlerType<BladeOfVitalityContainer> BLADE_OF_VITALITY_CONTAINER = new ExtendedScreenHandlerType<>(BladeOfVitalityContainer::new);
        public static final ScreenHandlerType<HeartAmuletContainer> HEART_AMUlET_CONTAINER = new ExtendedScreenHandlerType<>(HeartAmuletContainer::new);
        public static final ScreenHandlerType<SoulHeartAmuletContainer> SOUL_HEART_AMUlET_CONTAINER = new ExtendedScreenHandlerType<>(SoulHeartAmuletContainer::new);

        @Override
        public net.minecraft.registry.Registry<ScreenHandlerType<?>> getRegistry() {
            return Registries.SCREEN_HANDLER;
        }
        @Override
        @SuppressWarnings("unchecked")
        public Class<ScreenHandlerType<?>> getTargetFieldType() {
            return (Class<ScreenHandlerType<?>>) (Object) ScreenHandlerType.class;
        }
    }

    public static class RecipeSerializers implements AutoRegistryContainer<RecipeSerializer<?>> {
        public static final RecipeSerializer<HeartAmuletRecipe> AMULET_SHAPELESS = new HeartAmuletRecipe.BHCSerializer();
        @Override
        public net.minecraft.registry.Registry<RecipeSerializer<?>> getRegistry() {
            return Registries.RECIPE_SERIALIZER;
        }
        @Override
        @SuppressWarnings("unchecked")
        public Class<RecipeSerializer<?>> getTargetFieldType() {
            return (Class<RecipeSerializer<?>>) (Object) RecipeSerializer.class;
        }
    }

    public static void registerAll() {
        FieldRegistrationHandler.register(Items.class, MOD_ID, false);
        FieldRegistrationHandler.register(ScreenHandlers.class, MOD_ID, false);
        FieldRegistrationHandler.register(RecipeSerializers.class, MOD_ID, false);

        var tabBuilder = FabricItemGroup.builder();
        tabBuilder.displayName(Text.translatable("itemGroup.bhcTab"));
        tabBuilder.icon(() -> ITEM_GROUP_ICON);
        tabBuilder.entries((displayContext, entries) -> {
            ITEM_GROUP.forEach(entries::add);
        });
        net.minecraft.registry.Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "items"), tabBuilder.build());
    }
}
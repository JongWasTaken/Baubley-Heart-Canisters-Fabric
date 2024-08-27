package pw.smto.bhc.common.util;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import pw.smto.bhc.common.BaubleyHeartCanisters;
import pw.smto.bhc.common.Registry;
import pw.smto.bhc.common.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropHandler {
    public static void onEntityDrop(Entity entity, PlayerEntity player, World world) {
        if (world.isClient || entity instanceof PlayerEntity) return;

        double dropRateModifier = 0.0;

        if (player.getMainHandStack().hasNbt()) {
            var enchantments = player.getMainHandStack().getNbt().getList("Enchantments", NbtElement.COMPOUND_TYPE);
            if (enchantments != null) {
                for (NbtElement enchantment : enchantments) {
                    NbtCompound compound = (NbtCompound) enchantment;
                    if (compound.getString("id").equals("minecraft:looting")) {
                        for(int i = 0; i < compound.getShort("lvl"); i++) {
                            dropRateModifier += 0.1;
                        }
                    }
                }
            }
        }

        if (!FabricLoader.getInstance().isModLoaded("tconstruct") && entity instanceof WitherSkeletonEntity) {
            if (world.random.nextDouble() < ConfigHandler.general.boneDropRate.get() + dropRateModifier) {
                world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Registry.Items.WITHER_BONE, 1)));
            }
        }

        if(entity instanceof WardenEntity) {
            if(world.random.nextDouble() < ConfigHandler.general.echoShardDropRate.get() + dropRateModifier) {
                world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.ECHO_SHARD, 1)));
            }
        }

        for (ItemStack stack : getEntityDrops(entity,dropRateModifier)) {
            world.spawnEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), stack));
        }
    }

    public static List<ItemStack> getEntityDrops(Entity entity, double dropRateModifier) {
        List<ItemStack> list = new ArrayList<>();
        handleEntry("red",entity,list,dropRateModifier);
        handleEntry("yellow",entity,list,dropRateModifier);
        handleEntry("green",entity,list,dropRateModifier);
        handleEntry("blue",entity,list,dropRateModifier);
        return list;
    }

    public static void handleEntry(String category, Entity entity, List<ItemStack> items, double dropRateModifier) {
        for (Map.Entry<String, Double> entry : BaubleyHeartCanisters.config.getHeartTypeEntries(category).entrySet()) {
            ItemStack stack = ItemStack.EMPTY;
            switch (category) {
                case "red":
                    stack = new ItemStack(Registry.Items.RED_HEART);
                    break;
                case "yellow":
                    stack = new ItemStack(Registry.Items.YELLOW_HEART);
                    break;
                case "green":
                    stack = new ItemStack(Registry.Items.GREEN_HEART);
                    break;
                case "blue":
                    stack = new ItemStack(Registry.Items.BLUE_HEART);
                    break;
            }

            if (entry.getKey().equals(Registries.ENTITY_TYPE.getId(entity.getType()).toString())) {
                addWithPercent(items, stack, entry.getValue() + dropRateModifier);
            } else {
                switch (entry.getKey()) {
                    case "passive":
                        if((!(entity instanceof HostileEntity) && !(entity instanceof PlayerEntity))) {
                            addWithPercent(items, stack, entry.getValue() + dropRateModifier);
                        }
                        break;
                    case "hostile":
                        if (entity instanceof HostileEntity && !(isBoss(entity) && !(entity instanceof WardenEntity))) {
                            addWithPercent(items, stack, entry.getValue() + dropRateModifier);
                        }
                        break;
                    case "boss":
                        if (isBoss(entity) && !(entity instanceof EnderDragonEntity)) {
                            addWithPercent(items, stack, entry.getValue() + dropRateModifier);
                        }
                        break;
                    case "dragon":
                        if (entity instanceof EnderDragonEntity) {
                            addWithPercent(items, stack, entry.getValue() + dropRateModifier);
                        }
                        break;
                }
            }
        }
    }

    public static void addWithPercent(List<ItemStack> list, ItemStack stack, double percentage) {
        Random random = new Random();
        int percent = (int) (percentage * 100);
        if (random.nextInt(100) < percent) {
            list.add(stack);
        }
    }

    private static boolean isBoss(Entity entity) {
        if(entity != null) {
            return entity.getType().isIn(ConventionalEntityTypeTags.BOSSES) || entity instanceof WardenEntity;
        }
        return false;
    }
}

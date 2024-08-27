package pw.smto.bhc.common;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import pw.smto.bhc.common.config.ConfigHandler;
import pw.smto.bhc.common.items.ItemSoulHeartAmulet;
import pw.smto.bhc.common.util.DropHandler;
import pw.smto.bhc.common.util.InventoryUtil;

import java.util.concurrent.atomic.AtomicReference;

public class EventHandler {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register(EventHandler::setStartingHealth);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(EventHandler::onItemDrop);
        ServerLivingEntityEvents.ALLOW_DEATH.register(EventHandler::onPlayerDeathEvent);
    }

    public static void setStartingHealth(Entity entity, World world) {
        if(ConfigHandler.server.allowStartingHeathTweaks.get() && entity instanceof PlayerEntity player && !(player instanceof FakePlayer)) {
            if(ConfigHandler.server.startingHealth.get() > 0) {
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(ConfigHandler.server.startingHealth.get());
            }
        }
    }

    public static void onItemDrop(ServerWorld world, Entity entity, LivingEntity killedEntity) {
        if (entity instanceof PlayerEntity player) {
            DropHandler.onEntityDrop(killedEntity,player,world);
        }
    }

    public static boolean onPlayerDeathEvent(LivingEntity entity, DamageSource source, float damageAmount) {
        if (!entity.getWorld().isClient()) {
            if (entity instanceof PlayerEntity player) {
                var temp = TrinketsApi.getTrinketComponent(player).orElse(null);
                if (temp == null) return true;

                var handler = temp.getAllEquipped();
                AtomicReference<ItemStack> amulet = new AtomicReference<>(ItemStack.EMPTY);
                handler.forEach(slot -> {
                    if (slot.getRight().getItem() instanceof ItemSoulHeartAmulet) {
                        amulet.set(slot.getRight());
                    }
                });
                if (amulet.get().equals(ItemStack.EMPTY)) return true;
                SimpleInventory soulInventory = InventoryUtil.createVirtualInventory(5, amulet.get());
                if (!soulInventory.getStack(4).isEmpty()) {
                    soulInventory.getStack(4).setCount(soulInventory.getStack(4).getCount() - 1);
                    InventoryUtil.serializeInventory(soulInventory, amulet.get());
                    player.sendMessage(Text.translatable("soulheartused.bhc.message").setStyle(Style.EMPTY.withFormatting(Formatting.DARK_PURPLE)), true);
                    player.getWorld().playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TOTEM_USE, player.getSoundCategory(), 1.0F, 1.0F, false);
                    //15% chance
                    if (player.getWorld().random.nextDouble() <= ConfigHandler.general.soulHeartReturnChance.get()) {
                        player.giveItemStack(new ItemStack(Registry.Items.BLUE_HEART_CANISTER));
                    }
                    player.setHealth(player.getMaxHealth());
                    player.clearStatusEffects();
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                    return false;
                }
            }
        }
        return true;
    }
}

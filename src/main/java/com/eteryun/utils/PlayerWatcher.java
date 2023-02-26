package com.eteryun.utils;

import com.eteryun.modules.backtoo.extension.IInventory;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerWatcher {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Minecraft minecraft = Minecraft.getInstance();
    public ArrayList<WatcherValue> watcherValues = new ArrayList<>();
    public WatcherValue<Player> watcherPlayer;
    public Watcher watcher;
    private Player player;

    public PlayerWatcher(Watcher watcher) {
        this.watcher = watcher;
        watcherPlayer = new WatcherValue<Player>(() -> minecraft.player, newValue -> {
            this.player = newValue;
            if (this.player != null)
                registerWatchers();
            else watcherValues.clear();
        }, Entity::is);
    }

    public void registerWatchers() {
        watcherValues.clear();
        watcherValues.add(new WatcherValue<String>(() -> player.getName().getString(), newValue -> watcher.onChange("name", newValue)));
        watcherValues.add(new WatcherValue<Float>(player::getHealth, newValue -> watcher.onChange("health", newValue)));
        watcherValues.add(new WatcherValue<Float>(player::getMaxHealth, newValue -> watcher.onChange("maxHealth", newValue)));
        watcherValues.add(new WatcherValue<Float>(() -> player.experienceProgress, newValue -> watcher.onChange("experience", newValue)));
        watcherValues.add(new WatcherValue<Integer>(() -> player.experienceLevel, newValue -> watcher.onChange("level", newValue)));
        watcherValues.add(new WatcherValue<Integer>(() -> player.getInventory().selected, newValue -> watcher.onChange("currentSlot", newValue)));
        watcherValues.add(new WatcherValue<Float>(player::getAbsorptionAmount, newValue -> watcher.onChange("absorption", newValue)));
        watcherValues.add(new WatcherValue<JsonArray>(() -> effectInstancesToJson(player.getActiveEffects()),
                newValue -> watcher.onChange("effects", newValue)));
        watcherValues.add(new WatcherValue<JsonObject>(() -> hotbarJson(player.getInventory()), newValue -> watcher.onChange("hotbar", newValue)));

        watcherValues.forEach(WatcherValue::notifyWatcher);
    }

    private JsonArray effectInstancesToJson(Collection<MobEffectInstance> collection) {
        JsonArray jsonArray = new JsonArray();
        player.getActiveEffects().forEach(mobEffectInstance -> jsonArray.add(MobEffect.getId(mobEffectInstance.getEffect())));
        return jsonArray;
    }

    private JsonObject hotbarJson(Inventory inventory) {
        JsonObject jsonObject = new JsonObject();

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inventory.items.get(i);
            if (itemStack.isEmpty()) continue;

            jsonObject.add(String.valueOf(i), itemStackToJson(itemStack));
        }

        ItemStack itemStack = inventory.offhand.get(0);
        if (!itemStack.isEmpty())
            jsonObject.add(String.valueOf(8), itemStackToJson(itemStack));
        itemStack = ((IInventory) inventory).getBackToolSlot(0);
        if (!itemStack.isEmpty())
            jsonObject.add(String.valueOf(9), itemStackToJson(itemStack));

        return jsonObject;
    }

    private JsonObject itemStackToJson(ItemStack itemStack) {
        JsonObject jsonObject = new JsonObject();
        ResourceLocation resourceLocation = Registry.ITEM.getKey(itemStack.getItem());
        jsonObject.addProperty("key", resourceLocation.toString());
        jsonObject.addProperty("damage", itemStack.getDamageValue());
        jsonObject.addProperty("maxDamage", itemStack.getMaxDamage());
        jsonObject.addProperty("quantity", itemStack.getCount());
        jsonObject.addProperty("isEnchanted", itemStack.isEnchanted());

        return jsonObject;
    }

    public void tick() {
        watcherPlayer.tick();
        watcherValues.forEach(WatcherValue::tick);
    }

    public interface Watcher<T> {
        void onChange(String name, T newValue);
    }
}

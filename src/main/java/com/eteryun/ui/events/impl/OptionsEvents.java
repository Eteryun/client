package com.eteryun.ui.events.impl;

import com.eteryun.ui.events.UIEventTarget;
import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.ramon.ultralight.UltralightEngine;
import net.minecraft.client.*;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptionsEvents {
    private static Minecraft minecraft = Minecraft.getInstance();
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @UIEventTarget(name = "getTranslate")
    public String getTranslate(JsonObject object) {
        JsonObject translate = new JsonObject();
        String string = String.format("lang/%s.json", minecraft.options.languageCode);
        Set<String> namespaces = minecraft.getResourceManager().getNamespaces();
        namespaces.forEach(namespace -> {
            ResourceLocation resourceLocation = new ResourceLocation(namespace, string);
            try {
                Resource resource = minecraft.getResourceManager().getResource(resourceLocation);
                Language.loadFromJson(resource.getInputStream(), translate::addProperty);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return GSON.toJson(translate);
    }

    @UIEventTarget(name = "getOption")
    public Object getOption(String name) {
        try {
            Field field = Option.class.getField(name);
            if (field.getType() == ProgressOption.class) {
                ProgressOption progressOption = (ProgressOption) field.get(null);
                return progressOption.get(minecraft.options);
            } else if (field.getType() == CycleOption.class) {
                CycleOption cycleOption = (CycleOption) field.get(null);
                Function<Options, ?> getter = getDeclaredField(cycleOption, "getter");
                if (getter != null) {
                    Object value = getter.apply(minecraft.options);
                    if (value instanceof Enum<?>)
                        return ((Enum<?>) value).ordinal();
                    else return value;
                }
                return 0;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @UIEventTarget(name = "setOption")
    public void setOption(JsonObject object) {
        String name = GsonHelper.getAsString(object, "name");
        try {
            Field field = Option.class.getField(name);
            if (field.getType() == ProgressOption.class) {
                ProgressOption progressOption = (ProgressOption) field.get(null);
                Double value = object.get("value").getAsDouble();
                progressOption.set(minecraft.options, value);
                minecraft.options.save();
            } else if (field.getType() == CycleOption.class) {
                CycleOption cycleOption = (CycleOption) field.get(null);
                CycleOption.OptionSetter setter = getDeclaredField(cycleOption, "setter");
                if (setter != null) {
                    Function<Options, ?> getter = getDeclaredField(cycleOption, "getter");
                    Object lastValue = getter.apply(minecraft.options);
                    JsonElement jsonElement = object.get("value");
                    if (lastValue instanceof Enum<?>) {
                        setter.accept(minecraft.options, cycleOption, lastValue.getClass().getEnumConstants()[jsonElement.getAsInt()]);
                    } else {
                        setter.accept(minecraft.options, cycleOption, jsonElement.getAsBoolean());
                    }
                    minecraft.options.save();
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @UIEventTarget(name = "getModelPart")
    public Boolean getModelPart(String id) {
        PlayerModelPart modelPart = Arrays.stream(PlayerModelPart.values()).filter(playerModelPart -> playerModelPart.getId().equalsIgnoreCase(id)).findFirst().get();
        return minecraft.options.isModelPartEnabled(modelPart);
    }

    @UIEventTarget(name = "setModelPart")
    public void setModelPart(JsonObject object) {
        String id = GsonHelper.getAsString(object, "name");
        PlayerModelPart modelPart = Arrays.stream(PlayerModelPart.values()).filter(playerModelPart -> playerModelPart.getId().equalsIgnoreCase(id)).findFirst().get();
        Boolean value = object.get("value").getAsBoolean();
        minecraft.options.toggleModelPart(modelPart, value);
        minecraft.options.save();
    }

    @UIEventTarget(name = "getSound")
    public float getSound(String id) {
        return minecraft.options.getSoundSourceVolume(SoundSource.valueOf(id));
    }

    @UIEventTarget(name = "setSound")
    public void setSound(JsonObject object) {
        String id = GsonHelper.getAsString(object, "name");
        float value = object.get("value").getAsFloat();
        minecraft.options.setSoundCategoryVolume(SoundSource.valueOf(id), value);
        minecraft.options.save();
    }

    @UIEventTarget(name = "getLanguage")
    public String getLanguage(String id) {
        return minecraft.options.languageCode;
    }

    @UIEventTarget(name = "setLanguage")
    public void setLanguage(JsonObject object) {
        String value = object.get("value").getAsString();

        minecraft.getLanguageManager().setSelected(minecraft.getLanguageManager().getLanguage(value));
        minecraft.options.languageCode = value;
        minecraft.reloadResourcePacks();
        minecraft.options.save();

        UltralightEngine.getInstance().sendMessageAll("getTranslate", null);
    }

    @UIEventTarget(name = "getKeyMappings")
    public String getKeyMappings(JsonObject object) {
        JsonObject json = new JsonObject();
        Stream<KeyMapping> keyMappings = Arrays.stream(minecraft.options.keyMappings);
        Map<String, List<KeyMapping>> keyMappingsGrouped = keyMappings
                .collect(Collectors.groupingBy(KeyMapping::getCategory));

        for (var entry : keyMappingsGrouped.entrySet()) {
            JsonArray array = new JsonArray();
            entry.getValue().stream().map(keyMapping -> {
                JsonObject keyJson = new JsonObject();
                keyJson.addProperty("defaultKey", keyMapping.getDefaultKey().getName());
                keyJson.addProperty("currentKey", keyMapping.saveString());
                keyJson.addProperty("title", keyMapping.getName());
                return keyJson;
            }).forEach(array::add);

            json.add(entry.getKey(), array);
        }

        return GSON.toJson(json);
    }

    @UIEventTarget(name = "setKeyMapping")
    public void setKeyMapping(JsonObject object) {
        String id = object.get("id").getAsString();
        JsonObject key = object.get("key").getAsJsonObject();

        KeyMapping keyMapping = null;
        for (KeyMapping keyMapping1 : minecraft.options.keyMappings) {
            if (keyMapping1.getName().equalsIgnoreCase(id)) {
                keyMapping = keyMapping1;
                break;
            }
        }
        if (keyMapping == null)
            return;
        InputConstants.Key inputting = key.get("isMouse").getAsBoolean() ? InputConstants.Type.MOUSE.getOrCreate(key.get("code").getAsInt())
                : InputConstants.getKey(key.get("code").getAsInt(), key.get("scanCode").getAsInt());

        minecraft.options.setKey(keyMapping, inputting);
        KeyMapping.resetMapping();
    }

    @UIEventTarget(name = "resetKeyMapping")
    public void resetKeyMapping(JsonObject object) {
        String id = object.get("id").getAsString();

        KeyMapping keyMapping = null;
        for (KeyMapping keyMapping1 : minecraft.options.keyMappings) {
            if (keyMapping1.getName().equalsIgnoreCase(id)) {
                keyMapping = keyMapping1;
                break;
            }
        }
        if (keyMapping == null)
            return;

        minecraft.options.setKey(keyMapping, keyMapping.getDefaultKey());
        KeyMapping.resetMapping();
    }

    public <T> T getDeclaredField(Object obj, String name) {
        try {
            Field setterField = obj.getClass().getDeclaredField(name);
            if (setterField.trySetAccessible()) {
                setterField.setAccessible(true);
                return (T) setterField.get(obj);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}

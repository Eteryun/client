package com.eteryun.modules.ui.screens;

import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.modules.cef.screen.CefScreen;
import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsScreen extends CefScreen {
    private final Screen lastScreen;
    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public SettingsScreen(Screen lastScreen, boolean hasBackground) {
        super(getUrl(hasBackground));
        this.lastScreen = lastScreen;
    }

    public SettingsScreen(boolean hasBackground) {
        this(null, hasBackground);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @QueryTarget(name = "getOption")
    public Object getOption(String name) {
        try {
            Field field = Option.class.getField(name);
            if (field.getType() == ProgressOption.class) {
                ProgressOption progressOption = (ProgressOption) field.get(null);
                return progressOption.get(minecraft.options);
            } else if (field.getType() == CycleOption.class) {
                CycleOption cycleOption = (CycleOption) field.get(null);
                String getterFieldName = Boolean.getBoolean("fabric.development") ? "getter" : "ag";
                Function<Options, ?> getter = getDeclaredField(cycleOption, getterFieldName);
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

    @QueryTarget(name = "setOption")
    public void setOption(JsonObject object) {
        String name = GsonHelper.getAsString(object, "name");
        try {
            Field field = Option.class.getField(name);
            if (field.getType() == ProgressOption.class) {
                Double value = GsonHelper.getAsDouble(object, "value");
                ProgressOption progressOption = (ProgressOption) field.get(null);
                progressOption.set(minecraft.options, value);
                minecraft.options.save();
            } else if (field.getType() == CycleOption.class) {
                CycleOption cycleOption = (CycleOption) field.get(null);
                String setterFieldName = Boolean.getBoolean("fabric.development") ? "setter" : "af";
                CycleOption.OptionSetter setter = getDeclaredField(cycleOption, setterFieldName);
                if (setter != null) {
                    JsonElement value = object.get("value");
                    String getterFieldName = Boolean.getBoolean("fabric.development") ? "getter" : "ag";
                    Function<Options, ?> getter = getDeclaredField(cycleOption, getterFieldName);
                    Object lastValue = getter.apply(minecraft.options);
                    if (lastValue instanceof Enum<?>) {
                        setter.accept(minecraft.options, cycleOption, lastValue.getClass().getEnumConstants()[value.getAsInt()]);
                    } else {
                        setter.accept(minecraft.options, cycleOption, value.getAsBoolean());
                    }
                    minecraft.options.save();
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @QueryTarget(name = "getModelPart")
    public Boolean getModelPart(String id) {
        PlayerModelPart modelPart = Arrays.stream(PlayerModelPart.values()).filter(playerModelPart -> playerModelPart.getId().equalsIgnoreCase(id)).findFirst().get();
        return minecraft.options.isModelPartEnabled(modelPart);
    }

    @QueryTarget(name = "setModelPart")
    public void setModelPart(JsonObject object) {
        String id = GsonHelper.getAsString(object, "name");
        Boolean value = GsonHelper.getAsBoolean(object, "value");
        PlayerModelPart modelPart = Arrays.stream(PlayerModelPart.values()).filter(playerModelPart -> playerModelPart.getId().equalsIgnoreCase(id)).findFirst().get();
        minecraft.options.toggleModelPart(modelPart, value);
        minecraft.options.save();
    }

    @QueryTarget(name = "getSound")
    public float getSound(String id) {
        return minecraft.options.getSoundSourceVolume(SoundSource.valueOf(id));
    }

    @QueryTarget(name = "setSound")
    public void setSound(JsonObject object) {
        String id = GsonHelper.getAsString(object, "name");
        float value = GsonHelper.getAsFloat(object, "value");
        minecraft.options.setSoundCategoryVolume(SoundSource.valueOf(id), value);
        minecraft.options.save();
    }

    @QueryTarget(name = "getLanguage")
    public String getLanguage(String name) {
        return minecraft.options.languageCode;
    }

    @QueryTarget(name = "setLanguage")
    public void setLanguage(JsonObject object) {
        String value = GsonHelper.getAsString(object, "value");
        minecraft.getLanguageManager().setSelected(minecraft.getLanguageManager().getLanguage(value));
        minecraft.options.languageCode = value;
        minecraft.reloadResourcePacks();
        minecraft.options.save();

        cefBrowser.sendMessage("getTranslate", null);
    }

    @QueryTarget(name = "getKeyMappings")
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

    @QueryTarget(name = "setKeyMapping")
    public void setKeyMapping(JsonObject object) {
        String id = GsonHelper.getAsString(object, "id");
        JsonObject key = GsonHelper.getAsJsonObject(object, "key");
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

    @QueryTarget(name = "resetKeyMapping")
    public void resetKeyMapping(String id) {
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

    @QueryTarget(name = "return")
    public void ret(JsonObject object) {
        minecraft.setScreen(lastScreen);
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

    private static String getUrl(boolean hasBackground) {
        String url = "https://ui.eteryun.com.br/screens/#/options/video";
        if (hasBackground)
            url += "?background";
        return url;
    }
}

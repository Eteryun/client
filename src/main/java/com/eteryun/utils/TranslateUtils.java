package com.eteryun.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.Set;

public class TranslateUtils {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Minecraft minecraft = Minecraft.getInstance();

    public static String getJson() {
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
}

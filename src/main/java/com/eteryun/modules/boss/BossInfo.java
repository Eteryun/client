package com.eteryun.modules.boss;

import com.eteryun.modules.boss.network.client.ClientboundPacketBoss;
import com.eteryun.modules.ui.UiModule;
import com.google.gson.JsonObject;

import static org.cef.browser.CefBrowserCustom.GSON;

public class BossInfo {
    public double getHealth() {
        return 0;
    }

    public double getMaxHealth() {
        return 0;
    }

    public String getTitle() {
        return "";
    }

    public String getColor() {
        return "";
    }

    public String getImage() {
        return "";
    }

    public static void update(ClientboundPacketBoss clientboundPacketBoss, UiModule uiModule) {
        clientboundPacketBoss.dispatch(new ClientboundPacketBoss.Handler() {
            @Override
            public void add(String title, String color, String image, double health, double maxHealth) {
                JsonObject object = new JsonObject();
                object.addProperty("title", title);
                object.addProperty("color", color);
                object.addProperty("image", image);
                object.addProperty("health", health);
                object.addProperty("maxHealth", maxHealth);
                object.addProperty("show", true);
                uiModule.getGui().sendMessage("setBoss", GSON.toJson(object));
            }

            @Override
            public void remove() {
                JsonObject object = new JsonObject();
                object.addProperty("show", false);
                uiModule.getGui().sendMessage("setBoss", GSON.toJson(object));
            }

            @Override
            public void updateTitle(String title) {
                JsonObject object = new JsonObject();
                object.addProperty("title", title);
                uiModule.getGui().sendMessage("setBoss", GSON.toJson(object));
            }

            @Override
            public void updateStyle(String color, String image) {
                JsonObject object = new JsonObject();
                object.addProperty("color", color);
                object.addProperty("image", image);
                uiModule.getGui().sendMessage("setBoss", GSON.toJson(object));
            }

            @Override
            public void updateHealth(double health, double maxHealth) {
                JsonObject object = new JsonObject();
                object.addProperty("health", health);
                object.addProperty("maxHealth", maxHealth);
                uiModule.getGui().sendMessage("setBoss", GSON.toJson(object));
            }
        });
    }
}

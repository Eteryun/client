package com.eteryun.modules.ui;

import com.eteryun.event.EventManager;
import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent.ElementType;
import com.eteryun.event.impl.gameoverlay.TickGameOverlayEvent;
import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.utils.PlayerWatcher;
import com.eteryun.utils.TranslateUtils;
import com.eteryun.utils.WatcherValue;
import com.google.gson.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowserCustom;

import java.util.ArrayList;

public class InGameGui {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected CefBrowserCustom cefBrowser;
    private Minecraft minecraft = Minecraft.getInstance();
    public ArrayList<WatcherValue> watcherValues = new ArrayList<>();
    private ArrayList<Runnable> queueUpdates = new ArrayList<>();
    public PlayerWatcher watcherPlayer;

    private boolean loaded = false;

    private final ArrayList<ElementType> HIDDEN_ELEMENTS = new ArrayList<>();

    public InGameGui() {
        cefBrowser = new CefBrowserCustom(CefManager.cefClient, "http://localhost:3000", true, null);
        cefBrowser.setCloseAllowed();
        cefBrowser.createImmediately();

        CefManager.getInstance().registerQueryHandler(this);
        EventManager.register(this);

        init();
        registerElements();
        this.watcherPlayer = new PlayerWatcher(this::valueUpdate);
    }

    public <T> void valueUpdate(String name, T value) {
        if (value instanceof String)
            updatePartialUser(name, (String) value);
        else if (value instanceof Number)
            updatePartialUser(name, (Number) value);
        else if (value instanceof JsonElement)
            updatePartialUser(name, (JsonElement) value);
    }

    public void registerElements() {
        HIDDEN_ELEMENTS.add(ElementType.HEALTH);
        HIDDEN_ELEMENTS.add(ElementType.EXPERIENCE);
        HIDDEN_ELEMENTS.add(ElementType.EFFECTS);
        HIDDEN_ELEMENTS.add(ElementType.HOTBAR);
    }

    public void init() {
        cefBrowser.setFocus(true);
        cefBrowser.wasResized_(minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight());
        cefBrowser.sendMessage("getTranslate", null);
    }

    public void render() {
        RenderSystem.enableBlend();
        GlStateManager._enableDepthTest();
        cefBrowser.draw(0, 0, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        GlStateManager._disableDepthTest();
        RenderSystem.disableBlend();
    }

    public void updatePartialUser(String name, JsonElement value) {
        if (!loaded) {
            queueUpdates.add(() -> this.updatePartialUser(name, value));
            return;
        }
        JsonObject object = new JsonObject();
        object.add(name, value);
        cefBrowser.sendMessage("setUser", GSON.toJson(object));
    }

    public void updatePartialUser(String name, Number value) {
        if (!loaded) {
            queueUpdates.add(() -> this.updatePartialUser(name, value));
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty(name, value);
        cefBrowser.sendMessage("setUser", GSON.toJson(object));
    }

    public void updatePartialUser(String name, String value) {
        if (!loaded) {
            queueUpdates.add(() -> this.updatePartialUser(name, value));
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty(name, value);
        cefBrowser.sendMessage("setUser", GSON.toJson(object));
    }

    @QueryTarget(name = "hud:getTranslate")
    public String getTranslate(JsonObject object) {
        return TranslateUtils.getJson();
    }

    @QueryTarget(name = "hud:loaded")
    public void setLoaded(JsonObject jsonObject) {
        loaded = true;
    }

    @EventTarget
    public void tick(TickGameOverlayEvent event) {
        watcherValues.forEach(WatcherValue::tick);
        watcherPlayer.tick();
        cefBrowser.wasResized_(minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight());

        if (loaded) {
            queueUpdates.forEach(Runnable::run);
            queueUpdates.clear();
        }
    }

    @EventTarget
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (HIDDEN_ELEMENTS.contains(event.getType()))
            event.setCanceled(true);

        if (event.getType().equals(ElementType.ALL))
            render();
    }
}

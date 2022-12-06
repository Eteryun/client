package com.eteryun.modules.cef.screen;

import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.query.QueryTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.cef.browser.CefBrowserCustom;
import org.cef.browser.ICefRenderer;
import org.cef.browser.lwjgl.CefRendererLwjgl;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class CefScreen extends Screen {
    private CefBrowserCustom cefBrowser;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private ICefRenderer cefRenderer;

    public CefScreen(String url) {
        super(new TextComponent("CefScreen"));
        cefRenderer = new CefRendererLwjgl(true);
        cefBrowser = new CefBrowserCustom(CefManager.cefClient, url, true, null, cefRenderer);
        cefBrowser.setCloseAllowed();
        cefBrowser.createImmediately();

        CefManager.getInstance().registerQueryHandler(this);
    }

    public CefScreen() {
        this("https://google.com.br");
    }

    @Override
    protected void init() {
        super.init();
        cefBrowser.setFocus(true);
        cefBrowser.wasResized_(minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight());
    }

    @Override
    public void onClose() {
        super.onClose();
        cefBrowser.close(true);
    }

    public void loadUrl(String url) {
        cefBrowser.loadURL(url);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        super.render(poseStack, mouseX, mouseY, delta);

        GlStateManager._enableDepthTest();
        GlStateManager._enableTexture();
        cefBrowser.wasResized_(minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight());
        cefRenderer.render(0,0, width, height);
        GlStateManager._disableDepthTest();
    }

    @Override
    public void mouseMoved(double xPos, double yPos) {
        cefBrowser.mouseMoved((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0);
        super.mouseMoved(xPos, yPos);
    }

    @Override
    public boolean mouseReleased(double xPos, double yPos, int btn) {
        if (btn == -1) {
            cefBrowser.mouseMoved((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0);
        } else {
            cefBrowser.mouseInteracted((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0, btn, false, 1);
        }
        return super.mouseReleased(xPos, yPos, btn);
    }

    @Override
    public boolean mouseClicked(double xPos, double yPos, int btn) {
        if (btn == -1) {
            cefBrowser.mouseMoved((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0);
        } else {
            cefBrowser.mouseInteracted((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0, btn, true, 1);
        }
        return super.mouseClicked(xPos, yPos, btn);
    }

    @Override
    public boolean mouseScrolled(double xPos, double yPos, double scrolled) {
        cefBrowser.mouseScrolled((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0, 1, (int)scrolled * 120);
        return super.mouseScrolled(xPos, yPos, scrolled);
    }

    @Override
    public boolean mouseDragged(double xPos, double yPos, int btn, double xDelta, double yDelta) {
        cefBrowser.mouseDragged((int) minecraft.mouseHandler.xpos(), (int) minecraft.mouseHandler.ypos(), 0, btn);
        return super.mouseDragged(xPos, yPos, btn, xDelta, yDelta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.keyChanged(keyCode, scanCode, modifiers, true);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.keyChanged(keyCode, scanCode, modifiers, false);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean charTyped(char c, int i) {
        cefBrowser.keyTyped(c, i);
        return true;
    }

    public void keyChanged(int keyCode, int scanCode, int modifiers, boolean pressed) {
        String keystr = GLFW.glfwGetKeyName(keyCode, scanCode);

        char key = keystr == null || keystr.length() == 0 ? 0 : keystr.charAt(keystr.length() - 1);
        key = (char) CefBrowserCustom.remapKeycode(keyCode, key);

        cefBrowser.keyEventByKeyCode(keyCode, key, modifiers, pressed);

        switch (keyCode) {
            case GLFW_KEY_ENTER:
                cefBrowser.keyTyped(13, 0);
                break;
            case GLFW_KEY_BACKSPACE:
            case GLFW_KEY_ESCAPE:
                cefBrowser.keyTyped(keyCode, 0);
        }
    }

    @QueryTarget(name = "getTranslate")
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
}

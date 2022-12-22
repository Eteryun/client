package com.eteryun.modules.ui.mixin.gui;

import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.utils.TranslateUtils;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.cef.browser.CefBrowserCustom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReceivingLevelScreen.class)
public class ReceivingLevelScreenMixin extends Screen {
    protected CefBrowserCustom cefBrowser;

    protected ReceivingLevelScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(CallbackInfo ci) {
        cefBrowser = new CefBrowserCustom(CefManager.cefClient, "http://ui.eteryun.com.br/screens/#/receiving", true, null);
        cefBrowser.setCloseAllowed();
        cefBrowser.createImmediately();

        CefManager.getInstance().registerQueryHandler(this);
    }

    @Override
    public void onClose() {
        remove();
        super.onClose();
    }

    @Override
    public void removed() {
        remove();
    }

    @Override
    protected void init() {
        cefBrowser.setFocus(true);
        cefBrowser.wasResized_(minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight());
        cefBrowser.sendMessage("getTranslate", null);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        cefBrowser.wasResized_(this.minecraft.getWindow().getScreenWidth(), this.minecraft.getWindow().getScreenHeight());
        GlStateManager._enableDepthTest();
        cefBrowser.draw(0, 0, width, height);
        GlStateManager._disableDepthTest();
    }

    @QueryTarget(name = "getTranslate")
    public String getTranslate(JsonObject object) {
        return TranslateUtils.getJson();
    }

    public void remove(){
        CefManager.getInstance().unregisterQueryHandler(this);
        cefBrowser.close(true);
    }
}

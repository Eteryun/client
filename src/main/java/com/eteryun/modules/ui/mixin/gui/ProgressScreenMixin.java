package com.eteryun.modules.ui.mixin.gui;

import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.utils.TranslateUtils;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.cef.browser.CefBrowserCustom;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ProgressScreen.class)
public class ProgressScreenMixin extends Screen {
    @Shadow
    private int progress;
    @Shadow
    private @Nullable Component stage;
    protected CefBrowserCustom cefBrowser;
    private boolean isLoaded = false;
    private final List<Runnable> pendentsRunnable = new ArrayList<>();

    protected ProgressScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(boolean bl, CallbackInfo ci) {
        cefBrowser = new CefBrowserCustom(CefManager.cefClient, "http://ui.eteryun.com.br/screens/#/progress", true, null);
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ProgressScreen;renderBackground(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        cefBrowser.wasResized_(this.minecraft.getWindow().getScreenWidth(), this.minecraft.getWindow().getScreenHeight());
        GlStateManager._enableDepthTest();
        cefBrowser.draw(0, 0, width, height);
        GlStateManager._disableDepthTest();
    }

    @Inject(method = "progressStart", at = @At("HEAD"), cancellable = true)
    private void progressStartMixin(Component component, CallbackInfo ci) {
        Runnable runnableTitle = sendMessage("setTitle", component);
        if (!isLoaded) {
            if (component != null)
                pendentsRunnable.add(runnableTitle);
        } else {
            if (component != null)
                runnableTitle.run();
        }
    }

    @Inject(method = "progressStagePercentage", at = @At("RETURN"), cancellable = true)
    private void progressStagePercentageMixin(int i, CallbackInfo ci) {
        Runnable runnableStage = sendMessage("setStage", new TextComponent("").append(this.stage).append(" " + this.progress + "%"));
        if (!isLoaded) {
            if (progress != 0 && stage != null)
                pendentsRunnable.add(runnableStage);
        } else {
            if (progress != 0 && stage != null)
                runnableStage.run();
        }
    }

    private Runnable sendMessage(String type, Component component) {
        return new Runnable() {
            @Override
            public void run() {
                cefBrowser.sendMessage(type, component instanceof TranslatableComponent ? ((TranslatableComponent) component).getKey() : component.getString());
            }
        };
    }

    @QueryTarget(name = "getTranslate")
    public String getTranslate(JsonObject object) {
        return TranslateUtils.getJson();
    }

    @QueryTarget(name = "loaded")
    public void setLoaded(JsonObject jsonObject) {
        isLoaded = true;
        pendentsRunnable.forEach(Runnable::run);
    }

    public void remove(){
        CefManager.getInstance().unregisterQueryHandler(this);
        cefBrowser.close(true);
    }
}

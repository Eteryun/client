package com.eteryun.modules.ui.mixin.gui;

import com.eteryun.modules.cef.CefManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.cef.browser.CefBrowserCustom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Shadow @Final private ReloadInstance reload;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private Consumer<Optional<Throwable>> onFinish;
    protected CefBrowserCustom cefBrowser;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Minecraft minecraft, ReloadInstance reloadInstance, Consumer consumer, boolean bl, CallbackInfo ci) {
        cefBrowser = new CefBrowserCustom(CefManager.cefClient, "https://ui.eteryun.com.br/loading/", true, null);
        cefBrowser.setCloseAllowed();
        cefBrowser.createImmediately();
        cefBrowser.setFocus(true);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        cefBrowser.wasResized_(this.minecraft.getWindow().getScreenWidth(), this.minecraft.getWindow().getScreenHeight());
        GlStateManager._enableDepthTest();
        cefBrowser.draw(0,0, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        GlStateManager._disableDepthTest();

        cefBrowser.sendMessage("updateProgress", this.reload.getActualProgress() * 100);

        if (this.reload.isDone()) {
            if (this.minecraft.screen != null)
                this.minecraft.screen.render(poseStack, 0,0, f);

            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }

            this.minecraft.setOverlay(null);
            remove();
            cefBrowser.sendMessage("forceUpdate", null);
        }
    }

    public void remove(){
        CefManager.getInstance().unregisterQueryHandler(this);
        cefBrowser.close(true);
    }
}

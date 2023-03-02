package com.eteryun.modules.skills.network.client;

import com.eteryun.Eteryun;
import com.eteryun.modules.IModule;
import com.eteryun.modules.ui.UiModule;
import com.eteryun.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import org.cef.browser.CefBrowserCustom;

import java.util.Optional;

public class ClientboundPacketPlayerCastSkill implements Packet {
    String id;

    public ClientboundPacketPlayerCastSkill(String id) {
        this.id = id;
    }

    public ClientboundPacketPlayerCastSkill(FriendlyByteBuf buffer) {
        this.id =  buffer.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {

    }

    @Override
    public void handle() {
        Optional<IModule> module = Eteryun.getModule("ui");
        if (module.isPresent()) {
            UiModule uiModule = (UiModule) module.get();
            uiModule.getGui().sendMessage("castSkill", CefBrowserCustom.createDefaultDto(id));
        }
    }
}

package com.eteryun.modules.stats.network.client;

import com.eteryun.Eteryun;
import com.eteryun.modules.IModule;
import com.eteryun.modules.ui.UiModule;
import com.eteryun.network.Packet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public class ClientboundPacketPlayerStats implements Packet {
    private double value;
    private PlayerStats playerStats;

    public ClientboundPacketPlayerStats(double value, PlayerStats playerStats) {
        this.playerStats = playerStats;
        this.value = value;
    }

    public ClientboundPacketPlayerStats(FriendlyByteBuf buffer) {
        this.playerStats = buffer.readEnum(PlayerStats.class);
        this.value = buffer.readDouble();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        // nope
    }

    @Override
    public void handle() {
        Optional<IModule> module = Eteryun.getModule("ui");
        if (module.isPresent()) {
            UiModule uiModule = (UiModule) module.get();
            switch (playerStats) {
                case MANA -> uiModule.getGui().updatePartialUser("mana", value);
                case MAX_MANA -> uiModule.getGui().updatePartialUser("maxMana", value);
            }
        }
    }

    public enum PlayerStats {
        MANA,
        MAX_MANA
    }
}

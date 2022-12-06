package com.eteryun.modules.backtoo;

import com.eteryun.event.EventManager;
import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.input.KeyInputEvent;
import com.eteryun.modules.IModule;
import com.eteryun.modules.Module;
import com.eteryun.modules.backtoo.network.server.ServerboundPacketPlayerAction;
import com.eteryun.network.PacketsProtocol;
import com.eteryun.utils.KeyMappingsHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketFlow;
import org.lwjgl.glfw.GLFW;

@Module
public class BacktooModule implements IModule {
    private final KeyMapping KEY_BACK_TOOL = new KeyMapping("et.swap.backtool", GLFW.GLFW_KEY_G, KeyMapping.CATEGORY_INVENTORY);
    private Minecraft minecraft = Minecraft.getInstance();

    @Override
    public void preInit() {
        PacketsProtocol.registerPacket(PacketFlow.SERVERBOUND, ServerboundPacketPlayerAction.class, ServerboundPacketPlayerAction::new);
        KeyMappingsHelper.registerKeyMapping(KEY_BACK_TOOL);
        EventManager.register(this);
    }

    @Override
    public void init() {
    }

    @Override
    public void shutdown() {
    }

    @EventTarget
    public void onKeyEvent(KeyInputEvent event) {
        if (event.getAction() == 0) {
            if (KEY_BACK_TOOL.consumeClick() && !minecraft.player.isSpectator()) {
                PacketsProtocol.sendPacket(new ServerboundPacketPlayerAction(minecraft.player.getUUID(), ServerboundPacketPlayerAction.Action.SWAP_BACKTOOL));
            }
        }
    }
}

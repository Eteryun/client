package com.eteryun.modules.skills;

import com.eteryun.event.EventManager;
import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.input.KeyInputEvent;
import com.eteryun.modules.IModule;
import com.eteryun.modules.skills.network.client.ClientboundPacketPlayerCastSkill;
import com.eteryun.modules.skills.network.client.ClientboundPacketPlayerSkills;
import com.eteryun.modules.skills.network.server.ServerboundPacketPlayerCastSkill;
import com.eteryun.network.PacketsProtocol;
import com.eteryun.utils.KeyMappingsHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketFlow;
import org.lwjgl.glfw.GLFW;

public class SkillsModule implements IModule {
    private static final KeyMapping KEY_SKILL_1 = new KeyMapping("et.slot.1", GLFW.GLFW_KEY_Z, "key.categories.skills");
    private static final KeyMapping KEY_SKILL_2 = new KeyMapping("et.slot.2", GLFW.GLFW_KEY_X, "key.categories.skills");
    private static final KeyMapping KEY_SKILL_3 = new KeyMapping("et.slot.3", GLFW.GLFW_KEY_C, "key.categories.skills");
    private static final KeyMapping KEY_SKILL_4 = new KeyMapping("et.slot.4", GLFW.GLFW_KEY_V, "key.categories.skills");
    private static final KeyMapping KEY_SKILL_5 = new KeyMapping("et.slot.5", GLFW.GLFW_KEY_B, "key.categories.skills");
    private static final KeyMapping KEY_SKILL_6 = new KeyMapping("et.slot.6", GLFW.GLFW_KEY_N, "key.categories.skills");

    public static final KeyMapping[] KEY_SKILLS = new KeyMapping[]{KEY_SKILL_1, KEY_SKILL_2, KEY_SKILL_3, KEY_SKILL_4, KEY_SKILL_5, KEY_SKILL_6};

    private Minecraft minecraft = Minecraft.getInstance();

    @Override
    public String name() {
        return "skills";
    }

    @Override
    public void preInit() {
        KeyMappingsHelper.registerKeyMappings(KEY_SKILLS);
        PacketsProtocol.registerPacket(PacketFlow.CLIENTBOUND, ClientboundPacketPlayerSkills.class, ClientboundPacketPlayerSkills::new);
        PacketsProtocol.registerPacket(PacketFlow.CLIENTBOUND, ClientboundPacketPlayerCastSkill.class, ClientboundPacketPlayerCastSkill::new);

        PacketsProtocol.registerPacket(PacketFlow.SERVERBOUND, ServerboundPacketPlayerCastSkill.class, ServerboundPacketPlayerCastSkill::new);
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
            for (int i = 0; i < KEY_SKILLS.length; i++) {
                KeyMapping keyMapping = KEY_SKILLS[i];
                if (keyMapping.consumeClick()) {
                    PacketsProtocol.sendPacket(new ServerboundPacketPlayerCastSkill(minecraft.player.getUUID(), i));
                }
            }
        }
    }
}

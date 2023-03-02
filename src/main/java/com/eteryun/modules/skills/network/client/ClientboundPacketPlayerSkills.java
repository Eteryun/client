package com.eteryun.modules.skills.network.client;

import com.eteryun.Eteryun;
import com.eteryun.modules.IModule;
import com.eteryun.modules.ui.UiModule;
import com.eteryun.network.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Optional;

import static com.eteryun.modules.skills.SkillsModule.KEY_SKILLS;

public class ClientboundPacketPlayerSkills implements Packet {
    private ArrayList<Skill> skills;
    private ArrayList<Skill> passives;

    public ClientboundPacketPlayerSkills(ArrayList<Skill> skills, ArrayList<Skill> passives) {
        this.skills = skills;
        this.passives = passives;
    }

    public ClientboundPacketPlayerSkills(FriendlyByteBuf buffer) {
        this.skills = new ArrayList<>();
        int skillsSize = buffer.readInt();
        for (int i = 0; i < skillsSize; i++) {
            String id = buffer.readUtf();
            String icon = buffer.readUtf();
            double cost = buffer.readDouble();
            double cooldown = buffer.readDouble();
            int slot = buffer.readInt();
            skills.add(new Skill(id, icon, cost, cooldown, slot));
        }

        this.passives = new ArrayList<>();
        int passivesSize = buffer.readInt();
        for (int i = 0; i < passivesSize; i++) {
            String id = buffer.readUtf();
            String icon = buffer.readUtf();
            double cooldown = buffer.readDouble();
            passives.add(new Skill(id, icon, cooldown));
        }
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
            uiModule.getGui().updatePartialUser("skills", skillsToJsonArray(skills));
            uiModule.getGui().updatePartialUser("passives", passiveToJsonArray(passives));
        }
    }

    private JsonArray skillsToJsonArray(ArrayList<Skill> skills) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < 6; i++) {
            int slot = i;
            JsonObject jsonObject = new JsonObject();

            Optional<Skill> optionalSkill = skills.stream().filter(skill1 -> skill1.getSlot() == slot).findFirst();
            KeyMapping keyMapping = KEY_SKILLS[slot];
            if (optionalSkill.isPresent()) {
                Skill skill = optionalSkill.get();
                jsonObject.addProperty("id", skill.getId());
                jsonObject.addProperty("icon", skill.getIcon());
                jsonObject.addProperty("cost", skill.getCost());
                jsonObject.addProperty("cooldown", skill.getCooldown());
                jsonObject.addProperty("slot", skill.getSlot());
                jsonObject.addProperty("keybinding", keyMapping.saveString());
            } else {
                jsonObject.addProperty("id", "skill_" + slot);
                jsonObject.addProperty("slot", slot);
                jsonObject.addProperty("keybinding", keyMapping.saveString());
            }

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private JsonArray passiveToJsonArray(ArrayList<Skill> passives) {
        JsonArray jsonArray = new JsonArray();
        passives.forEach(passive -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", passive.getId());
            jsonObject.addProperty("icon", passive.getIcon());
            jsonObject.addProperty("cooldown", passive.getCooldown());
            jsonArray.add(jsonObject);
        });
        return jsonArray;
    }

    public class Skill {
        private String id;
        private String icon;
        private double cost;
        private double cooldown;
        private int slot;

        public Skill(String id, String icon, double cost, double cooldown, int slot) {
            this.id = id;
            this.icon = icon;
            this.cost = cost;
            this.cooldown = cooldown;
            this.slot = slot;
        }

        public Skill(String id, String icon, double cooldown) {
            this.id = id;
            this.icon = icon;
            this.cost = 0;
            this.cooldown = cooldown;
            this.slot = -1;
        }

        public String getId() {
            return id;
        }

        public String getIcon() {
            return icon;
        }

        public double getCost() {
            return cost;
        }

        public double getCooldown() {
            return cooldown;
        }

        public int getSlot() {
            return slot;
        }
    }
}

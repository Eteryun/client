package com.eteryun.launch;

import com.eteryun.launch.agent.Agent;
import cpw.mods.modlauncher.Launcher;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        String username = "Player" + Util.getMillis() % 1000L;

        arguments.add("--launchTarget");
        arguments.add("eteryunlaunch");

        if (Boolean.getBoolean("fabric.development")) {
            arguments.add("--version");
            arguments.add("dev");

            arguments.add("--username");
            arguments.add("Eteryun");

            arguments.add("--uuid");
            arguments.add(Player.createPlayerUUID(username).toString());

            arguments.add("--accessToken");
            arguments.add("a");
        }
        Agent.updateSecurity();

        Launcher.main(arguments.toArray(String[]::new));
    }
}

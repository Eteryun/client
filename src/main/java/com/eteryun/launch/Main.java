package com.eteryun.launch;

import net.minecraft.Util;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        String username = "Player";

        arguments.add("--tweakClass");
        arguments.add("com.eteryun.launch.EteryunTweaker");

        if (Boolean.getBoolean("fabric.development")) {
            arguments.add("--version");
            arguments.add("dev");

            arguments.add("--username");
            arguments.add(username);

            arguments.add("--uuid");
            arguments.add(Player.createPlayerUUID(username).toString());

            arguments.add("--accessToken");
            arguments.add("0");
        }

        Launch.main(arguments.toArray(String[]::new));
    }
}

package com.eteryun.modules.ui;

import com.eteryun.event.EventManager;
import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.screen.ScreenOpenEvent;
import com.eteryun.modules.IModule;
import com.eteryun.modules.Module;
import com.eteryun.modules.ui.screens.MainScreen;
import com.eteryun.modules.ui.screens.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;

import java.lang.reflect.Field;

@Module
public class UiModule implements IModule {
    @Override
    public void preInit() {
        EventManager.register(this);
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }

    @EventTarget
    public void onScreenOpen(ScreenOpenEvent event)  {
        Screen screen = event.getScreen();
        if (screen instanceof TitleScreen || screen instanceof JoinMultiplayerScreen) {
            event.setScreen(new MainScreen());
        }

        if (screen instanceof OptionsScreen) {
            Screen lastScreen = Minecraft.getInstance().screen;
            try {
                Field field = screen.getClass().getDeclaredField("lastScreen");
                if (field.trySetAccessible()) {
                    field.setAccessible(true);
                    lastScreen = (Screen) field.get(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.setScreen(new SettingsScreen(lastScreen,true));
        }
    }
}

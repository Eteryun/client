package com.eteryun.modules.ui;

import com.eteryun.event.EventManager;
import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.screen.ScreenOpenEvent;
import com.eteryun.modules.IModule;
import com.eteryun.modules.ui.screens.DisconnectScreen;
import com.eteryun.modules.ui.screens.MainScreen;
import com.eteryun.modules.ui.screens.SettingsScreen;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.lang.reflect.Field;

public class UiModule implements IModule {
    InGameGui gui;

    @Override
    public String name() {
        return "ui";
    }

    @Override
    public void preInit() {
        EventManager.register(this);
    }

    @Override
    public void init() {
        gui = new InGameGui();
    }

    @Override
    public void shutdown() {

    }

    @EventTarget
    public void onScreenOpen(ScreenOpenEvent event) {
        Screen screen = event.getScreen();
        if (screen instanceof TitleScreen || screen instanceof JoinMultiplayerScreen) {
            event.setScreen(new MainScreen());
        }

        if (screen instanceof OptionsScreen) {
            Screen lastScreen = null;
            event.setScreen(new SettingsScreen(lastScreen, true));
        }

        if (screen instanceof DisconnectedScreen) {
            Screen lastScreen = null;
            Component title = screen != null ? screen.getTitle() : CommonComponents.CONNECT_FAILED;
            Component reason = new TranslatableComponent("disconnect.unknownHost");
            if (screen != null) {
                Object fieldValue = getDeclaredField("reason", screen);
                if (fieldValue != null)
                    reason = (Component) fieldValue;
            }
            event.setScreen(new DisconnectScreen(lastScreen, title, reason));
        }

        if (screen instanceof PauseScreen) {
            event.setScreen(new com.eteryun.modules.ui.screens.PauseScreen());
        }
    }

    public Object getDeclaredField(String name, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            if (field.trySetAccessible()) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public InGameGui getGui() {
        return gui;
    }
}

package com.eteryun.modules.ui.mixin.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(Gui.class)
public interface GuiAccessor {
    @Accessor("title")
     Component getTitle();

    @Accessor("subtitle")
     Component getSubtitle();

    @Accessor("titleStayTime")
     int getTitleTime();
}

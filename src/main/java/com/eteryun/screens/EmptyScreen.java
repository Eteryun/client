package com.eteryun.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ramon.ultralight.UltralightEngine;
import com.ramon.ultralight.View.ScreenView;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;

public class EmptyScreen extends Screen {
	private ScreenView view;
	
	public EmptyScreen(String screenName) {
		super(NarratorChatListener.NO_TITLE);
		this.view = UltralightEngine.getInstance().newScreenView(screenName, this, this, this);
	}

	public void loadUrl(String string) {
		view.loadUrl(string);
	}

	public void sendMessage(String type, Object obj) {
		view.sendViewMessage(type, obj);
	}
}

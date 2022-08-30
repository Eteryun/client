package com.ramon.ultralight.js;

import com.eteryun.Eteryun;
import com.eteryun.ui.events.UIEventManager;
import com.labymedia.ultralight.databind.api.JavaAPI;
import com.labymedia.ultralight.javascript.JavascriptContext;
import com.labymedia.ultralight.javascript.JavascriptGlobalContext;
import com.labymedia.ultralight.javascript.JavascriptObject;
import com.labymedia.ultralight.javascript.JavascriptValue;
import com.ramon.ultralight.View;
import com.ramon.ultralight.View.ScreenView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ContextSetup {
	public static void setupContext(View view, JavascriptContext context) {
		JavaAPI javaApi = new JavaAPI(view.databind);
		JavascriptGlobalContext globalContext = context.getGlobalContext();

		JavascriptObject globalObject = globalContext.getGlobalObject();

		Minecraft mc = Minecraft.getInstance();

		globalObject.setProperty("minecraft", view.databind.getConversionUtils().toJavascript(context, mc), 0);

		globalObject.setProperty("options",
				view.databind.getConversionUtils().toJavascript(context, mc.options), 0);

		globalObject.setProperty("eteryun",
				view.databind.getConversionUtils().toJavascript(context, Eteryun.getInstance()), 0);

		JavascriptValue translatedApi = view.databind.getConversionUtils().toJavascript(context, javaApi);
		globalObject.setProperty("java", translatedApi, 0);

		globalObject.setProperty("uievent",
				view.databind.getConversionUtils().toJavascript(context, UIEventManager.getInstance()), 0);

		if (view instanceof ScreenView) {
			ScreenView screenView = (ScreenView) view;
			globalObject.setProperty("minecraftScreen", view.databind.getConversionUtils().toJavascript(context,
					screenView.adaptedScreen != null ? screenView.adaptedScreen : screenView.screen), 0);

			Screen parentScreen = screenView.parentScreen;

			if (parentScreen != null) {
				globalObject.setProperty("parentScreen",
						view.databind.getConversionUtils().toJavascript(context, screenView.parentScreen), 0);
			}
		}
	}
}

package com.ramon.ultralight;

import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.UltralightViewConfig;
import com.labymedia.ultralight.databind.Databind;
import com.labymedia.ultralight.databind.DatabindConfiguration;
import com.labymedia.ultralight.input.UltralightKeyEvent;
import com.labymedia.ultralight.input.UltralightMouseEvent;
import com.labymedia.ultralight.input.UltralightScrollEvent;
import com.labymedia.ultralight.javascript.JavascriptContextLock;
import com.labymedia.ultralight.javascript.JavascriptEvaluationException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ramon.ultralight.UltralightEngine.RenderLayer;
import com.ramon.ultralight.js.ViewContextProvider;
import com.ramon.ultralight.listener.ViewListener;
import com.ramon.ultralight.listener.ViewLoadListener;
import com.ramon.ultralight.renderer.ViewRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.Logger;

public class View {
	public UltralightView ultralightView;
	public Databind databind;
	private long jsGarbageCollected = 0L;
	private final ViewRenderer viewRenderer;
	public final RenderLayer layer;
	private final String name;
	private Logger logger = UltralightEngine.getLogger();

	public View(String name, RenderLayer renderLayer, UltralightRenderer ultralightRenderer,
			ViewRenderer viewRenderer) {
		this.name = name;
		this.viewRenderer = viewRenderer;
		this.layer = renderLayer;
		Minecraft mc = Minecraft.getInstance();
		int width = mc.getWindow().getWidth();
		int height = mc.getWindow().getHeight();
		UltralightViewConfig viewConfig = new UltralightViewConfig().isTransparent(true).initialDeviceScale(1.0);

		viewRenderer.setupConfig(viewConfig);
		ultralightView = ultralightRenderer.createView(width, height, viewConfig);
		ultralightView.setViewListener(new ViewListener());
		ultralightView.setLoadListener(new ViewLoadListener(this));

		databind = new Databind(DatabindConfiguration.builder()
				.contextProviderFactory(new ViewContextProvider.Factory(this.ultralightView)).build());

		logger.debug("Created new view " + toString());
	}

	public void loadHTML(String html) {
		ultralightView.loadHTML(html);

		logger.debug("Loaded html");
	}

	public void loadUrl(String url) {
		ultralightView.loadURL(url);

		logger.debug("Loaded html from " + url);
	}

	public String getName() {
		return this.name;
	}

	public void update() {
		collectGarbage();
	}

	public void render(PoseStack matrices) {
		viewRenderer.render(ultralightView, matrices);
	}

	public void resize(int width, int height) {
		this.ultralightView.resize(width, height);
		logger.debug("Resized " + toString() + " to (w: " + width + " h: " + height + ")");
	}

	private void collectGarbage() {
		if (jsGarbageCollected == 0) {
			jsGarbageCollected = System.currentTimeMillis();
		} else if (System.currentTimeMillis() - jsGarbageCollected > 1000) {
			logger.debug("Garbage collecting Javascript...");
			try (JavascriptContextLock lock = this.ultralightView.lockJavascriptContext()) {
				lock.getContext().garbageCollect();
			}
			jsGarbageCollected = System.currentTimeMillis();
		}
	}

	public void free() {
		ultralightView.loadHTML("");
		ultralightView.unfocus();
		ultralightView.stop();
		viewRenderer.delete();
	}

	public void focus() {
		ultralightView.focus();
	}

	public void unfocus() {
		ultralightView.unfocus();
	}

	public void fireScrollEvent(UltralightScrollEvent event) {
		ultralightView.fireScrollEvent(event);
	}

	public void fireMouseEvent(UltralightMouseEvent event) {
		ultralightView.fireMouseEvent(event);
	}

	public void fireKeyEvent(UltralightKeyEvent event) {
		ultralightView.fireKeyEvent(event);
	}

	public void sendViewMessage(String type, Object obj) {
		try {
			ultralightView.evaluateScript("window.dispatchEvent(new CustomEvent(\"mMessage\", { detail: { type: '"
					+ type + "', data: '" + obj + "' } }));");
		} catch (JavascriptEvaluationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "View(url: " + ultralightView.url() + ", w: " + ultralightView.width() + " , h: "
				+ ultralightView.height() + ")";
	}

	public static class ScreenView extends View {
		public Screen screen;
		public Screen adaptedScreen;
		public Screen parentScreen;

		public ScreenView(String name, UltralightRenderer ultralightRenderer, ViewRenderer viewRenderer, Screen screen,
				Screen adaptedScreen, Screen parentScreen) {
			super(name, RenderLayer.SCREEN_LAYER, ultralightRenderer, viewRenderer);

			this.screen = screen;
			this.adaptedScreen = adaptedScreen;
			this.parentScreen = parentScreen;
		}
	}
}

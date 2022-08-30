package com.ramon.ultralight;

import java.util.HashMap;
import java.util.Optional;

import com.eteryun.event.EventManager;
import com.labymedia.ultralight.UltralightPlatform;
import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.config.FontHinting;
import com.labymedia.ultralight.config.UltralightConfig;
import com.labymedia.ultralight.plugin.logging.UltralightLogLevel;
import com.labymedia.ultralight.plugin.logging.UltralightLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ramon.ultralight.View.ScreenView;
import com.ramon.ultralight.glfw.GlfwClipboardAdapter;
import com.ramon.ultralight.glfw.GlfwCursorAdapter;
import com.ramon.ultralight.glfw.GlfwInputAdapter;
import com.ramon.ultralight.renderer.CpuViewRenderer;
import com.ramon.ultralight.renderer.ViewRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UltralightEngine {
	private static UltralightEngine instance;
	private static final Logger logger = LogManager.getLogger("Ultralight");
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final long window = minecraft.getWindow().getWindow();
	
	private static UltralightPlatform platform;
	private final UltralightRenderer renderer;
	
	public final GlfwClipboardAdapter clipboardAdapter;
	public final GlfwCursorAdapter cursorAdapter;
	public final GlfwInputAdapter inputAdapter;
	
	private static HashMap<String, View> views = new HashMap<>();
	
	public UltralightEngine() {
		instance = this;
		int refreshRate = minecraft.getWindow().getRefreshRate();
		
		clipboardAdapter = new GlfwClipboardAdapter();
		cursorAdapter = new GlfwCursorAdapter();
		inputAdapter = new GlfwInputAdapter();

		logger.info("Checando Bibliotecas");
		UltralightResources.downloadLibraries();

		logger.info("Carregando Bibliotecas");
		UltralightResources.loadLibraries();

		logger.info("Baixando as telas");
		UltralightResources.downloadNUIS();

		logger.info("Configurando plataforma do Ultralight");
		platform = UltralightPlatform.instance();
		UltralightConfig config = new UltralightConfig();
		config.animationTimerDelay(1.0 / refreshRate);
		config.scrollTimerDelay(1.0 / refreshRate);
		config.cachePath(UltralightResources.ultralightCache.getAbsolutePath());
		config.fontHinting(FontHinting.SMOOTH);
		platform.setConfig(config);
		platform.usePlatformFontLoader();
		platform.usePlatformFileSystem(UltralightResources.ultralightRoot.getAbsolutePath());
		platform.setClipboard(clipboardAdapter);
		platform.setLogger(utralightLogger);

		logger.info("Configurando renderização do Ultralight");
		renderer = UltralightRenderer.create();
		
		EventManager.register(new UltralightEvents());

		logger.info("Carregado Ultralight com sucesso");
	}
	
	public void shutdown() {
		cursorAdapter.cleanup();
	}
	
	public void update() {
		views.values().forEach(View::update);
		renderer.update();
	}

	public void render(RenderLayer layer, PoseStack matrices) {
		renderer.render();

		switch (layer) {
		case SCREEN_LAYER:
			if (getActiveView() != null)
				getActiveView().render(matrices);
			break;
		default:
			views.values().stream().filter(view -> view.layer == layer).forEach(view -> view.render(matrices));
			break;
		}
	}
	
	public void resize(int width, int height) {
		views.values().forEach(view -> view.resize(width, height));
	}
	
	public void sendMessage(String name, String type, Object obj) {
		View view = views.get(name);
		if (view != null)
			view.sendViewMessage(type, obj);
	}
	
	public void sendMessageAll(String type, Object obj) {
		views.values().forEach(view -> {
			view.sendViewMessage(type, obj);
		});
	}
	
	public void removeView(String name) {
		View view = views.get(name);
		view.free();
		views.remove(name);
	}
	
	private ViewRenderer newViewRenderer() {
		return new CpuViewRenderer();
	}
	
	public View newOverlayView(String name) {
		View view = new View(name, RenderLayer.OVERLAY_LAYER, renderer, newViewRenderer());
		views.put(name, view);
		return view;
	}

	public View newSplashView(String name) {
		View view = new View(name, RenderLayer.SPLASH_LAYER, renderer, newViewRenderer());
		views.put(name, view);
		return view;
	}

	public ScreenView newScreenView(String name, Screen screen, Screen adaptedScreen, Screen parentScreen) {
		ScreenView view = new ScreenView(name, renderer, newViewRenderer(), screen, adaptedScreen, parentScreen);
		views.put(name, view);
		return view;
	}
	
	public static View getActiveView() {
		Optional<View> optional = views.values().stream()
				.filter(view -> view instanceof ScreenView && minecraft.screen == ((ScreenView) view).screen).findFirst();

		if (optional.isPresent())
			return optional.get();
		return null;
	}
	
	public static UltralightEngine getInstance() {
		return instance;
	}

	public static Logger getLogger() {
		return logger;
	}
	
	public static HashMap<String, View> getViews() {
		return views;
	}
	
	public static long getWindow() {
		return window;
	}
	
	public static enum RenderLayer {
		OVERLAY_LAYER, SCREEN_LAYER, SPLASH_LAYER
	}
	
	private UltralightLogger utralightLogger = new UltralightLogger() {
		@Override
		public void logMessage(UltralightLogLevel level, String message) {
			switch (level) {
			case ERROR:
				logger.error(message);
				break;

			case WARNING:
				logger.warn(message);
				break;

			case INFO:
				logger.info(message);
				break;
			}
		}
	};
}

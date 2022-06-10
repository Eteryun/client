package com.ramon.ultralight.listener;

import com.labymedia.ultralight.javascript.JavascriptContext;
import com.labymedia.ultralight.javascript.JavascriptContextLock;
import com.labymedia.ultralight.plugin.loading.UltralightLoadListener;
import com.ramon.ultralight.UltralightEngine;
import com.ramon.ultralight.View;
import com.ramon.ultralight.js.ContextSetup;
import org.apache.logging.log4j.Logger;

public class ViewLoadListener implements UltralightLoadListener {
	private Logger logger = UltralightEngine.getLogger();
	private final View view;

	public ViewLoadListener(View view) {
		this.view = view;
	}

	private String frameName(long frameId, boolean isMainFrame, String url) {
		return "[" + (isMainFrame ? "MainFrame " : "Frame ") + frameId + " (" + url + ")" + "]: ";
	}

	@Override
	public void onBeginLoading(long frameId, boolean isMainFrame, String url) {
		logger.info(frameName(frameId, isMainFrame, url) + "The view is about to load");
	}

	@Override
	public void onFinishLoading(long frameId, boolean isMainFrame, String url) {
		logger.info(frameName(frameId, isMainFrame, url) + "The view finished loading");
	}

	@Override
	public void onFailLoading(long frameId, boolean isMainFrame, String url, String description, String errorDomain,
			int errorCode) {
		logger.error(frameName(frameId, isMainFrame, url) + "Failed to load " + errorDomain + " " + errorCode + "("
				+ description + ")");
	}

	@Override
	public void onUpdateHistory() {
	}

	@Override
	public void onWindowObjectReady(long frameId, boolean isMainFrame, String url) {
		try (JavascriptContextLock lock = this.view.ultralightView.lockJavascriptContext()) {
			JavascriptContext context = lock.getContext();
			ContextSetup.setupContext(view, context);
		}
	}

	@Override
	public void onDOMReady(long frameId, boolean isMainFrame, String url) {
	}
}

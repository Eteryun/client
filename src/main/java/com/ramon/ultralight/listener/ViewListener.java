package com.ramon.ultralight.listener;

import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.input.UltralightCursor;
import com.labymedia.ultralight.math.IntRect;
import com.labymedia.ultralight.plugin.view.MessageLevel;
import com.labymedia.ultralight.plugin.view.MessageSource;
import com.labymedia.ultralight.plugin.view.UltralightViewListener;
import com.ramon.ultralight.UltralightEngine;
import org.apache.logging.log4j.Logger;

public class ViewListener implements UltralightViewListener {
	private Logger logger = UltralightEngine.getLogger();

	@Override
	public void onChangeTitle(String title) { }

	@Override
	public void onChangeURL(String url) {
		logger.debug("View url has changed: " + url);
	}

	@Override
	public void onChangeTooltip(String tooltip) { }

	@Override
	public void onChangeCursor(UltralightCursor cursor) { }

	@Override
	public void onAddConsoleMessage(MessageSource source, MessageLevel level, String message, long lineNumber,
			long columnNumber, String sourceId) {
		logger.info("View message: [" + source.name() + "/" + level.name() + "] " + sourceId + ":" + lineNumber + ":" + columnNumber + ": " + message);
	}

	@Override
	public UltralightView onCreateChildView(String openerUrl, String targetUrl, boolean isPopup, IntRect popupRect) {
		return null;
	}
}

package com.ramon.ultralight.js;

import java.util.function.Consumer;

import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.databind.context.ContextProvider;
import com.labymedia.ultralight.databind.context.ContextProviderFactory;
import com.labymedia.ultralight.javascript.JavascriptContextLock;
import com.labymedia.ultralight.javascript.JavascriptValue;

public class ViewContextProvider implements ContextProvider  {
	private final UltralightView view;

	private ViewContextProvider(UltralightView view) {
		this.view = view;
	}

	@Override
	public void syncWithJavascript(Consumer<JavascriptContextLock> callback) {
		try (JavascriptContextLock lock = view.lockJavascriptContext()) {
			callback.accept(lock);
		}
	}

	public static class Factory implements ContextProviderFactory {
		private final UltralightView view;

		public Factory(UltralightView view) {
			this.view = view;
		}

		@Override
		public ContextProvider bindProvider(JavascriptValue value) {
			return new ViewContextProvider(view);
		}
	}
}

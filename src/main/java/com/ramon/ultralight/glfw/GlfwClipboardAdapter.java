package com.ramon.ultralight.glfw;

import com.labymedia.ultralight.plugin.clipboard.UltralightClipboard;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwClipboardAdapter implements UltralightClipboard {
	@Override
	public void clear() {
		glfwSetClipboardString(0, "");
	}

	@Override
	public String readPlainText() {
		return glfwGetClipboardString(0);
	}

	@Override
	public void writePlainText(String text) {
		glfwSetClipboardString(0, text);
	}
}

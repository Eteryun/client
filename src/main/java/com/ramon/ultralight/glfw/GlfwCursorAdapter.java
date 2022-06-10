package com.ramon.ultralight.glfw;

import static org.lwjgl.glfw.GLFW.*;

import com.labymedia.ultralight.input.UltralightCursor;
import com.ramon.ultralight.UltralightEngine;

public class GlfwCursorAdapter {
	private final long iBeamCursor = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
	private final long crosshairCursor = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
	private final long handCursor = glfwCreateStandardCursor(GLFW_HAND_CURSOR);;
	private final long hresizeCursor = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
	private final long vresizeCursor = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);

	public void notifyCursorUpdated(UltralightCursor cursor) {
		switch (cursor) {
		case CROSS:
			glfwSetCursor(UltralightEngine.getWindow(), crosshairCursor);
			break;

		case HAND:
			glfwSetCursor(UltralightEngine.getWindow(), handCursor);
			break;

		case I_BEAM:
			glfwSetCursor(UltralightEngine.getWindow(), iBeamCursor);
			break;

		case EAST_WEST_RESIZE:
			glfwSetCursor(UltralightEngine.getWindow(), hresizeCursor);
			break;

		case NORTH_SOUTH_RESIZE:
			glfwSetCursor(UltralightEngine.getWindow(), vresizeCursor);
			break;

		default:
			glfwSetCursor(UltralightEngine.getWindow(), 0);
			break;
		}
	}

	public void cleanup() {
		glfwDestroyCursor(vresizeCursor);
		glfwDestroyCursor(hresizeCursor);
		glfwDestroyCursor(handCursor);
		glfwDestroyCursor(crosshairCursor);
		glfwDestroyCursor(iBeamCursor);
	}
}

package com.ramon.ultralight;

import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent.ElementType;
import com.eteryun.event.impl.input.CharInputEvent;
import com.eteryun.event.impl.input.KeyInputEvent;
import com.eteryun.event.impl.input.MouseCursorEvent;
import com.eteryun.event.impl.input.MouseInputEvent;
import com.eteryun.event.impl.input.MouseScrollEvent;
import com.eteryun.event.impl.screen.DrawScreenEvent;
import com.eteryun.event.impl.tick.RenderTickEvent;
import com.eteryun.event.impl.tick.TickEvent.Phase;
import com.eteryun.event.impl.window.WindowFocusEvent;
import com.eteryun.event.impl.window.WindowResizeEvent;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import com.ramon.ultralight.UltralightEngine.RenderLayer;

public class UltralightEvents {
	@EventTarget
	public void onDrawScreen(DrawScreenEvent.Post event) {
		UltralightEngine.getInstance().render(RenderLayer.SCREEN_LAYER, event.getMatrixStack());
	}

	@EventTarget
	public void onOverlayRender(RenderGameOverlayEvent.Post event) {
		if (event.getType().equals(ElementType.ALL)) {
			UltralightEngine.getInstance().render(RenderLayer.OVERLAY_LAYER, event.getMatrixStack());
		}
	}

	@EventTarget
	public void onTickRender(RenderTickEvent event) {
		if (event.phase.equals(Phase.END))
			return;

		UltralightEngine.getInstance().update();
	}

	@EventTarget
	public void onWindowResize(WindowResizeEvent event) {
		UltralightEngine.getInstance().resize(event.getWidth(), event.getHeight());
	}

	@EventTarget
	public void onWindowFocus(WindowFocusEvent event) {
		UltralightEngine.getInstance().inputAdapter.focusCallback(event.getWindow(), event.isFocused());
	}

	@EventTarget
	public void onMouseButton(MouseInputEvent event) {
		if (event.getAction() == 1) {
			InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(event.getButton());
			onKeyDown(key, false);
		}
		UltralightEngine.getInstance().inputAdapter.mouseButtonCallback(event.getWindow(), event.getButton(),
				event.getAction(), event.getMods());
	}

	@EventTarget
	public void onMouseScroll(MouseScrollEvent event) {
		UltralightEngine.getInstance().inputAdapter.scrollCallback(event.getWindow(), event.getXDelta(),
				event.getYDelta());
	}

	@EventTarget
	public void onMouseCursor(MouseCursorEvent event) {
		UltralightEngine.getInstance().inputAdapter.cursorPosCallback(event.getWindow(), event.getMouseX(),
				event.getMouseY());
	}

	@EventTarget
	public void onKeyEvent(KeyInputEvent event) {
		if (event.getAction() == 1) {
			InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(event.getKey());
			onKeyDown(key, false);
		}
		UltralightEngine.getInstance().inputAdapter.keyCallback(event.getWindow(), event.getKey(), event.getScanCode(),
				event.getAction(), event.getModifiers());
	}

	@EventTarget
	public void onCharEvent(CharInputEvent event) {
		UltralightEngine.getInstance().inputAdapter.charCallback(event.getWindow(), event.getCodePoint());
	}

	public void onKeyDown(InputConstants.Key key, boolean isMouse) {
		JsonObject json = new JsonObject();
		json.addProperty("code", key.getValue());
		json.addProperty("scanCode", -1);
		json.addProperty("translateKey", key.getName());
		json.addProperty("isMouse", isMouse);
		UltralightEngine.getInstance().sendMessageAll("onKeyDown", json);
	}
}

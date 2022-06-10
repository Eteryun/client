package com.ramon.ultralight.renderer;

import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.UltralightViewConfig;
import com.mojang.blaze3d.vertex.PoseStack;

public interface ViewRenderer {
	public void setupConfig(UltralightViewConfig config);

	public void render(UltralightView view, PoseStack matrices);

	public void delete();
}

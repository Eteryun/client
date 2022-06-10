package com.ramon.ultralight.renderer;

import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.bitmap.UltralightBitmap;
import com.labymedia.ultralight.bitmap.UltralightBitmapSurface;
import com.labymedia.ultralight.config.UltralightViewConfig;
import com.labymedia.ultralight.math.IntRect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

import static org.lwjgl.opengl.GL12.*;

import java.nio.ByteBuffer;

public class CpuViewRenderer implements ViewRenderer {
	private int glTexture = -1;

	@Override
	public void setupConfig(UltralightViewConfig config) {}

	@Override
	public void render(UltralightView view, PoseStack matrices) {
		if (glTexture == -1) {
			createGlTexture();
		}

		Minecraft mc = Minecraft.getInstance();

		// As we are using the CPU renderer, draw with a bitmap (we did not set a custom
		// surface)
		UltralightBitmapSurface surface = (UltralightBitmapSurface) view.surface();
		UltralightBitmap bitmap = surface.bitmap();
		int width = (int) view.width();
		int height = (int) view.height();

		// Prepare OpenGL for 2D textures and bind our texture
		RenderSystem.enableTexture();
		RenderSystem.bindTexture(glTexture);

		IntRect dirtyBounds = surface.dirtyBounds();

		if (dirtyBounds.isValid()) {
			ByteBuffer imageData = bitmap.lockPixels();

			glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
			glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
			glPixelStorei(GL_UNPACK_SKIP_IMAGES, 0);
			glPixelStorei(GL_UNPACK_ROW_LENGTH, (int) bitmap.rowBytes() / 4);

			if (dirtyBounds.width() == width && dirtyBounds.height() == height) {
				// Update full image
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV,
						imageData);
			} else {
				// Update partial image
				int x = dirtyBounds.x();
				int y = dirtyBounds.y();
				int dirtyWidth = dirtyBounds.width();
				int dirtyHeight = dirtyBounds.height();
				int startOffset = (int) (y * bitmap.rowBytes() + x * 4);

				glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, dirtyWidth, dirtyHeight, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV,
						imageData.position(startOffset));
			}
			glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);

			bitmap.unlockPixels();
			surface.clearDirtyBounds();
		}


		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		float scaleFactor = (float) mc.getWindow().getGuiScale();

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, glTexture);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.enableBlend();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

		bufferBuilder.vertex(0.0, height, 0.0).uv(0f, scaleFactor).color(255, 255, 255, 255).endVertex();
		bufferBuilder.vertex(width, height, 0.0).uv(scaleFactor, scaleFactor)
				.color(255, 255, 255, 255).endVertex();
		bufferBuilder.vertex(width, 0.0, 0.0).uv(scaleFactor, 0.0f).color(255, 255, 255, 255).endVertex();

		bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();

		tesselator.end();
		RenderSystem.disableBlend();
	}

	@Override
	public void delete() {
		glDeleteTextures(glTexture);
		glTexture = -1;
	}

	private void createGlTexture() {
		glTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, glTexture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}

package org.cef.browser.lwjgl;

import com.eteryun.modules.cef.CefManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.cef.browser.ICefRenderer;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class CefRendererLwjgl implements ICefRenderer {
    private final boolean transparent_;
    public int texture_id_ = 0;
    private int view_width_ = 0;
    private int view_height_ = 0;
    private Rectangle popup_rect_ = new Rectangle(0, 0, 0, 0);
    private Rectangle original_popup_rect_ = new Rectangle(0, 0, 0, 0);

    public CefRendererLwjgl(boolean transparent) {
        transparent_ = transparent;
        initialize();
    }

    protected void initialize() {
        GlStateManager._enableTexture();
        texture_id_ = glGenTextures();

        GlStateManager._bindTexture(texture_id_);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GlStateManager._bindTexture(0);
    }

    @Override
    public void destroy() {
        if(texture_id_ != 0) {
            glDeleteTextures(texture_id_);
            texture_id_ = 0;
        }
    }

    @Override
    protected void finalize() {
        destroy(); // NO MEMORY LEAKS!
    }

    @Override
    public void render(double x1, double y1, double x2, double y2) {
        if(view_width_ == 0 || view_height_ == 0)
            return;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        GlStateManager._bindTexture(texture_id_);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture_id_);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(x1, y2, 0.0).uv(0f, 1.f).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(x2, y2, 0.0).uv(1.f, 1.f).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(x2, y1, 0.0).uv(1.f, 0.0f).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(x1, y1, 0.0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        tesselator.end();
        GlStateManager._bindTexture(0);
    }

    @Override
    public void onPopupSize(Rectangle rect) {
        if(rect.width <= 0 || rect.height <= 0)
            return;
        original_popup_rect_ = rect;
        popup_rect_ = getPopupRectInWebView(original_popup_rect_);
    }

    protected Rectangle getPopupRectInWebView(Rectangle rc) {
        // if x or y are negative, move them to 0.
        if(rc.x < 0)
            rc.x = 0;
        if(rc.y < 0)
            rc.y = 0;
        // if popup goes outside the view, try to reposition origin
        if(rc.x + rc.width > view_width_)
            rc.x = view_width_ - rc.width;
        if(rc.y + rc.height > view_height_)
            rc.y = view_height_ - rc.height;
        // if x or y became negative, move them to 0 again.
        if(rc.x < 0)
            rc.x = 0;
        if(rc.y < 0)
            rc.y = 0;
        return rc;
    }

    @Override
    public void onPopupClosed() {
        popup_rect_.setBounds(0, 0, 0, 0);
        original_popup_rect_.setBounds(0, 0, 0, 0);
    }

    @Override
    public void onPaint(boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height, boolean completeReRender) {
        if(transparent_) // Enable alpha blending.
            GlStateManager._enableBlend();

        final int size = (width * height) << 2;
        if(size > buffer.limit()) {
            CefManager.LOGGER.warn("Bad data passed to CefRenderer.onPaint() triggered safe guards... (1)");
            return;
        }

        // Enable 2D textures.
        GlStateManager._enableTexture();
        GlStateManager._bindTexture(texture_id_);

        int oldAlignement = glGetInteger(GL_UNPACK_ALIGNMENT);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        if(!popup) {
            if(completeReRender || width != view_width_ || height != view_height_) {
                // Update/resize the whole texture.
                view_width_ = width;
                view_height_ = height;
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, view_width_, view_height_, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            } else {
                glPixelStorei(GL_UNPACK_ROW_LENGTH, view_width_);

                // Update just the dirty rectangles.
                for(Rectangle rect: dirtyRects) {
                    if(rect.x < 0 || rect.y < 0 || rect.x + rect.width > view_width_ || rect.y + rect.height > view_height_)
                        CefManager.LOGGER.warn("Bad data passed to CefRenderer.onPaint() triggered safe guards... (2)");
                    else {
                        glPixelStorei(GL_UNPACK_SKIP_PIXELS, rect.x);
                        glPixelStorei(GL_UNPACK_SKIP_ROWS, rect.y);
                        glTexSubImage2D(GL_TEXTURE_2D, 0, rect.x, rect.y, rect.width, rect.height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
                    }
                }

                glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
                glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            }
        } else if(popup_rect_.width > 0 && popup_rect_.height > 0) {
            int skip_pixels = 0, x = popup_rect_.x;
            int skip_rows = 0, y = popup_rect_.y;
            int w = width;
            int h = height;

            // Adjust the popup to fit inside the view.
            if(x < 0) {
                skip_pixels = -x;
                x = 0;
            }
            if(y < 0) {
                skip_rows = -y;
                y = 0;
            }
            if(x + w > view_width_)
                w -= x + w - view_width_;
            if(y + h > view_height_)
                h -= y + h - view_height_;

            // Update the popup rectangle.
            glPixelStorei(GL_UNPACK_ROW_LENGTH, width);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, skip_pixels);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, skip_rows);
            glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_BGRA, GL_UNSIGNED_BYTE, buffer);
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
        }

        glPixelStorei(GL_UNPACK_ALIGNMENT, oldAlignement);
        GlStateManager._bindTexture(0);
    }
}

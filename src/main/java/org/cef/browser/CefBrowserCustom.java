package org.cef.browser;

import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.UnsafeExample;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.cef.CefClient;
import org.cef.browser.lwjgl.CefRendererLwjgl;
import org.cef.callback.CefDragData;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.glfw.GLFW.*;

public class CefBrowserCustom extends CefBrowser_N implements CefRenderHandler {
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final ICefRenderer renderer_;
    private boolean justCreated_ = false;
    private final Rectangle browser_rect_ = new Rectangle(0, 0, 1, 1);
    private final Point screenPoint_ = new Point(0, 0);
    private final boolean isTransparent_;
    private final Component dc_ = new Component() {
    };

    public CefBrowserCustom(CefClient client, String url, boolean transparent, CefRequestContext context) {
        this(client, url, transparent, context, null, null);
    }

    public CefBrowserCustom(CefClient client, String url, boolean isTransparent_, CefRequestContext context, CefBrowserCustom parent, Point inspectAt) {
        super(client, url, context, parent, inspectAt);
        this.isTransparent_ = isTransparent_;
        renderer_ = new CefRendererLwjgl(true);;
        CefManager.browserList.add(this);
    }

    @Override
    protected CefBrowser_N createDevToolsBrowser(CefClient client, String url, CefRequestContext context, CefBrowser_N parent, Point inspectAt) {
        return new CefBrowserCustom(client, url, this.isTransparent_, context, this, inspectAt);
    }

    @Override
    public void createImmediately() {
        this.justCreated_ = true;
        this.createBrowserIfRequired(false);
    }

    @Override
    public Component getUIComponent() {
        return this.dc_;
    }

    @Override
    public CefRenderHandler getRenderHandler() {
        return this;
    }

    private synchronized long getWindowHandle() {
        return 0L;
    }

    @Override
    public Rectangle getViewRect(CefBrowser browser) {
        return this.browser_rect_;
    }

    @Override
    public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
        Point screenPoint = new Point(this.screenPoint_);
        screenPoint.translate(viewPoint.x, viewPoint.y);
        return screenPoint;
    }

    @Override
    public void onPopupShow(CefBrowser browser, boolean show) {
        if (!show) {
            this.renderer_.onPopupClosed();
            this.invalidate();
        }
    }

    @Override
    public void onPopupSize(CefBrowser browser, Rectangle size) {
        this.renderer_.onPopupSize(size);
    }

    private static class PaintData {
        private ByteBuffer buffer;
        private int width;
        private int height;
        private Rectangle[] dirtyRects;
        private boolean hasFrame;
        private boolean fullReRender;
    }

    private final PaintData paintData = new PaintData();

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects,
                        ByteBuffer buffer, int width, int height) {
        if (popup)
            return;

        final int size = (width * height) << 2;

        synchronized (paintData) {
            if (buffer.limit() > size)
                CefManager.LOGGER.warn("Skipping MCEF browser frame, data is too heavy"); //TODO: Don't spam
            else {
                if (paintData.hasFrame) //The previous frame was not uploaded to GL texture, so we skip it and render this on instead
                    paintData.fullReRender = true;

                if (paintData.buffer == null || size != paintData.buffer.capacity()) //This only happens when the browser gets resized
                    paintData.buffer = BufferUtils.createByteBuffer(size);

                paintData.buffer.position(0);
                paintData.buffer.limit(buffer.limit());
                buffer.position(0);
                paintData.buffer.put(buffer);
                paintData.buffer.position(0);

                paintData.width = width;
                paintData.height = height;
                paintData.dirtyRects = dirtyRects;
                paintData.hasFrame = true;
            }
        }
    }

    public void update() {
        synchronized (paintData) {
            if (paintData.hasFrame) {
                renderer_.onPaint(false, paintData.dirtyRects, paintData.buffer, paintData.width, paintData.height, paintData.fullReRender);
                paintData.hasFrame = false;
                paintData.fullReRender = false;
            }
        }
    }

    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        return true;
    }

    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        System.out.println("startDragging: " + dragData + " | X: " + x + " Y: " + y);
        return false;
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {
        System.out.println("updateDragCursor: " + operation);
    }

    private void createBrowserIfRequired(boolean hasParent) {
        long windowHandle = 0L;
        if (hasParent) {
            windowHandle = this.getWindowHandle();
        }
        if (this.getNativeRef("CefBrowser") == 0L) {
            if (this.getParentBrowser() != null) {
                this.createDevTools(this.getParentBrowser(), this.getClient(), windowHandle, true, this.isTransparent_, null, this.getInspectAt());
            } else {
                this.createBrowser(this.getClient(), windowHandle, this.getUrl(), true, this.isTransparent_, null, this.getRequestContext());
            }
        } else if (hasParent && this.justCreated_) {
            this.notifyAfterParentChanged();
            this.setFocus(true);
            this.justCreated_ = false;
        }
    }

    private void notifyAfterParentChanged() {
        this.getClient().onAfterParentChanged(this);
    }

    @Override
    public boolean getScreenInfo(CefBrowser browser, CefScreenInfo screenInfo) {
        int depth_per_component = 8;
        int depth = 32;
        double scaleFactor_ = 1.0;
        screenInfo.Set(scaleFactor_, depth, depth_per_component, false, this.browser_rect_.getBounds(), this.browser_rect_.getBounds());
        return true;
    }

    @Override
    public CompletableFuture<BufferedImage> createScreenshot(boolean nativeResolution) {
        return null;
    }

    @Override
    public void close(boolean force) {
        CefManager.browserList.remove(this);
        this.renderer_.destroy();
        super.close(force);
    }

    public void wasResized_(int width, int height) {
        this.browser_rect_.setBounds(0, 0, width, height);
        this.dc_.setBounds(this.browser_rect_);
        this.dc_.setVisible(true);
        super.wasResized(width, height);
    }

    public void draw(double x1, double y1, double x2, double y2) {
        renderer_.render(x1, y1, x2, y2);
    }

    public void mouseMoved(int x, int y, int mods) {
        MouseEvent ev = new MouseEvent(dc_, MouseEvent.MOUSE_MOVED, 0, mods, x, y, 0, false);
        sendMouseEvent(ev);
    }

    public void mouseDragged(int x, int y, int mods, int btn) {
        MouseEvent ev = new MouseEvent(dc_, MouseEvent.MOUSE_DRAGGED, 0, mods, x, y, 0, false, btn);
        sendMouseEvent(ev);
    }

    public void mouseInteracted(int x, int y, int mods, int btn, boolean pressed, int ccnt) {
        MouseEvent ev = new MouseEvent(dc_, pressed ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED, 0, mods, x, y, ccnt, false, remapMouseCode(btn));
        sendMouseEvent(ev);
    }

    private static int remapMouseCode(int kc) {
        switch (kc) {
            case 0:
                return 1;
            case 1:
                return 3;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    public void mouseScrolled(int x, int y, int mods, int amount, int rot) {
        MouseWheelEvent ev = new MouseWheelEvent(dc_, MouseEvent.MOUSE_WHEEL, 0, mods, x, y, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, amount, rot);
        sendMouseWheelEvent(ev);
    }

    public void keyTyped(int key, int mods) {
        KeyEvent ev = new UnsafeExample().makeEvent(dc_, key, (char) remapKeycode(key, (char) key), KeyEvent.KEY_LOCATION_UNKNOWN, KeyEvent.KEY_TYPED, 0, mods);
        sendKeyEvent(ev);
    }

    /**
     * fill the gap between LWJGL and AWT key codes
     * https://stackoverflow.com/questions/15313469/java-keyboard-keycodes-list/31637206
     */
    public static int remapKeycode(int kc, char c) {
        switch (kc) {
            case GLFW_KEY_CAPS_LOCK:
                return KeyEvent.VK_CAPS_LOCK;
            case GLFW_KEY_BACKSPACE:
                return KeyEvent.VK_BACK_SPACE;
            case GLFW_KEY_DELETE:
                return KeyEvent.VK_DELETE;
            case GLFW_KEY_DOWN:
                return KeyEvent.VK_DOWN;
            case GLFW_KEY_ENTER:
                return 13;
            case GLFW_KEY_ESCAPE:
                return KeyEvent.VK_ESCAPE;
            case GLFW_KEY_LEFT:
                return KeyEvent.VK_LEFT;
            case GLFW_KEY_RIGHT:
                return KeyEvent.VK_RIGHT;
            case GLFW_KEY_TAB:
                return KeyEvent.VK_TAB;
            case GLFW_KEY_UP:
                return KeyEvent.VK_UP;
            case GLFW_KEY_PAGE_UP:
                return KeyEvent.VK_PAGE_UP;
            case GLFW_KEY_PAGE_DOWN:
                return KeyEvent.VK_PAGE_DOWN;
            case GLFW_KEY_END:
                return GLFW_KEY_END;
            case GLFW_KEY_HOME:
                return GLFW_KEY_HOME;
            default:
                return c;
        }
    }

    private static final Map<Integer, Character> WORST_HACK = new HashMap<>();

    public void keyEventByKeyCode(int keyCode, char c, int mods, boolean pressed) {
        if (c != '\0' && pressed) {
            synchronized (WORST_HACK) {
                WORST_HACK.put(keyCode, c);
            }
        }
        if (c == '\0' && !pressed) {
            synchronized (WORST_HACK) {
                c = WORST_HACK.getOrDefault(keyCode, '\0');
            }
        }
        // we already processed the char in GuiView, so we don't need to do it again like MCEF does
        KeyEvent ev = new KeyEvent(dc_, pressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED, 0, mods, remapKeycode(keyCode, c), c);
        sendKeyEvent(ev);
    }

    @Override
    protected void finalize() throws Throwable {
        if (!isClosed()) {
            close(true); // NO FUCKING MEMORY LEAKS
        }
        super.finalize();
    }

    public void sendMessage(String type, Object obj) {
        executeJavaScript("window.postMessage({ detail: { type: '" + type + "', content: " + obj + " } }, '*');", "", 0);
    }

    public static String createDefaultDto(String value) {
        JsonObject object = new JsonObject();
        object.addProperty("value", value);
        return GSON.toJson(object);
    }
}

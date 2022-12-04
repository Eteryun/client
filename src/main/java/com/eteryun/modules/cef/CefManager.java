package com.eteryun.modules.cef;

import com.eteryun.modules.IModule;
import com.eteryun.modules.Module;
import com.eteryun.modules.cef.extension.CefAppAccess;
import com.mojang.logging.LogUtils;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import me.friwi.jcefmaven.impl.step.init.CefInitializer;
import net.minecraft.client.Minecraft;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserCustom;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static me.friwi.jcefmaven.EnumPlatform.PROPERTY_OS_ARCH;
import static me.friwi.jcefmaven.EnumPlatform.PROPERTY_OS_NAME;

@Module
public class CefManager implements IModule {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static CefApp cefApp;
    public static CefClient cefClient;

    private static File dataDir = new File("cef");
    private static File cacheDir = new File("cef_cache");

    public static List<CefBrowserCustom> browserList = new ArrayList<>();

    private static boolean initialized = false;

    @Override
    public void preInit() {

    }

    public void init() {
        if (initialized) return;
        CefSettings cefSettings = new CefSettings();
        cefSettings.windowless_rendering_enabled = true;
        cefSettings.locale = Minecraft.getInstance().getLanguageManager().getSelected().getName();
        cefSettings.cache_path = cacheDir.getAbsolutePath();

        CefApp.addAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0);
            }
        });

        try {
            cefApp = CefInitializer.initialize(dataDir, new LinkedList<>(), cefSettings); // builder.build();
            cefClient = cefApp.createClient();

            CefApp.CefVersion version = cefApp.getVersion();
            LOGGER.info("Cef Loaded (jcefVersion=" + version.getJcefVersion() + ", cefVersion=" + version.getCefVersion() + ", chromeVersion=" + version.getChromeVersion() + ")");
        } catch (UnsupportedPlatformException e) {
            LOGGER.error(System.getProperty(PROPERTY_OS_NAME) + "(" + System.getProperty(PROPERTY_OS_ARCH) + ") is not supported by jcef-maven");
            e.printStackTrace();
        } catch (CefInitializationException e) {
            LOGGER.error("Chromium embedded framework failed:");
            e.printStackTrace();
        } finally {
            initialized = true;
        }
    }

    public void shutdown() {
        browserList.forEach((browser) -> browser.close(true));
        cefApp.dispose();
        cefClient.dispose();
    }

    public static void update() {
        ((CefAppAccess) cefApp).doLoopWork();

        browserList.forEach(CefBrowserCustom::update);
    }
}

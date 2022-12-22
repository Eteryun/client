package com.eteryun.modules.cef;

import com.eteryun.modules.IModule;
import com.eteryun.modules.cef.extension.CefAppAccess;
import com.eteryun.utils.FileUtils;
import com.mojang.logging.LogUtils;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.EnumPlatform;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.step.init.CefInitializer;
import net.minecraft.client.Minecraft;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowserCustom;
import org.cef.browser.CefMessageRouter;
import org.slf4j.Logger;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static me.friwi.jcefmaven.EnumPlatform.PROPERTY_OS_ARCH;
import static me.friwi.jcefmaven.EnumPlatform.PROPERTY_OS_NAME;

public class CefManager implements IModule {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static CefApp cefApp;
    public static CefClient cefClient;
    public static CefMessageRouter cefRouter;
    public static MessageRouter messageRouter;

    private static File dataDir = new File("cef");
    private static File cacheDir = new File("cef_cache");

    public static List<CefBrowserCustom> browserList = new ArrayList<>();

    private static boolean initialized = false;

    private static CefManager instance;

    public CefManager() {
        instance = this;
    }

    @Override
    public void preInit() {

    }

    public void init() {
        if (initialized) return;
//        CefAppBuilder builder = new CefAppBuilder();
//        builder.setInstallDir(dataDir);
//        CefSettings cefSettings = builder.getCefSettings();
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
            extract();
            cefApp = CefInitializer.initialize(dataDir, new LinkedList<>(), cefSettings);
            cefClient = cefApp.createClient();

            messageRouter = new MessageRouter();
            cefRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("eteryunQuery", "eteryunQueryCancel"));
            cefClient.addMessageRouter(cefRouter);
            cefRouter.addHandler(messageRouter, false);
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
        cefClient.dispose();
//        cefApp.dispose();
    }

    public static void update() {
        ((CefAppAccess) cefApp).doLoopWork();
        browserList.forEach(CefBrowserCustom::update);
    }

    public void registerQueryHandler(Object o) {
        messageRouter.register(o);
    }

    public void unregisterQueryHandler(Object o) {
//        messageRouter.unregister(o);
    }

    public static CefManager getInstance() {
        return instance;
    }

    private void extract() {
        try {
            EnumPlatform platform = EnumPlatform.getCurrentPlatform();
            File zip = new File("cef-" + platform.getIdentifier() + ".zip");
            String sha1 = FileUtils.sha1Hash(zip.getAbsolutePath());

            File lock = new File(dataDir, "install.lock");
            if (!FileUtils.readFile(lock).equalsIgnoreCase(sha1)) {
                CefExtractor.extract(zip, dataDir);

                FileWriter versionWrite = new FileWriter(lock, false);
                versionWrite.write(sha1);
                versionWrite.close();
            }
        } catch (UnsupportedPlatformException | IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

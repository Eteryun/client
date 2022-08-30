package com.eteryun.screens;

import com.mojang.logging.LogUtils;
import com.ramon.ultralight.UltralightEngine;
import com.ramon.ultralight.UltralightResources;
import com.ramon.ultralight.View;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectingScreen extends Screen {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private View.ScreenView view;
    volatile boolean aborted;
    final Screen parent;
    volatile Connection connection;

    protected ConnectingScreen(Screen screen) {
        super(NarratorChatListener.NO_TITLE);
        this.view = UltralightEngine.getInstance().newScreenView("connecting", this, this, this);
        this.parent = screen;
    }

    @Override
    protected void init() {
        super.init();
        view.loadUrl(UltralightResources.getNUI("screens", "index.html", "connecting"));
    }

    public static void startConnecting(Screen screen, Minecraft minecraft, ServerAddress serverAddress, @Nullable ServerData serverData) {
        ConnectingScreen connectScreen = new ConnectingScreen(screen);
        minecraft.clearLevel();
        minecraft.prepareForMultiplayer();
        minecraft.setCurrentServer(serverData);
        minecraft.setScreen(connectScreen);
        connectScreen.connect(minecraft, serverAddress);
    }

    private void connect(final Minecraft minecraft, final ServerAddress serverAddress) {
        LOGGER.info("Connecting to {}, {}", serverAddress.getHost(), serverAddress.getPort());
        Thread thread = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                InetSocketAddress inetSocketAddress = null;

                try {
                    if (ConnectingScreen.this.aborted) {
                        return;
                    }

                    Optional<InetSocketAddress> optional = ServerNameResolver.DEFAULT.resolveAddress(serverAddress).map(ResolvedServerAddress::asInetSocketAddress);
                    if (ConnectingScreen.this.aborted) {
                        return;
                    }

                    if (!optional.isPresent()) {
                        minecraft.execute(() -> {
                            minecraft.setScreen(new DisconnectedScreen(ConnectingScreen.this.parent, CommonComponents.CONNECT_FAILED, ConnectScreen.UNKNOWN_HOST_MESSAGE));
                        });
                        return;
                    }

                    inetSocketAddress = (InetSocketAddress)optional.get();
                    ConnectingScreen.this.connection = Connection.connectToServer(inetSocketAddress, minecraft.options.useNativeTransport());
                    ConnectingScreen.this.connection.setListener(new ClientHandshakePacketListenerImpl(ConnectingScreen.this.connection, minecraft, ConnectingScreen.this.parent, ConnectingScreen.this::updateStatus));
                    ConnectingScreen.this.connection.send(new ClientIntentionPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), ConnectionProtocol.LOGIN));
                    ConnectingScreen.this.connection.send(new ServerboundHelloPacket(minecraft.getUser().getGameProfile()));
                } catch (Exception var6) {
                    if (ConnectingScreen.this.aborted) {
                        return;
                    }

                    Throwable var5 = var6.getCause();
                    Exception exception3;
                    if (var5 instanceof Exception exception2) {
                        exception3 = exception2;
                    } else {
                        exception3 = var6;
                    }

                    ConnectingScreen.LOGGER.error("Couldn't connect to server", var6);
                    String string = inetSocketAddress == null ? exception3.getMessage() : exception3.getMessage().replaceAll(inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort(), "").replaceAll(inetSocketAddress.toString(), "");
                    minecraft.execute(() -> {
                        minecraft.setScreen(new DisconnectedScreen(ConnectingScreen.this.parent, CommonComponents.CONNECT_FAILED, new TranslatableComponent("disconnect.genericReason", new Object[]{string})));
                    });
                }

            }
        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    private void updateStatus(Component component) {
        if (component instanceof  TranslatableComponent) {
            view.sendViewMessage("setStatus", ((TranslatableComponent)component).getKey());
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null) {
            this.connection.disconnect(new TranslatableComponent("connect.aborted"));
        }

        this.minecraft.setScreen(this.parent);
    }
}

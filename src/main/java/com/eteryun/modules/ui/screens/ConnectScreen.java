package com.eteryun.modules.ui.screens;

import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.modules.cef.screen.CefScreen;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
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

public class ConnectScreen extends CefScreen {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Component UNKNOWN_HOST_MESSAGE = new TranslatableComponent("disconnect.genericReason", new Object[]{new TranslatableComponent("disconnect.unknownHost")});
    @Nullable
    volatile Connection connection;
    volatile boolean aborted;
    private final Screen lastScreen;

    public ConnectScreen(Screen lastScreen) {
        super("http://ui.eteryun.com.br/screens/#/connecting");
        this.lastScreen = lastScreen;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static void connecting(Screen screen, Minecraft minecraft, ServerAddress serverAddress, @Nullable ServerData serverData) {
        ConnectScreen connectScreen = new ConnectScreen(screen);
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
                    if (ConnectScreen.this.aborted) {
                        return;
                    }

                    Optional<InetSocketAddress> optional = ServerNameResolver.DEFAULT.resolveAddress(serverAddress).map(ResolvedServerAddress::asInetSocketAddress);
                    if (ConnectScreen.this.aborted) {
                        return;
                    }

                    if (!optional.isPresent()) {
                        minecraft.execute(() -> {
                            minecraft.setScreen(new DisconnectedScreen(ConnectScreen.this.lastScreen, CommonComponents.CONNECT_FAILED, ConnectScreen.UNKNOWN_HOST_MESSAGE));
                        });
                        return;
                    }

                    inetSocketAddress = optional.get();
                    ConnectScreen.this.connection = Connection.connectToServer(inetSocketAddress, minecraft.options.useNativeTransport());
                    ConnectScreen.this.connection.setListener(new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, minecraft, ConnectScreen.this.lastScreen, ConnectScreen.this::updateStatus));
                    ConnectScreen.this.connection.send(new ClientIntentionPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), ConnectionProtocol.LOGIN));
                    ConnectScreen.this.connection.send(new ServerboundHelloPacket(minecraft.getUser().getGameProfile()));
                } catch (Exception var6) {
                    if (ConnectScreen.this.aborted) {
                        return;
                    }

                    Throwable var5 = var6.getCause();
                    Exception exception3;
                    if (var5 instanceof Exception exception2) {
                        exception3 = exception2;
                    } else {
                        exception3 = var6;
                    }

                    ConnectScreen.LOGGER.error("Couldn't connect to server", var6);
                    String string = inetSocketAddress == null ? exception3.getMessage() : exception3.getMessage().replaceAll(inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort(), "").replaceAll(inetSocketAddress.toString(), "");
                    minecraft.execute(() -> {
                        minecraft.setScreen(new DisconnectedScreen(ConnectScreen.this.lastScreen, CommonComponents.CONNECT_FAILED, new TranslatableComponent("disconnect.genericReason", new Object[]{string})));
                    });
                }

            }
        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    private void updateStatus(Component component) {
        cefBrowser.sendMessage("setStatus", (component instanceof TranslatableComponent && ((TranslatableComponent) component).getArgs().length == 0) ? ((TranslatableComponent) component).getKey() : component.getString());
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    @QueryTarget(name = "connect/abort")
    public void abort(JsonObject object) {
        this.aborted = true;
        if (this.connection != null) {
            this.connection.disconnect(new TranslatableComponent("connect.aborted"));
        }

        this.minecraft.setScreen(this.lastScreen);
    }

}

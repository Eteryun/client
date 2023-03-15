package com.eteryun.modules.boss.network.client;

import com.eteryun.Eteryun;
import com.eteryun.modules.IModule;
import com.eteryun.modules.boss.BossInfo;
import com.eteryun.modules.ui.UiModule;
import com.eteryun.network.Packet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.function.Function;

public class ClientboundPacketBoss implements Packet {
    private Operation operation;

    private ClientboundPacketBoss(Operation operation) {
        this.operation = operation;
    }

    public ClientboundPacketBoss(FriendlyByteBuf buffer) {
        OperationType operationType = buffer.readEnum(OperationType.class);
        this.operation = operationType.reader.apply(buffer);
    }

    public static ClientboundPacketBoss createAddPacket(BossInfo bossInfo) {
        return new ClientboundPacketBoss(new AddOperation(bossInfo));
    }

    public static ClientboundPacketBoss createRemovePacket() {
        return new ClientboundPacketBoss(REMOVE_OPERATION);
    }

    public static ClientboundPacketBoss createUpdateTitle(String title) {
        return new ClientboundPacketBoss(new TitleOperation(title));
    }

    public static ClientboundPacketBoss createUpdateStyle(String color, String image) {
        return new ClientboundPacketBoss(new StyleOperation(color, image));
    }

    public static ClientboundPacketBoss createUpdateHealth(double health, double maxHealth) {
        return new ClientboundPacketBoss(new HealthOperation(health, maxHealth));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.operation.getType());
        this.operation.write(pBuffer);
    }

    @Override
    public void handle() {
        Optional<IModule> module = Eteryun.getModule("ui");
        if (module.isPresent()) {
            UiModule uiModule = (UiModule) module.get();
            BossInfo.update(this, uiModule);
        }
    }

    public void dispatch(Handler handler) {
        this.operation.dispatch(handler);
    }

    private interface Operation {
        OperationType getType();

        void dispatch(Handler handler);

        void write(FriendlyByteBuf byteBuf);
    }

    enum OperationType {
        ADD(AddOperation::new),
        REMOVE(byteBuf -> REMOVE_OPERATION),
        UPDATE_TITLE(TitleOperation::new),
        UPDATE_STYLE(StyleOperation::new),
        UPDATE_HEALTH(HealthOperation::new);

        final Function<FriendlyByteBuf, Operation> reader;

        private OperationType(Function<FriendlyByteBuf, Operation> function) {
            this.reader = function;
        }
    }

    private static class AddOperation implements Operation {
        private final String title;
        private final String color;
        private final String image;
        private final double health;
        private final double maxHealth;

        public AddOperation(BossInfo bossInfo) {
            this.title = bossInfo.getTitle();
            this.color = bossInfo.getColor();
            this.image = bossInfo.getImage();
            this.health = bossInfo.getHealth();
            this.maxHealth = bossInfo.getMaxHealth();
        }

        private AddOperation(FriendlyByteBuf pBuffer) {
            this.title = pBuffer.readUtf();
            this.color = pBuffer.readUtf();
            this.image = pBuffer.readUtf();
            this.health = pBuffer.readDouble();
            this.maxHealth = pBuffer.readDouble();
        }

        @Override
        public OperationType getType() {
            return OperationType.ADD;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.add(title, color, image, health, maxHealth);
        }

        @Override
        public void write(FriendlyByteBuf byteBuf) {
            byteBuf.writeUtf(title);
            byteBuf.writeUtf(color);
            byteBuf.writeUtf(image);
            byteBuf.writeDouble(health);
            byteBuf.writeDouble(maxHealth);
        }
    }

    static final Operation REMOVE_OPERATION = new Operation() {

        @Override
        public OperationType getType() {
            return OperationType.REMOVE;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.remove();
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) {
        }
    };

    private static class TitleOperation implements Operation {
        private final String title;

        public TitleOperation(String title) {
            this.title = title;
        }

        private TitleOperation(FriendlyByteBuf pBuffer) {
            this.title = pBuffer.readUtf();
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_TITLE;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.updateTitle(title);
        }

        @Override
        public void write(FriendlyByteBuf byteBuf) {
            byteBuf.writeUtf(title);
        }
    }

    private static class StyleOperation implements Operation {
        private final String color;
        private final String image;

        public StyleOperation(String color, String image) {
            this.color = color;
            this.image = image;
        }

        private StyleOperation(FriendlyByteBuf pBuffer) {
            this.color = pBuffer.readUtf();
            this.image = pBuffer.readUtf();
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_STYLE;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.updateStyle(color, image);
        }

        @Override
        public void write(FriendlyByteBuf byteBuf) {
            byteBuf.writeUtf(color);
            byteBuf.writeUtf(image);
        }
    }

    private static class HealthOperation implements Operation {
        private final double health;
        private final double maxHealth;

        public HealthOperation(double health, double maxHealth) {
            this.health = health;
            this.maxHealth = maxHealth;
        }

        private HealthOperation(FriendlyByteBuf pBuffer) {
            this.health = pBuffer.readDouble();
            this.maxHealth = pBuffer.readDouble();
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_HEALTH;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.updateHealth(health, maxHealth);
        }

        @Override
        public void write(FriendlyByteBuf byteBuf) {
            byteBuf.writeDouble(health);
            byteBuf.writeDouble(maxHealth);
        }
    }

    public interface Handler {
        default void add(String title, String color, String image, double health, double maxHealth) {
        }

        default void remove() {
        }

        default void updateTitle(String title) {
        }

        default void updateStyle(String color, String image) {
        }

        default void updateHealth(double health, double maxHealth) {
        }
    }
}

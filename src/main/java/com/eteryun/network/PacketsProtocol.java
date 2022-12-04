package com.eteryun.network;

import com.eteryun.Eteryun;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.eteryun.utils.Constants.ETERYUN_BRAND;

public class PacketsProtocol {
    private static final Map<PacketFlow, PacketSet> flows = new HashMap<>();

    public static <P extends Packet> void registerPacket(PacketFlow pDirection, Class<P> pPacketClass,
                                                         Function<FriendlyByteBuf, P> pDeserializer) {
        if (flows.containsKey(pDirection)) {
            PacketSet packetSet = flows.get(pDirection);
            packetSet.addPacket(pPacketClass, pDeserializer);
        } else {
            flows.put(pDirection,
                    new PacketSet()
                            .addPacket(pPacketClass, pDeserializer));
        }
    }

    @Nullable
    public static Packet createPacket(PacketFlow pDirection, int pPacketId, FriendlyByteBuf pBuffer) {
        return flows.get(pDirection).createPacket(pPacketId, pBuffer);
    }

    @Nullable
    public static Integer getPacketId(PacketFlow pDirection, Packet pPacket) {
        return flows.get(pDirection).getId(pPacket.getClass());
    }

    public static void sendPacket(Packet packet) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());

        // write id
        byteBuf.writeInt(getPacketId(PacketFlow.SERVERBOUND, packet));
        // write custom data
        packet.write(byteBuf);

        ServerboundCustomPayloadPacket customPayloadPacket = new ServerboundCustomPayloadPacket(ETERYUN_BRAND, byteBuf);
        Minecraft.getInstance().getConnection().send(customPayloadPacket);
    }

    static class PacketSet {
        final Object2IntMap<Class<? extends Packet>> classToId = make(new Object2IntOpenHashMap<>(),
                (map) -> {
                    map.defaultReturnValue(-1);
                });
        private final List<Function<FriendlyByteBuf, ? extends Packet>> idToDeserializer = Lists.newArrayList();

        public <P extends Packet> PacketSet addPacket(Class<P> pPacketClass,
                                                      Function<FriendlyByteBuf, P> pDeserializer) {
            int i = this.idToDeserializer.size();
            int j = this.classToId.put(pPacketClass, i);

            if (j != -1) {
                String s = "Packet " + pPacketClass + " is already registered to ID " + j;
                Eteryun.getLogger().error(s);
                throw new IllegalArgumentException(s);
            } else {
                String s = "Packet " + pPacketClass + " registered to ID " + i;
                Eteryun.getLogger().info(s);
                this.idToDeserializer.add(pDeserializer);
                return this;
            }
        }

        @Nullable
        public Integer getId(Class<?> pPacketClass) {
            int i = this.classToId.getInt(pPacketClass);
            return i == -1 ? null : i;
        }

        @Nullable
        public Packet createPacket(int pPacketId, FriendlyByteBuf pBuffer) {
            Function<FriendlyByteBuf, ? extends Packet> function = this.idToDeserializer.get(pPacketId);
            return function != null ? function.apply(pBuffer) : null;
        }

        public Iterable<Class<? extends Packet>> getAllPackets() {
            return Iterables.unmodifiableIterable(this.classToId.keySet());
        }
    }

    static class ProtocolBuilder {
        final Map<PacketFlow, PacketSet> flows = Maps.newEnumMap(PacketFlow.class);

        public ProtocolBuilder addFlow(PacketFlow pPacketFlow, PacketSet pPacketSet) {
            this.flows.put(pPacketFlow, pPacketSet);
            return this;
        }
    }

    private static ProtocolBuilder protocol() {
        return new ProtocolBuilder();
    }

    static <T> T make(T pObject, Consumer<T> pConsumer) {
        pConsumer.accept(pObject);
        return pObject;
    }
}

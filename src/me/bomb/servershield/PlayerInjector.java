package me.bomb.servershield;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EnumProtocolDirection;
import net.minecraft.server.v1_15_R1.PacketDecoder;
import net.minecraft.server.v1_15_R1.PacketDecompressor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

class PlayerInjector {

	protected Channel getPlayerChannel(Player player) throws IllegalArgumentException {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        return entityPlayer.playerConnection.networkManager.channel;
    }

	protected int getCompressionLevel() {
        return 256;
    }

	protected ByteToMessageDecoder getDecompressor() {
        return new PacketDecompressor(getCompressionLevel());
    }

	protected ByteToMessageDecoder getDecoder() {
        return new PanillaPacketDecoder(EnumProtocolDirection.SERVERBOUND);
    }

    private class PanillaPacketDecoder extends PacketDecoder {
        public PanillaPacketDecoder(EnumProtocolDirection enumProtocolDirection) {
            super(enumProtocolDirection);
        }

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
            try {
                super.decode(channelHandlerContext, byteBuf, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void register(ServerShield panilla, Player player) throws IOException {
        Channel pChannel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;

        if (pChannel == null || !pChannel.isRegistered()) {
            return;
        }

        /* Replace Minecraft packet decompressor */
//        ChannelHandler minecraftDecompressor = pChannel.pipeline().get(getDecompressorHandlerName());
//
//        if (minecraftDecompressor != null && !(minecraftDecompressor instanceof PacketDecompressorDplx)) {
//            PacketDecompressorDplx packetDecompressor = new PacketDecompressorDplx(panilla, player);
//            pChannel.pipeline().replace(getDecompressorHandlerName(), getDecompressorHandlerName(), packetDecompressor);
//        }

//        /* Replace Minecraft decoder */
//        ChannelHandler minecraftDecoder = pChannel.pipeline().get(getDecoderName());
//
//        if (minecraftDecoder != null) {
//            ByteToMessageDecoder decoder = getDecoder();
//            pChannel.pipeline().replace(getDecoderName(), getDecoderName(), decoder);
//        }

        /* Inject packet inspector */
        ChannelHandler minecraftHandler = pChannel.pipeline().get("packet_handler");

        if (minecraftHandler != null && !(minecraftHandler instanceof PacketInspectorDplx)) {
            PacketInspectorDplx packetInspector = new PacketInspectorDplx(panilla, player);
            pChannel.pipeline().addBefore("packet_handler", "panilla", packetInspector);
        }
    }

    protected void unregister(final Player player) throws IOException {
        Channel pChannel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;

        if (pChannel == null || !pChannel.isRegistered()) {
            return;
        }
        ChannelHandler panillaHandler = pChannel.pipeline().get("panilla");

        if (panillaHandler instanceof PacketInspectorDplx) {
            try {
                pChannel.pipeline().remove(panillaHandler);
            } catch (NoSuchElementException e) {
            }
        }
    }

}

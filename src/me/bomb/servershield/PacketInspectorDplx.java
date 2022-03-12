package me.bomb.servershield;

import org.bukkit.entity.Player;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

class PacketInspectorDplx extends ChannelDuplexHandler {

    private final ServerShield panilla;
    private final Player player;

    protected PacketInspectorDplx(ServerShield panilla, Player player) {
        this.panilla = panilla;
        this.player = player;
    }

    // player -> server
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            panilla.getPacketInspector().checkPlayIn(panilla, player, msg);
        } catch (PacketException e) {
            if (handlePacketException(player, e)) {
                return;
            }
        }

        super.channelRead(ctx, msg);
    }

    // server -> player
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            panilla.getPacketInspector().checkPlayOut(panilla, msg);
        } catch (PacketException e) {
            if (handlePacketException(player, e)) {
                return;
            }
        }

        super.write(ctx, msg, promise);
    }

    private boolean handlePacketException(Player player, PacketException e) {
    	panilla.exec(() -> {
            panilla.getInventoryCleaner().clean(player);
        });
        return true;
    }

}

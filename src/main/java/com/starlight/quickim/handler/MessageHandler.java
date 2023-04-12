package com.starlight.quickim.handler;

import com.starlight.quickim.common.Message;
import com.starlight.quickim.common.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Aaron
 * @since 1.0
 */
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        String content = msg.getContent();
        Message response = new Message();
        response.setType(msg.getType());
        response.setSenderId(msg.getSenderId());
        response.setContent(content);
        channels.writeAndFlush(response);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.add(incoming);
        channels.writeAndFlush(new Message(MessageType.SYSTEM, "[SERVER]", incoming.remoteAddress() + " is online"));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.writeAndFlush(new Message(MessageType.SYSTEM, "[SERVER]", incoming.remoteAddress() + " is offline"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel incoming = ctx.channel();
        System.out.println("Client: " + incoming.remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
}

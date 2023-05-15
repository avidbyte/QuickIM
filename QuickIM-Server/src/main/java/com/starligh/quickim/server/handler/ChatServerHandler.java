package com.starligh.quickim.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.starligh.quickim.server.common.Message;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
//import io.netty.handler.codec.http.websocketx.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Aaron
 * @since 1.0
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    static final Logger log = LoggerFactory.getLogger(ChatServerHandler.class);


    // 定义一个静态成员变量，用来保存全局的内存队列对象
    private static BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    /**
     * 存储客户端的ChannelHandlerContext
     */
    private static Map<String, ChannelHandlerContext> clients = new ConcurrentHashMap<>();

    /**
     * 存储群组信息，key为群组ID，value为群组成员的ChannelHandlerContext列表
     */
    private static Map<String, List<ChannelHandlerContext>> groups = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        if (frame instanceof TextWebSocketFrame) {
            handleTextMessage(ctx, (TextWebSocketFrame) frame);
        } else if (frame instanceof BinaryWebSocketFrame) {
            handleBinaryMessage(ctx, (BinaryWebSocketFrame) frame);
        } else if (frame instanceof PingWebSocketFrame) {
            handlePingMessage(ctx, (PingWebSocketFrame) frame);
        } else if (frame instanceof PongWebSocketFrame) {
            handlePongMessage(ctx, (PongWebSocketFrame) frame);
            // 判断是否是关闭帧
        } else if (frame instanceof CloseWebSocketFrame) {
            handleCloseMessage(ctx, (CloseWebSocketFrame) frame);
        } else {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }


    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 新建连接时，将客户端的ChannelHandlerContext保存到clients中
        String clientId = UUID.randomUUID().toString();
        clients.put(clientId, ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 连接断开时，从clients和groups中移除客户端的ChannelHandlerContext
        clients.entrySet().removeIf(entry -> entry.getValue().equals(ctx));
        groups.entrySet().forEach(entry -> entry.getValue().removeIf(entry1 -> entry1.equals(ctx)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常处理，可以记录日志或者给客户端返回错误信息等
    }


    private void handleTextMessage(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        // 解析客户端发送的消息

        String message = frame.text();
        log.error("message{}",message);
        // handle text message

        JSONObject jsonObject = JSONObject.parseObject(message);
        String messageType = jsonObject.getString("type");
        String targetId = jsonObject.getString("targetId");
        String senderId = jsonObject.getString("senderId");
        String content = jsonObject.getString("content");

        // 根据消息类型分别处理单人聊天和群组聊天
        switch (messageType) {
            case "single":
                // 发送单人聊天消息
                if (clients.containsKey(targetId)) {
                    clients.get(targetId).writeAndFlush(new TextWebSocketFrame(content));
                } else {
                    // 目标客户端不在线，可以记录日志或者给发送者返回错误信息等
                }
                break;
            case "group":
                // 发送群组聊天消息
                if (groups.containsKey(targetId)) {
                    for (ChannelHandlerContext memberCtx : groups.get(targetId)) {
                        if (!memberCtx.equals(ctx)) {
                            memberCtx.writeAndFlush(new TextWebSocketFrame(content));
                        }
                    }
                } else {
                    // 群组不存在或者群组成员为空，可以记录日志或者给发送者返回错误信息等
                    log.error("群组不存在或者群组成员为空");
                }
                break;
            default:
                // 未知消息类型，可以记录日志或者给发送者返回错误信息等
                log.error("未知消息类型");
                break;
        }
    }

    private void handleBinaryMessage(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
        BinaryWebSocketFrame retain = frame.retain();
        // handle binary message
    }

    private void handlePingMessage(ChannelHandlerContext ctx, PingWebSocketFrame frame) {
        // handle ping message
        // 处理 PingWebSocketFrame 类型的数据
        PongWebSocketFrame pongFrame = new PongWebSocketFrame(frame.retain().content().retain());
        ctx.writeAndFlush(pongFrame);
    }




    private void handlePongMessage(ChannelHandlerContext ctx, PongWebSocketFrame frame) {
        // handle pong message
        log.debug("pong");
    }

    private void handleCloseMessage(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        // handle close message
        // 处理 CloseWebSocketFrame 类型的数据
        // 关闭连接，并发送关闭帧
        ctx.writeAndFlush(frame.retain()).addListener(ChannelFutureListener.CLOSE);

    }


}



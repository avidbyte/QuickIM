package com.starligh830.quickim.server.common;

/**
 *  消息类型枚举类
 * @author aaron
 */
public enum MessageType {

    /**
     * 普通消息
     */
    NORMAL(1),

    /**
     * 系统通知
     */
    SYSTEM(2),

    /**
     * 发送心跳消息
     */
    HEARTBEAT(3);


    private final int type;

    MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

package com.starlight.quickim.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aaron
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 时间戳
     */
    private Long timestamp;

    public Message(MessageType messageType, String content, String s) {
    }
}
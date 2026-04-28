package com.gunwala.model.entitities.gunwala;

import java.time.Instant;

public class ChatMessage {

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    private MessageType type;
    private String senderId;
    private String senderName;
    private String content;
    private String conversationId;
    private String timestamp;

    public ChatMessage() {}

    public ChatMessage(MessageType type, String senderId, String senderName,
                       String content, String conversationId) {
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.conversationId = conversationId;
        this.timestamp = Instant.now().toString();
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
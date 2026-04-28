package com.gunwala.controller;

import com.gunwala.model.entitities.gunwala.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ── In-memory store (replace with DB/Redis in production) ─────────────────
    private final Map<String, List<ChatMessage>> chatHistory = new ConcurrentHashMap<>();

    // ─── WebSocket: Receive and broadcast message ─────────────────────────────
    // Frontend sends to:  /app/chat.send/{conversationId}
    // Frontend subscribes: /topic/chat/{conversationId}
    @MessageMapping("/chat.send/{conversationId}")
    public void sendMessage(
            @DestinationVariable String conversationId,
            @Payload ChatMessage message
    ) {
        message.setTimestamp(Instant.now().toString());
        message.setConversationId(conversationId);
        message.setType(ChatMessage.MessageType.CHAT);

        // Save to history
        chatHistory.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(message);

        // Broadcast to all subscribers of this conversation
        messagingTemplate.convertAndSend("/topic/chat/" + conversationId, message);
    }

    // ─── REST: Get chat history for a conversation ────────────────────────────
    // GET http://localhost:5000/api/chat/history/{conversationId}
    @GetMapping("/history/{conversationId}")
    public List<ChatMessage> getChatHistory(@PathVariable String conversationId) {
        return chatHistory.getOrDefault(conversationId, new ArrayList<>());
    }

    // ─── REST: Get all conversations for a user ───────────────────────────────
    // GET http://localhost:5000/api/chat/conversations/{userId}
    @GetMapping("/conversations/{userId}")
    public List<Map<String, Object>> getUserConversations(@PathVariable String userId) {
        List<Map<String, Object>> result = new ArrayList<>();

        chatHistory.forEach((convId, messages) -> {
            // convId format: {productId}-{sellerId}-{buyerId}
            boolean userInConversation = convId.contains(userId);
            if (userInConversation && !messages.isEmpty()) {
                ChatMessage last = messages.get(messages.size() - 1);
                Map<String, Object> conv = new HashMap<>();
                conv.put("conversationId", convId);
                conv.put("lastMessage", last.getContent());
                conv.put("lastTimestamp", last.getTimestamp());
                conv.put("messageCount", messages.size());
                result.add(conv);
            }
        });

        // Sort by latest message
        result.sort((a, b) -> ((String) b.get("lastTimestamp"))
                .compareTo((String) a.get("lastTimestamp")));

        return result;
    }
}

package com.gunwala.controller;

import com.gunwala.model.entitities.gunwala.ChatMessage;
import com.gunwala.service.Service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
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

    /** Push to the recipient's phone(s), so chats arrive even when the app is closed. */
    @Autowired
    private PushNotificationService pushService;

    // ── In-memory store (replace with DB/Redis in production) ─────────────────
    private final Map<String, List<ChatMessage>> chatHistory = new ConcurrentHashMap<>();

    /** Unread messages: userId → (conversationId → count). */
    private final Map<String, Map<String, Integer>> unread = new ConcurrentHashMap<>();

    /** Per conversation: product shown in the inbox, and the display name of each participant. */
    private final Map<String, ConversationMeta> meta = new ConcurrentHashMap<>();

    private static final class ConversationMeta {
        volatile String productTitle;
        volatile String productImage;
        final Map<String, String> names = new ConcurrentHashMap<>();
    }

    /** conversationId format: {productId}-{sellerId}-{buyerId} */
    private record Participants(String sellerId, String buyerId) {
        static Participants of(String convId) {
            String[] parts = convId.split("-");
            if (parts.length < 3) return null;
            return new Participants(parts[parts.length - 2], parts[parts.length - 1]);
        }

        boolean includes(String userId) {
            return userId.equals(sellerId) || userId.equals(buyerId);
        }

        /** The other person, or null for an owner's {seller}-{seller} thread. */
        String otherThan(String userId) {
            if (sellerId.equals(buyerId)) return null;
            if (userId.equals(sellerId)) return buyerId;
            if (userId.equals(buyerId)) return sellerId;
            return null;
        }
    }

    // ─── WebSocket: Receive and broadcast message ─────────────────────────────
    // Frontend sends to:   /app/chat.send/{conversationId}
    // Open chat window:    /topic/chat/{conversationId}
    // Notifications:       /topic/inbox/{userId}  (every message addressed to that user, any conversation)
    @MessageMapping("/chat.send/{conversationId}")
    public void sendMessage(
            @DestinationVariable String conversationId,
            @Payload ChatMessage message
    ) {
        message.setTimestamp(Instant.now().toString());
        message.setConversationId(conversationId);
        message.setType(ChatMessage.MessageType.CHAT);

        // Save to history
        chatHistory.computeIfAbsent(conversationId, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
        rememberMeta(conversationId, message);

        // Broadcast to all subscribers of this conversation
        messagingTemplate.convertAndSend("/topic/chat/" + conversationId, message);

        // Notify the recipient wherever they are in the app
        Participants p = Participants.of(conversationId);
        String recipient = (p == null || message.getSenderId() == null) ? null : p.otherThan(message.getSenderId());
        if (recipient != null) {
            Map<String, Integer> recipientUnread = unread.computeIfAbsent(recipient, k -> new ConcurrentHashMap<>());
            recipientUnread.merge(conversationId, 1, Integer::sum);
            messagingTemplate.convertAndSend("/topic/inbox/" + recipient, message);

            // Mobile push (runs in the background; does nothing until Firebase is configured)
            ConversationMeta m = meta.get(conversationId);
            int unreadTotal = recipientUnread.values().stream().mapToInt(Integer::intValue).sum();
            pushService.notifyChatMessage(
                    recipient,
                    message.getSenderId(),
                    message.getSenderName(),
                    message.getContent(),
                    conversationId,
                    m == null ? message.getProductTitle() : m.productTitle,
                    unreadTotal);
        }
    }

    // ─── REST: Get chat history for a conversation ────────────────────────────
    @GetMapping("/history/{conversationId}")
    public List<ChatMessage> getChatHistory(@PathVariable String conversationId) {
        List<ChatMessage> messages = chatHistory.get(conversationId);
        if (messages == null) return new ArrayList<>();
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }

    // ─── REST: Get all conversations for a user, newest first ────────────────
    @GetMapping("/conversations/{userId}")
    public List<Map<String, Object>> getUserConversations(@PathVariable String userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> myUnread = unread.getOrDefault(userId, Map.of());

        chatHistory.forEach((convId, messages) -> {
            Participants p = Participants.of(convId);
            if (p == null || !p.includes(userId)) return;
            ChatMessage last;
            int count;
            synchronized (messages) {
                if (messages.isEmpty()) return;
                last = messages.get(messages.size() - 1);
                count = messages.size();
            }
            ConversationMeta m = meta.get(convId);
            String peerId = p.otherThan(userId);

            Map<String, Object> conv = new HashMap<>();
            conv.put("conversationId", convId);
            conv.put("lastMessage", last.getContent());
            conv.put("lastTimestamp", last.getTimestamp());
            conv.put("lastSenderId", last.getSenderId());
            conv.put("messageCount", count);
            conv.put("unreadCount", myUnread.getOrDefault(convId, 0));
            conv.put("productTitle", m == null ? null : m.productTitle);
            conv.put("productImage", m == null ? null : m.productImage);
            conv.put("sellerName", m == null ? null : m.names.get(p.sellerId()));
            conv.put("peerName", m == null || peerId == null ? null : m.names.get(peerId));
            result.add(conv);
        });

        result.sort((a, b) -> String.valueOf(b.get("lastTimestamp")).compareTo(String.valueOf(a.get("lastTimestamp"))));
        return result;
    }

    // ─── REST: Unread counts for the header badge ─────────────────────────────
    @GetMapping("/unread/{userId}")
    public Map<String, Object> getUnread(@PathVariable String userId) {
        Map<String, Integer> byConversation = new HashMap<>(unread.getOrDefault(userId, Map.of()));
        byConversation.values().removeIf(n -> n == null || n <= 0);
        int total = byConversation.values().stream().mapToInt(Integer::intValue).sum();
        return Map.of("total", total, "conversations", byConversation);
    }

    // ─── REST: Mark a conversation as read by this user ───────────────────────
    @PostMapping("/read/{conversationId}")
    public Map<String, Object> markRead(@PathVariable String conversationId, @RequestParam String userId) {
        Map<String, Integer> mine = unread.get(userId);
        if (mine != null) mine.remove(conversationId);
        return getUnread(userId);
    }

    private void rememberMeta(String conversationId, ChatMessage message) {
        ConversationMeta m = meta.computeIfAbsent(conversationId, k -> new ConversationMeta());
        if (StringUtils.hasText(message.getProductTitle())) m.productTitle = message.getProductTitle();
        if (StringUtils.hasText(message.getProductImage())) m.productImage = message.getProductImage();
        if (StringUtils.hasText(message.getSenderId()) && StringUtils.hasText(message.getSenderName())) {
            m.names.put(message.getSenderId(), message.getSenderName());
        }
    }
}

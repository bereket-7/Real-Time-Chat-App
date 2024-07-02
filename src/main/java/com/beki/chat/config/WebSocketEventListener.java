package com.beki.chat.config;

import com.beki.chat.dto.MessageType;
import com.beki.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageSendingOperation;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if(username != null) {
            log.info("User Disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .sender(username)
                    .type(MessageType.LEAVE)
                    .build();
            messageSendingOperation.convertAndSend("/topic/public", chatMessage);
        }
    }
}

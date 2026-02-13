package com.chat.chat_microservice.dto;

import com.chat.chat_microservice.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private String sender;
    private String content;
    private String context;
    private LocalDateTime createdAt;

    public static MessageResponse fromEntity(ChatMessage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSender(message.getSender());
        response.setContent(message.getContent());
        response.setContext(message.getContext());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
package com.chat.chat_microservice.dto;

import com.chat.chat_microservice.entity.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private Long id;
    private String userId;
    private String title;
    private boolean favorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int messageCount;

    public static SessionResponse fromEntity(ChatSession session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setUserId(session.getUserId());
        response.setTitle(session.getTitle());
        response.setFavorite(session.isFavorite());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        response.setMessageCount(session.getMessages() != null ? session.getMessages().size() : 0);
        return response;
    }
}

package com.chat.chat_microservice.service;

import com.chat.chat_microservice.dto.*;
import com.chat.chat_microservice.entity.ChatMessage;
import com.chat.chat_microservice.entity.ChatSession;
import com.chat.chat_microservice.exception.ResourceNotFoundException;
import com.chat.chat_microservice.repository.ChatMessageRepository;
import com.chat.chat_microservice.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        log.info("Creating new chat session for user: {}", request.getUserId());

        ChatSession session = new ChatSession();
        session.setUserId(request.getUserId());
        session.setTitle(request.getTitle());
        session.setFavorite(false);

        ChatSession savedSession = sessionRepository.save(session);
        log.info("Chat session created with ID: {}", savedSession.getId());

        return SessionResponse.fromEntity(savedSession);
    }

    @Transactional
    public MessageResponse addMessage(Long sessionId, String userId, AddMessageRequest request) {
        log.info("Adding message to session: {} by user: {}", sessionId, userId);

        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId));

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSender(request.getSender());
        message.setContent(request.getContent());
        message.setContext(request.getContext());

        ChatMessage savedMessage = messageRepository.save(message);
        log.info("Message added with ID: {}", savedMessage.getId());

        return MessageResponse.fromEntity(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long sessionId, String userId) {
        log.info("Retrieving messages for session: {} by user: {}", sessionId, userId);

        // Verify session belongs to user
        sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId));

        List<ChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessagesPaginated(Long sessionId, String userId, Pageable pageable) {
        log.info("Retrieving paginated messages for session: {} by user: {}", sessionId, userId);

        // Verify session belongs to user
        sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId));

        Page<ChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId, pageable);
        return messages.map(MessageResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getSessions(String userId) {
        log.info("Retrieving all sessions for user: {}", userId);

        List<ChatSession> sessions = sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return sessions.stream()
                .map(SessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(Long sessionId, String userId) {
        log.info("Retrieving session: {} for user: {}", sessionId, userId);

        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId));

        return SessionResponse.fromEntity(session);
    }

    @Transactional
    public SessionResponse updateSession(Long sessionId, String userId, UpdateSessionRequest request) {
        log.info("Updating session: {} for user: {}", sessionId, userId);

        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            session.setTitle(request.getTitle());
            log.info("Session title updated to: {}", request.getTitle());
        }

        if (request.getFavorite() != null) {
            session.setFavorite(request.getFavorite());
            log.info("Session favorite status updated to: {}", request.getFavorite());
        }

        ChatSession updatedSession = sessionRepository.save(session);
        return SessionResponse.fromEntity(updatedSession);
    }

    @Transactional
    public void deleteSession(Long sessionId, String userId) {
        log.info("Deleting session: {} for user: {}", sessionId, userId);

        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId));

        sessionRepository.delete(session);
        log.info("Session deleted successfully");
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getFavoriteSessions(String userId) {
        log.info("Retrieving favorite sessions for user: {}", userId);

        List<ChatSession> sessions = sessionRepository.findByUserIdAndFavoriteOrderByUpdatedAtDesc(userId, true);
        return sessions.stream()
                .map(SessionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

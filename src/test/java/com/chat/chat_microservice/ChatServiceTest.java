package com.chat.chat_microservice;

import com.chat.chat_microservice.dto.*;
import com.chat.chat_microservice.entity.ChatMessage;
import com.chat.chat_microservice.entity.ChatSession;
import com.chat.chat_microservice.exception.ResourceNotFoundException;
import com.chat.chat_microservice.repository.ChatMessageRepository;
import com.chat.chat_microservice.repository.ChatSessionRepository;
import com.chat.chat_microservice.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @InjectMocks
    private ChatService chatService;

    private ChatSession testSession;
    private ChatMessage testMessage;
    private final String TEST_USER_ID = "user123";
    private final Long TEST_SESSION_ID = 1L;

    @BeforeEach
    void setUp() {
        testSession = new ChatSession();
        testSession.setId(TEST_SESSION_ID);
        testSession.setUserId(TEST_USER_ID);
        testSession.setTitle("Test Session");
        testSession.setFavorite(false);
        testSession.setCreatedAt(LocalDateTime.now());
        testSession.setUpdatedAt(LocalDateTime.now());
        testSession.setMessages(new ArrayList<>());

        testMessage = new ChatMessage();
        testMessage.setId(1L);
        testMessage.setSession(testSession);
        testMessage.setSender("user");
        testMessage.setContent("Hello");
        testMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createSession_ShouldReturnSessionResponse() {
        // Arrange
        CreateSessionRequest request = new CreateSessionRequest(TEST_USER_ID, "New Session");
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(testSession);

        // Act
        SessionResponse response = chatService.createSession(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_SESSION_ID, response.getId());
        assertEquals(TEST_USER_ID, response.getUserId());
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    void addMessage_WhenSessionExists_ShouldReturnMessageResponse() {
        // Arrange
        AddMessageRequest request = new AddMessageRequest("user", "Hello", null);
        when(sessionRepository.findByIdAndUserId(TEST_SESSION_ID, TEST_USER_ID))
                .thenReturn(Optional.of(testSession));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(testMessage);

        // Act
        MessageResponse response = chatService.addMessage(TEST_SESSION_ID, TEST_USER_ID, request);

        // Assert
        assertNotNull(response);
        assertEquals("user", response.getSender());
        assertEquals("Hello", response.getContent());
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    void addMessage_WhenSessionNotFound_ShouldThrowException() {
        // Arrange
        AddMessageRequest request = new AddMessageRequest("user", "Hello", null);
        when(sessionRepository.findByIdAndUserId(TEST_SESSION_ID, TEST_USER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                chatService.addMessage(TEST_SESSION_ID, TEST_USER_ID, request));
    }

    @Test
    void getMessages_WhenSessionExists_ShouldReturnMessages() {
        // Arrange
        List<ChatMessage> messages = List.of(testMessage);
        when(sessionRepository.findByIdAndUserId(TEST_SESSION_ID, TEST_USER_ID))
                .thenReturn(Optional.of(testSession));
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc(TEST_SESSION_ID))
                .thenReturn(messages);

        // Act
        List<MessageResponse> responses = chatService.getMessages(TEST_SESSION_ID, TEST_USER_ID);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Hello", responses.get(0).getContent());
    }

    @Test
    void updateSession_ShouldUpdateTitleAndFavorite() {
        // Arrange
        UpdateSessionRequest request = new UpdateSessionRequest("Updated Title", true);
        when(sessionRepository.findByIdAndUserId(TEST_SESSION_ID, TEST_USER_ID))
                .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(testSession);

        // Act
        SessionResponse response = chatService.updateSession(TEST_SESSION_ID, TEST_USER_ID, request);

        // Assert
        assertNotNull(response);
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    void deleteSession_WhenSessionExists_ShouldDeleteSession() {
        // Arrange
        when(sessionRepository.findByIdAndUserId(TEST_SESSION_ID, TEST_USER_ID))
                .thenReturn(Optional.of(testSession));

        // Act
        chatService.deleteSession(TEST_SESSION_ID, TEST_USER_ID);

        // Assert
        verify(sessionRepository, times(1)).delete(testSession);
    }

    @Test
    void getSessions_ShouldReturnAllUserSessions() {
        // Arrange
        List<ChatSession> sessions = List.of(testSession);
        when(sessionRepository.findByUserIdOrderByUpdatedAtDesc(TEST_USER_ID))
                .thenReturn(sessions);

        // Act
        List<SessionResponse> responses = chatService.getSessions(TEST_USER_ID);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Session", responses.get(0).getTitle());
    }

    @Test
    void getFavoriteSessions_ShouldReturnOnlyFavorites() {
        // Arrange
        testSession.setFavorite(true);
        List<ChatSession> sessions = List.of(testSession);
        when(sessionRepository.findByUserIdAndFavoriteOrderByUpdatedAtDesc(TEST_USER_ID, true))
                .thenReturn(sessions);

        // Act
        List<SessionResponse> responses = chatService.getFavoriteSessions(TEST_USER_ID);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).isFavorite());
    }
}
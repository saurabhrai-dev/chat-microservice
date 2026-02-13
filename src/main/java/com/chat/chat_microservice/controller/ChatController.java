package com.chat.chat_microservice.controller;

import com.chat.chat_microservice.dto.*;
import com.chat.chat_microservice.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Management", description = "APIs for managing chat sessions and messages")
@SecurityRequirement(name = "apiKey")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/sessions")
    @Operation(summary = "Create a new chat session")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @Valid @RequestBody CreateSessionRequest request) {

        SessionResponse session = chatService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(session, "Chat session created successfully"));
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get all sessions for a user")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessions(
            @Parameter(description = "User ID") @RequestParam String userId) {

        List<SessionResponse> sessions = chatService.getSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get a specific session")
    public ResponseEntity<ApiResponse<SessionResponse>> getSession(
            @PathVariable Long sessionId,
            @Parameter(description = "User ID") @RequestParam String userId) {

        SessionResponse session = chatService.getSession(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @PatchMapping("/sessions/{sessionId}")
    @Operation(summary = "Update session (rename or mark as favorite)")
    public ResponseEntity<ApiResponse<SessionResponse>> updateSession(
            @PathVariable Long sessionId,
            @Parameter(description = "User ID") @RequestParam String userId,
            @Valid @RequestBody UpdateSessionRequest request) {

        SessionResponse session = chatService.updateSession(sessionId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(session, "Session updated successfully"));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Delete a chat session")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @PathVariable Long sessionId,
            @Parameter(description = "User ID") @RequestParam String userId) {

        chatService.deleteSession(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Session deleted successfully"));
    }

    @GetMapping("/sessions/favorites")
    @Operation(summary = "Get all favorite sessions for a user")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getFavoriteSessions(
            @Parameter(description = "User ID") @RequestParam String userId) {

        List<SessionResponse> sessions = chatService.getFavoriteSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Add a message to a session")
    public ResponseEntity<ApiResponse<MessageResponse>> addMessage(
            @PathVariable Long sessionId,
            @Parameter(description = "User ID") @RequestParam String userId,
            @Valid @RequestBody AddMessageRequest request) {

        MessageResponse message = chatService.addMessage(sessionId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, "Message added successfully"));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Get all messages in a session")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @PathVariable Long sessionId,
            @Parameter(description = "User ID") @RequestParam String userId) {

        List<MessageResponse> messages = chatService.getMessages(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/sessions/{sessionId}/messages/paginated")
    @Operation(summary = "Get paginated messages in a session")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessagesPaginated(
            @PathVariable Long sessionId,
            @Parameter(description = "User ID") @RequestParam String userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponse> messages = chatService.getMessagesPaginated(sessionId, userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
}

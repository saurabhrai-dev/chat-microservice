package com.chat.chat_microservice.repository;

import com.chat.chat_microservice.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);

    Page<ChatSession> findByUserId(String userId, Pageable pageable);

    Optional<ChatSession> findByIdAndUserId(Long id, String userId);

    List<ChatSession> findByUserIdAndFavoriteOrderByUpdatedAtDesc(String userId, boolean favorite);

    void deleteByIdAndUserId(Long id, String userId);
}

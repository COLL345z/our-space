package com.example.demo.repository;

import com.example.demo.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    List<MessageReaction> findAll();
    Optional<MessageReaction> findByMessageIdAndUsernameAndEmoji(Long messageId, String username, String emoji);
}

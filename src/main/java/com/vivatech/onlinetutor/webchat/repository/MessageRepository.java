package com.vivatech.onlinetutor.webchat.repository;

import com.vivatech.onlinetutor.webchat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Direct messages between two users
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender.id = :userId1 AND m.recipient.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.recipient.id = :userId1)) AND " +
           "m.group IS NULL " +
           "ORDER BY m.sentAt DESC")
    Page<Message> findDirectMessagesBetweenUsers(@Param("userId1") Long userId1, 
                                               @Param("userId2") Long userId2, 
                                               Pageable pageable);
    
    // Group messages
    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId ORDER BY m.sentAt DESC")
    Page<Message> findMessagesByGroupId(@Param("groupId") Long groupId, Pageable pageable);
    
    // All messages for a user (both sent and received)
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.recipient.id = :userId")
    List<Message> findMessagesByUserId(@Param("userId") Long userId);
    
    // Unread messages for a user
    @Query("SELECT m FROM Message m WHERE m.recipient.id = :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByUserId(@Param("userId") Long userId);
    
    // Count unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient.id = :userId AND m.isRead = false")
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);
    
    // Recent conversations for a user
    @Query("SELECT DISTINCT m FROM Message m WHERE " +
           "(m.sender.id = :userId OR m.recipient.id = :userId) AND m.group IS NULL " +
           "ORDER BY m.sentAt DESC")
    List<Message> findRecentDirectMessages(@Param("userId") Long userId);
}
package com.vivatech.onlinetutor.webchat.repository;


import com.vivatech.onlinetutor.webchat.model.ChatGroup;
import com.vivatech.onlinetutor.webchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    
    List<ChatGroup> findByIsActiveTrue();
    
    List<ChatGroup> findByCreatedBy(User createdBy);
    
    @Query("SELECT g FROM ChatGroup g WHERE g.isActive = true AND " +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ChatGroup> searchActiveGroups(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT g FROM ChatGroup g JOIN g.members m WHERE m.user.id = :userId AND g.isActive = true")
    List<ChatGroup> findGroupsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(m) FROM GroupMember m WHERE m.group.id = :groupId")
    Long countMembersByGroupId(@Param("groupId") Long groupId);
}
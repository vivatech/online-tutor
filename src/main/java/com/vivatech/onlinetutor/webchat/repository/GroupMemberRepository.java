package com.vivatech.onlinetutor.webchat.repository;

import com.vivatech.onlinetutor.webchat.model.ChatGroup;
import com.vivatech.onlinetutor.webchat.model.GroupMember;
import com.vivatech.onlinetutor.webchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    List<GroupMember> findByGroup(ChatGroup group);
    
    List<GroupMember> findByUser(User user);
    
    Optional<GroupMember> findByGroupAndUser(ChatGroup group, User user);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<GroupMember> findByGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user.id = :userId")
    List<GroupMember> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id = :groupId")
    Long countByGroupId(@Param("groupId") Long groupId);
    
    boolean existsByGroupAndUser(ChatGroup group, User user);
}
package com.vivatech.onlinetutor.webchat.repository;

import com.vivatech.onlinetutor.webchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByIsActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "u.isActive = true")
    List<User> searchActiveUsers(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', COALESCE(:searchTerm, ''), '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', COALESCE(:searchTerm, ''), '%')) OR " +
            "LOWER(u.msisdn) LIKE LOWER(CONCAT('%', COALESCE(:searchTerm, ''), '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', COALESCE(:searchTerm, ''), '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "u.isActive = true")
    List<User> searchActiveUsersForChat(@Param("searchTerm") String searchTerm, @Param("role") User.UserRole role);

    boolean existsByMsisdn(String msisdn);
}
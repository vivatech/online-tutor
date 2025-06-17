package com.vivatech.online_tutor.repository;

import com.vivatech.online_tutor.model.TutorSession;
import com.vivatech.online_tutor.webchat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TutorSessionRepository extends JpaRepository<TutorSession, Integer> {

    // Find sessions by title (case-insensitive)
    List<TutorSession> findBySessionTitleContainingIgnoreCase(String title);

    // Find sessions by type
    List<TutorSession> findBySessionType(TutorSession.SessionType sessionType);

    // Find sessions by date range
    List<TutorSession> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);

    // Find upcoming sessions
    @Query("SELECT s FROM TutorSession s WHERE s.sessionDate >= :currentDate ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<TutorSession> findUpcomingSessions(@Param("currentDate") LocalDate currentDate);

    // Find sessions with available spots
    @Query("SELECT s FROM TutorSession s WHERE s.maxStudents IS NULL OR s.maxStudents > 0")
    List<TutorSession> findSessionsWithAvailableSpots();

    // Find public sessions
    List<TutorSession> findByVisibility(TutorSession.Visibility visibility);

    // Find sessions by price range
    @Query("SELECT s FROM TutorSession s WHERE s.pricePerSession BETWEEN :minPrice AND :maxPrice")
    List<TutorSession> findSessionsByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                                @Param("maxPrice") java.math.BigDecimal maxPrice);

    // Paginated search
    Page<TutorSession> findBySessionTitleContainingIgnoreCaseAndVisibility(
            String title, TutorSession.Visibility visibility, Pageable pageable);

    @Query("SELECT u FROM TutorSession u WHERE " +
            "u.createdBy.username = :userId AND " +
            "(LOWER(u.sessionTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.sessionType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.recurrenceFrequency) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<TutorSession> searchAllSessions(@Param("searchTerm") String searchTerm, @Param("userId") String userId);

    List<TutorSession> findByCreatedBy(User tutorUser);
    List<TutorSession> findByCreatedByAndSessionEndDateGreaterThanEqual(User tutorUser, LocalDate endDate);
}

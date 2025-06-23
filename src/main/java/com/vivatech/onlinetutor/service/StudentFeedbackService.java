package com.vivatech.onlinetutor.service;

import com.vivatech.onlinetutor.dto.StudentFeedbackRequest;
import com.vivatech.onlinetutor.dto.StudentFeedbackResponse;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.model.SessionRegistration;
import com.vivatech.onlinetutor.model.StudentFeedback;
import com.vivatech.onlinetutor.repository.SessionRegistrationRepository;
import com.vivatech.onlinetutor.repository.StudentFeedbackRepository;
import com.vivatech.onlinetutor.webchat.model.User;
import com.vivatech.onlinetutor.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentFeedbackService {

    @Autowired
    private StudentFeedbackRepository studentFeedbackRepository;

    @Autowired
    private SessionRegistrationRepository sessionRegistrationRepository;
    @Autowired
    private UserRepository userRepository;

    public StudentFeedbackResponse createFeedback(StudentFeedbackRequest request) {
        // Validate session registration exists
        User tutor = userRepository.findByUsername(request.getCreatedBy()).orElseThrow(() -> new OnlineTutorExceptionHandler("User not found"));
        SessionRegistration sessionRegistration = sessionRegistrationRepository.findById(request.getSessionRegistrationId())
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Session registration not found with id: " + request.getSessionRegistrationId()));

        // Check if feedback already exists for this session registration and tutor
        if (studentFeedbackRepository.existsBySessionRegistrationIdAndTutorId(request.getSessionRegistrationId(), tutor.getId())) {
            throw new OnlineTutorExceptionHandler("Feedback already exists for this student and session");
        }

        StudentFeedback feedback = mapRequestToEntity(request, sessionRegistration, tutor);
        StudentFeedback savedFeedback = studentFeedbackRepository.save(feedback);

        return mapEntityToResponse(savedFeedback);
    }

    public StudentFeedbackResponse updateFeedback(Integer feedbackId, StudentFeedbackRequest request) {
        StudentFeedback existingFeedback = studentFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Student feedback not found with id: " + feedbackId));

        // Update fields
        updateFeedbackFields(existingFeedback, request);

        StudentFeedback updatedFeedback = studentFeedbackRepository.save(existingFeedback);
        return mapEntityToResponse(updatedFeedback);
    }

    public StudentFeedbackResponse getFeedbackById(Integer feedbackId) {
        StudentFeedback feedback = studentFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Student feedback not found with id: " + feedbackId));

        return mapEntityToResponse(feedback);
    }

    public ResponseEntity<Void> deleteFeedback(Integer feedbackId) {
        if (!studentFeedbackRepository.existsById(feedbackId)) {
            throw new OnlineTutorExceptionHandler("Student feedback not found with id: " + feedbackId);
        }
        studentFeedbackRepository.deleteById(feedbackId);
        return null;
    }

    private StudentFeedback mapRequestToEntity(StudentFeedbackRequest request, SessionRegistration sessionRegistration, User tutor) {
        StudentFeedback feedback = new StudentFeedback();

        feedback.setSessionRegistration(sessionRegistration);
        feedback.setTutor(tutor);

        // Attendance
        feedback.setAttendanceStatus(request.getAttendanceStatus());
        feedback.setTutorNotesAttendance(request.getTutorNotesAttendance());

        // Engagement & Participation
        feedback.setObservedBehaviors(request.getObservedBehaviors());
        feedback.setEngagementLevel(request.getEngagementLevel());

        // Comprehension & Understanding
        feedback.setUnderstandingLevel(request.getUnderstandingLevel());
        feedback.setComprehensionIssues(request.getComprehensionIssues());

        // Assignments & Work Submission
        feedback.setAssignmentGiven(request.getAssignmentGiven());
        feedback.setAssignmentType(request.getAssignmentType());
        feedback.setAssignmentDeadline(request.getAssignmentDeadline());
        feedback.setSubmissionReceived(request.getSubmissionReceived());
        feedback.setWorkQuality(request.getWorkQuality());

        // Feedback to Student
        feedback.setAcademicStrengths(request.getAcademicStrengths());
        feedback.setAreasForImprovement(request.getAreasForImprovement());
        feedback.setRecommendedFocus(request.getRecommendedFocus());

        // Parental Follow-up
        feedback.setSuggestedMessageParent(request.getSuggestedMessageParent());
        feedback.setFollowUpMeetingRequested(request.getFollowUpMeetingRequested());
        feedback.setFollowUpReason(request.getFollowUpReason());

        // Session Review Summary
        feedback.setOverallSessionRating(request.getOverallSessionRating());
        feedback.setAdditionalTutorComments(request.getAdditionalTutorComments());

        return feedback;
    }

    private void updateFeedbackFields(StudentFeedback feedback, StudentFeedbackRequest request) {

        // Attendance
        if (request.getAttendanceStatus() != null) feedback.setAttendanceStatus(request.getAttendanceStatus());
        if (request.getTutorNotesAttendance() != null) feedback.setTutorNotesAttendance(request.getTutorNotesAttendance());

        // Engagement & Participation
        if (request.getObservedBehaviors() != null) feedback.setObservedBehaviors(request.getObservedBehaviors());
        if (request.getEngagementLevel() != null) feedback.setEngagementLevel(request.getEngagementLevel());

        // Comprehension & Understanding
        if (request.getUnderstandingLevel() != null) feedback.setUnderstandingLevel(request.getUnderstandingLevel());
        if (request.getComprehensionIssues() != null) feedback.setComprehensionIssues(request.getComprehensionIssues());

        // Assignments & Work Submission
        if (request.getAssignmentGiven() != null) feedback.setAssignmentGiven(request.getAssignmentGiven());
        if (request.getAssignmentType() != null) feedback.setAssignmentType(request.getAssignmentType());
        if (request.getAssignmentDeadline() != null) feedback.setAssignmentDeadline(request.getAssignmentDeadline());
        if (request.getSubmissionReceived() != null) feedback.setSubmissionReceived(request.getSubmissionReceived());
        if (request.getWorkQuality() != null) feedback.setWorkQuality(request.getWorkQuality());

        // Feedback to Student
        if (request.getAcademicStrengths() != null) feedback.setAcademicStrengths(request.getAcademicStrengths());
        if (request.getAreasForImprovement() != null) feedback.setAreasForImprovement(request.getAreasForImprovement());
        if (request.getRecommendedFocus() != null) feedback.setRecommendedFocus(request.getRecommendedFocus());

        // Parental Follow-up
        if (request.getSuggestedMessageParent() != null) feedback.setSuggestedMessageParent(request.getSuggestedMessageParent());
        if (request.getFollowUpMeetingRequested() != null) feedback.setFollowUpMeetingRequested(request.getFollowUpMeetingRequested());
        if (request.getFollowUpReason() != null) feedback.setFollowUpReason(request.getFollowUpReason());

        // Session Review Summary
        if (request.getOverallSessionRating() != null) feedback.setOverallSessionRating(request.getOverallSessionRating());
        if (request.getAdditionalTutorComments() != null) feedback.setAdditionalTutorComments(request.getAdditionalTutorComments());
    }

    private StudentFeedbackResponse mapEntityToResponse(StudentFeedback feedback) {
        StudentFeedbackResponse response = new StudentFeedbackResponse();

        response.setId(feedback.getId());
        response.setSessionRegistrationId(feedback.getSessionRegistration().getId());
        response.setTutorId(feedback.getSessionRegistration().getRegisteredSession().getCreatedBy().getId());
        response.setTutorName(feedback.getSessionRegistration().getRegisteredSession().getCreatedBy().getFullName());
        response.setCourseTitle(feedback.getSessionRegistration().getRegisteredSession().getSubject());
        response.setSessionId(feedback.getSessionRegistration().getRegisteredSession().getId().toString());
        response.setSessionDate(feedback.getSessionRegistration().getRegisteredSession().getSessionDate());
        response.setSessionTime(feedback.getSessionRegistration().getRegisteredSession().getStartTime());
        response.setSessionDuration(feedback.getSessionRegistration().getRegisteredSession().getDurationMinutes());
        response.setStudentName(feedback.getSessionRegistration().getStudentName());
        response.setStudentId(feedback.getSessionRegistration().getId());

        // Attendance
        response.setAttendanceStatus(feedback.getAttendanceStatus());
        response.setTutorNotesAttendance(feedback.getTutorNotesAttendance());

        // Engagement & Participation
        response.setObservedBehaviors(feedback.getObservedBehaviors());
        response.setEngagementLevel(feedback.getEngagementLevel());

        // Comprehension & Understanding
        response.setUnderstandingLevel(feedback.getUnderstandingLevel());
        response.setComprehensionIssues(feedback.getComprehensionIssues());

        // Assignments & Work Submission
        response.setAssignmentGiven(feedback.getAssignmentGiven());
        response.setAssignmentType(feedback.getAssignmentType());
        response.setAssignmentDeadline(feedback.getAssignmentDeadline());
        response.setSubmissionReceived(feedback.getSubmissionReceived());
        response.setWorkQuality(feedback.getWorkQuality());

        // Feedback to Student
        response.setAcademicStrengths(feedback.getAcademicStrengths());
        response.setAreasForImprovement(feedback.getAreasForImprovement());
        response.setRecommendedFocus(feedback.getRecommendedFocus());

        // Parental Follow-up
        response.setSuggestedMessageParent(feedback.getSuggestedMessageParent());
        response.setFollowUpMeetingRequested(feedback.getFollowUpMeetingRequested());
        response.setFollowUpReason(feedback.getFollowUpReason());

        // Session Review Summary
        response.setOverallSessionRating(feedback.getOverallSessionRating());
        response.setAdditionalTutorComments(feedback.getAdditionalTutorComments());

        response.setCreatedAt(feedback.getCreatedAt());
        response.setUpdatedAt(feedback.getUpdatedAt());

        return response;
    }

    public StudentFeedbackResponse getFeedbackByRegistrationId(Integer registrationId) {
        return studentFeedbackRepository.findBySessionRegistrationId(registrationId)
                .map(this::mapEntityToResponse)
                .orElseThrow(() -> new OnlineTutorExceptionHandler("Feedback not found for registration ID: " + registrationId));
    }
}
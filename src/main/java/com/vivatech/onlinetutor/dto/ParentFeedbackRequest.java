package com.vivatech.onlinetutor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentFeedbackRequest {
    private String parentName;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private int rating;
    
    private Integer sessionId; // To reference the Session
    @Max(value = 200, message = "Comment must be less than 200 characters")
    private String comment;

    private Integer submittedById;
    private SubmittedByType submittedByType;
    private String username;

    public enum SubmittedByType {
        PARENT, STUDENT
    }

}

package com.vivatech.onlinetutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRegistrationDto {
    private Integer id;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private String studentAge;
    private String guardianName;
    private Integer sessionId;
    private String status;
    private String reason;
}

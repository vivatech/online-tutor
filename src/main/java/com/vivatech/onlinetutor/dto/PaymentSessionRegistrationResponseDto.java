package com.vivatech.onlinetutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSessionRegistrationResponseDto {
    private Integer id;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String age;
    private String gender;
    private String status;
}

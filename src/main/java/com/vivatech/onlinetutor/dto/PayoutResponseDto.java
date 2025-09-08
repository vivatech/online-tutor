package com.vivatech.onlinetutor.dto;

import com.vivatech.onlinetutor.helper.AppEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutResponseDto {
    private Integer sessionId;
    private Double amount;
    private Double commission;
    private Double netAmount;
    private AppEnums.PaymentStatus paymentStatus;
    private String sessionName;
    private String createdByName;
    private Integer noOfParticipant;
}

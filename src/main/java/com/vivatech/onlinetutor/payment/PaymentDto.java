package com.vivatech.onlinetutor.payment;

import com.vivatech.onlinetutor.helper.AppEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    private String msisdn;
    private Double amount;
    private String transactionId;
    private String referenceNo;
    private AppEnums.PaymentMode paymentMode;
    private Integer sessionRegistrationId;
    private String reason;

}

package com.vivatech.onlinetutor.dto;

import com.vivatech.onlinetutor.payment.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRegistrationRequestDto {
    private SessionRegistrationDto registrationDto;
    private PaymentDto paymentDto;
}

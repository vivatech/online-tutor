package com.vivatech.onlinetutor.payment.intasend;

import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/intasend/webhook")
public class IntaSendWebhook {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> handlePaymentCollectionWebhook(@RequestBody IntaSendPaymentResponse callbackRequest) {
        // Log the received callback
        String callbackData = CustomUtils.makeDtoToJsonString(callbackRequest);
        log.info("Received Inta send STK Callback: {}", callbackData);

        // Process the callback
        paymentService.processPaymentCallBack(callbackRequest.getApi_ref(), callbackRequest.getInvoice_id(), callbackRequest.getState(), callbackRequest.getFailed_reason());

        // Return success response
        return ResponseEntity.ok("Callback received successfully");
    }

    @PostMapping("/b2c")
    public void handleB2CPaymentWebhook(@RequestBody String callbackRequest) {
        // Log the received callback
        log.info("Received B2C Inta send Callback: {}", callbackRequest);

    }

}

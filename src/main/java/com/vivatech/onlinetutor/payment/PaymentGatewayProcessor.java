package com.vivatech.onlinetutor.payment;

import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.helper.AppEnums;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PaymentGatewayProcessor {
    private final List<PaymentInterface> sortedProcessors;

    public PaymentGatewayProcessor(List<PaymentInterface> sortedProcessors) {
        this.sortedProcessors = sortedProcessors;
    }

    public PaymentInterface getMatchedProcessor(AppEnums.PaymentMode paymentMode) {
        for (PaymentInterface processor : sortedProcessors) {
            if(processor.supports(paymentMode)) {
                return processor;
            }
        }
        return null;
    }

    public Response sendPayment(PaymentDto paymentDto, AppEnums.PaymentMode paymentMode){
        PaymentInterface matchedProcessor = getMatchedProcessor(paymentMode);
        return matchedProcessor.sendPayment(paymentDto);
    }

    public Response refundPayment(PaymentDto dto, AppEnums.PaymentMode paymentMode){
        PaymentInterface matchedProcessor = getMatchedProcessor(paymentMode);
        return matchedProcessor.reversePayment(dto);
    }

    public AppEnums.PaymentAggregator getPaymentAggregator(String country) {
        HashMap<String, AppEnums.PaymentAggregator> map = new HashMap<>();
        map.put("SO", AppEnums.PaymentAggregator.WAAFI);
        map.put("KE", AppEnums.PaymentAggregator.SAFARI);
        return map.get(country);
    }
}

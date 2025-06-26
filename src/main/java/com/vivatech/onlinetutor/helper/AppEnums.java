package com.vivatech.onlinetutor.helper;

public class AppEnums {
    public enum EventStatus {
        SUCCESS, FAILED, PENDING, ACTIVE, COMPLETED, CANCELLED, APPROVE, REJECT
    }

    public enum PaymentAggregator {
        SAFARI, WAAFI
    }

    public enum PaymentMode {
        CASH, DIGITAL_WALLET, BNPL, BANK_TRANSFER, MPESA, CARD, INTA_SEND
    }

    public enum PaymentStatus {
        SUCCESS, FAILED, PENDING, CANCELLED, REFUND, REFUND_FAILED
    }

    public enum NotificationType {
        REGISTRATION, EMERGENCY, PAYMENT, FEEDBACK
    }

    public static enum ApplicationName {
        MUMLY_EVENT,
        MUMLY_TUTOR;
    }

    public enum MeetingAggregator {
        DYTE, ZOOM
    }

}

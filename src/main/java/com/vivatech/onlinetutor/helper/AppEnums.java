package com.vivatech.onlinetutor.helper;

public class AppEnums {
    public enum EventStatus {
        SUCCESS, FAILED, PENDING, ACTIVE, COMPLETED, CANCELLED, APPROVE, REJECT
    }

    public enum PaymentAggregator {
        SAFARI, WAAFI
    }

    public enum PaymentMode {
        CASH, BANK_TRANSFER, MPESA
    }

    public enum PaymentStatus {
        SUCCESS, FAILED, PENDING, CANCELLED, REFUND, REFUND_FAILED, COMPLETE, REJECT
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

    public enum PaymentBreakUp {
        GROSS_REVENUE, COMMISSION, NET_REVENUE
    }

    public enum DocumentType {
        FILE, LINK
    }

}

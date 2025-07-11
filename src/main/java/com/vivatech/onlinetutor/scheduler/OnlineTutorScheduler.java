package com.vivatech.onlinetutor.scheduler;

import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.model.MumlyTutorPayment;
import com.vivatech.onlinetutor.model.MumlyTutorPayout;
import com.vivatech.onlinetutor.model.TutorSession;
import com.vivatech.onlinetutor.repository.MumlyTutorPaymentRepository;
import com.vivatech.onlinetutor.repository.MumlyTutorPayoutRepository;
import com.vivatech.onlinetutor.repository.TutorSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class OnlineTutorScheduler {
    @Autowired
    private MumlyTutorPaymentRepository mumlyTutorPaymentRepository;
    @Autowired
    private MumlyTutorPayoutRepository mumlyTutorPayoutRepository;

    @Autowired
    private TutorSessionRepository tutorSessionRepository;


    @Scheduled(cron = "0 0 0 * * *")
    private void updateUpcomingDates() {
        List<TutorSession> sessionList = tutorSessionRepository.findBySessionEndDateGreaterThanEqual(LocalDate.now());
        for (TutorSession tutorSession : sessionList) {
            if (tutorSession.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.WEEKLY)
                    || tutorSession.getRecurrenceFrequency().equals(TutorSession.RecurrenceFrequency.BIWEEKLY)) {
                tutorSession.getUpcomingDates().clear();
                tutorSession.setUpcomingDates(tutorSession.calculateUpcomingDates());
            }
            tutorSessionRepository.save(tutorSession);
        }
    }

    @Scheduled(cron = "0 */1 * * * *")
    private void sumAllTutorPayout() {
        log.info("Executing sumAllTutorPayout at {}", LocalDateTime.now());
        List<TutorSession> sessionList = tutorSessionRepository.findBySessionEndDateGreaterThanEqual(LocalDate.now());
        for (TutorSession session : sessionList) {
            MumlyTutorPayout mumlyTutorPayout = mumlyTutorPayoutRepository.findByTutorSessionId(session.getId());
            if (mumlyTutorPayout != null
                    && Stream.of(AppEnums.PaymentStatus.SUCCESS, AppEnums.PaymentStatus.COMPLETE)
                    .anyMatch(ele -> ele.toString().equals(mumlyTutorPayout.getPaymentStatus()))) continue;
            if (mumlyTutorPayout != null) {
                List<MumlyTutorPayment> paymentList = mumlyTutorPaymentRepository
                        .findBySessionRegistrationRegisteredSessionIdAndPaymentStatus(
                                mumlyTutorPayout.getTutorSession().getId(),
                                AppEnums.PaymentStatus.COMPLETE.toString()
                        );
                if (paymentList.isEmpty()) continue;
                Map<AppEnums.PaymentBreakUp, Double> paymentBreakUp = getPaymentBreakUp(paymentList);
                mumlyTutorPayout.setAmount(paymentBreakUp.get(AppEnums.PaymentBreakUp.GROSS_REVENUE));
                mumlyTutorPayout.setCommission(paymentBreakUp.get(AppEnums.PaymentBreakUp.COMMISSION));
                mumlyTutorPayout.setNetAmount(paymentBreakUp.get(AppEnums.PaymentBreakUp.NET_REVENUE));
                mumlyTutorPayoutRepository.save(mumlyTutorPayout);
            } else {
                MumlyTutorPayout payout = preparePayout(mumlyTutorPaymentRepository.findBySessionRegistrationRegisteredSessionId(session.getId()), session);
                mumlyTutorPayoutRepository.save(payout);
            }
        }
    }

    private Map<AppEnums.PaymentBreakUp, Double> getPaymentBreakUp(List<MumlyTutorPayment> payment) {
        double totalPayment = payment.stream().mapToDouble(MumlyTutorPayment::getAmount).sum();
        double commission = 0; //totalPayment * 0.2;
        double netPayment = 0; //totalPayment - commission;
        return Map.of(
                AppEnums.PaymentBreakUp.GROSS_REVENUE, totalPayment,
                AppEnums.PaymentBreakUp.COMMISSION, commission,
                AppEnums.PaymentBreakUp.NET_REVENUE, netPayment);
    }

    private MumlyTutorPayout preparePayout(List<MumlyTutorPayment> payment, TutorSession session) {
        Map<AppEnums.PaymentBreakUp, Double> paymentBreakUp = getPaymentBreakUp(payment);
        MumlyTutorPayout payout = new MumlyTutorPayout();
        payout.setTutorSession(session);
        payout.setAmount(paymentBreakUp.get(AppEnums.PaymentBreakUp.GROSS_REVENUE));
        payout.setCommission(paymentBreakUp.get(AppEnums.PaymentBreakUp.COMMISSION));
        payout.setNetAmount(paymentBreakUp.get(AppEnums.PaymentBreakUp.NET_REVENUE));
        payout.setPaymentStatus(AppEnums.PaymentStatus.PENDING.toString());
        payout.setTransactionId(null);
        payout.setReferenceNo(null);
        payout.setPaymentMode(null);
        payout.setReason(null);
        return payout;
    }
}

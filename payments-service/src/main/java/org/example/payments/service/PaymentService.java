package org.example.payments.service;

import org.example.payments.dto.PaymentRequest;
import org.example.payments.domain.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(PaymentRequest request);
    Optional<Payment> getPayment(String id);
    List<Payment> listPayments();
    Payment updatePayment(String id, PaymentRequest request);
    void deletePayment(String id);
}

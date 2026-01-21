package org.example.payments.service;

import org.example.payments.domain.Payment;
import org.example.payments.dto.PaymentRequest;
import org.example.payments.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final RestTemplate restTemplate;
    private final String bankUrl;

    public PaymentServiceImpl(PaymentRepository repository, @Value("${bank.provider.url}") String bankUrl) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.bankUrl = bankUrl;
    }

    @Override
    @Transactional
    public Payment createPayment(PaymentRequest request) {
        Payment p = new Payment();
        p.setAmount(request.getAmount());
        p.setCurrency(request.getCurrency());
        p.setPayerName(request.getPayerName());
        p.setPayerEmail(request.getPayerEmail());
        p.setStatus("PROCESSING");
        p.setUpdatedAt(Instant.now());
        repository.save(p);

        // Build payload for bank provider
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", request.getAmount());
        payload.put("currency", request.getCurrency());
        payload.put("payerName", request.getPayerName());
        payload.put("payerEmail", request.getPayerEmail());
        payload.put("metadata", request.getMetadata());

        try {
            ResponseEntity<Map> resp = restTemplate.postForEntity(bankUrl + "/charge", payload, Map.class);
            Map body = resp.getBody();
            // save raw provider response
            p.setProviderResponse(body != null ? body.toString() : null);

            if (body != null) {
                Object statusObj = body.get("status");
                String status = statusObj != null ? statusObj.toString() : null;
                Object tx = body.get("transactionId");
                if (tx != null) p.setProviderTransactionId(tx.toString());

                if ("APPROVED".equalsIgnoreCase(status)) {
                    p.setStatus("COMPLETED");
                } else {
                    // any non-approved is treated as failed
                    p.setStatus("FAILED");
                }
            } else {
                p.setStatus("FAILED");
            }

            p.setUpdatedAt(Instant.now());
            repository.save(p);

        } catch (Exception e) {
            // network or deserialization errors
            p.setStatus("FAILED");
            p.setProviderResponse("ERROR: " + e.getMessage());
            p.setUpdatedAt(Instant.now());
            repository.save(p);
        }

        return p;
    }

    @Override
    public Optional<Payment> getPayment(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Payment> listPayments() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Payment updatePayment(String id, PaymentRequest request) {
        Payment p = repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        if (request.getAmount() != null) p.setAmount(request.getAmount());
        if (request.getCurrency() != null) p.setCurrency(request.getCurrency());
        if (request.getPayerName() != null) p.setPayerName(request.getPayerName());
        if (request.getPayerEmail() != null) p.setPayerEmail(request.getPayerEmail());
        p.setUpdatedAt(Instant.now());
        return repository.save(p);
    }

    @Override
    public void deletePayment(String id) {
        repository.deleteById(id);
    }
}

package org.example.payments.controller;

import org.example.payments.domain.Payment;
import org.example.payments.dto.PaymentRequest;
import org.example.payments.dto.PaymentResponse;
import org.example.payments.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        Payment p = service.createPayment(request);
        PaymentResponse resp = toResponse(p);
        return ResponseEntity.created(URI.create("/api/payments/" + p.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> get(@PathVariable String id) {
        Optional<Payment> p = service.getPayment(id);
        return p.map(payment -> ResponseEntity.ok(toResponse(payment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<PaymentResponse> list() {
        return service.listPayments().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable String id, @RequestBody PaymentRequest request) {
        try {
            Payment p = service.updatePayment(id, request);
            return ResponseEntity.ok(toResponse(p));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setStatus(p.getStatus());
        r.setProviderTransactionId(p.getProviderTransactionId());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}

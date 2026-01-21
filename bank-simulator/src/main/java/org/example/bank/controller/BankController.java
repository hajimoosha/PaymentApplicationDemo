package org.example.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bank/api")
public class BankController {

    @PostMapping("/charge")
    public ResponseEntity<Map<String, Object>> charge(@RequestBody Map<String, Object> req) {
        Map<String, Object> resp = new HashMap<>();
        Object amountObj = req.get("amount");
        double amount = 0;
        if (amountObj instanceof Number) amount = ((Number) amountObj).doubleValue();
        else if (amountObj instanceof String) amount = Double.parseDouble((String) amountObj);

        // Simulated checks
        String account = (String) req.getOrDefault("account", "") ;
        String payerName = (String) req.getOrDefault("payerName", "");
        String payerEmail = (String) req.getOrDefault("payerEmail", "");

        // 1) Account existence check (placeholder): decline if account equals "missing"
        if ("missing".equalsIgnoreCase(account)) {
            resp.put("status", "DECLINED");
            resp.put("reason", "ACCOUNT_NOT_FOUND");
            return ResponseEntity.ok(resp);
        }

        // 2) Fraud check (placeholder): decline if payerName contains "fraud"
        if (payerName != null && payerName.toLowerCase().contains("fraud")) {
            resp.put("status", "DECLINED");
            resp.put("reason", "FRAUD_SUSPECTED");
            return ResponseEntity.ok(resp);
        }

        // 3) AML & Sanctions check (placeholder): decline if email domain is banned (example: banned.com)
        if (payerEmail != null && payerEmail.toLowerCase().endsWith("@banned.com")) {
            resp.put("status", "DECLINED");
            resp.put("reason", "AML_SANCTIONS_BLOCK");
            return ResponseEntity.ok(resp);
        }

        // 4) Business logic: deterministic approval by cents parity
        long cents = Math.round(amount * 100);
        if (cents % 2 == 0) {
            resp.put("status", "APPROVED");
            resp.put("transactionId", "bank-" + UUID.randomUUID());
            resp.put("approvedAt", System.currentTimeMillis());
            return ResponseEntity.ok(resp);
        } else {
            resp.put("status", "DECLINED");
            resp.put("transactionId", "bank-" + UUID.randomUUID());
            resp.put("reason", "INSUFFICIENT_FUNDS");
            return ResponseEntity.ok(resp);
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refund(@RequestBody Map<String, Object> req) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "REFUNDED");
        resp.put("refundId", "refund-" + UUID.randomUUID());
        return ResponseEntity.ok(resp);
    }
}

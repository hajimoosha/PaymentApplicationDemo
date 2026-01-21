Payments microservices demo

This workspace contains two Spring Boot microservices and a tiny React frontend stub:

- payments-service (port 8081)
- bank-simulator (port 8082)
- docker-compose.yml to run both services locally

Build and run (requires Docker and Maven):

- mvn -DskipTests -f payments-service/pom.xml package
- mvn -DskipTests -f bank-simulator/pom.xml package
- docker-compose up --build

Use POST /api/payments on http://localhost:8081 to create a payment. The bank simulator runs on port 8082 and deterministically approves or declines payments based on amount cents parity.

Example curl command to create a payment:

```bash
curl -X POST http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 10.00,
        "currency": "USD",
        "sourceAccount": "1234567890",
        "destinationAccount": "0987654321"
      }'
```

The response will indicate whether the payment was approved or declined by the bank simulator.

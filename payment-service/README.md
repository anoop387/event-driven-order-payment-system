# Payment Service

A Spring Boot microservice for managing payment operations in the event-driven order-payment system.

## Features

- Payment creation and management
- Payment processing workflow
- Multiple payment methods support
- Payment status tracking
- Transaction management
- RESTful API endpoints

## Payment Entity

The Payment entity includes:

- **id**: Auto-generated unique identifier
- **paymentNumber**: Unique payment identifier (auto-generated if not provided)
- **orderNumber**: Reference to the associated order
- **customerId**: Customer identifier
- **amount**: Payment amount
- **status**: Payment status (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED)
- **paymentMethod**: Payment method (CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, WALLET, CASH_ON_DELIVERY)
- **transactionId**: Gateway transaction ID
- **paymentDate**: Date when payment was completed
- **createdAt/updatedAt**: Auto-managed timestamps

## API Endpoints

### Payment CRUD Operations

- `POST /api/payments` - Create new payment
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/payment-number/{paymentNumber}` - Get payment by payment number
- `GET /api/payments` - Get all payments
- `PUT /api/payments/{id}` - Update payment
- `DELETE /api/payments/{id}` - Delete payment

### Payment Search Operations

- `GET /api/payments/order/{orderNumber}` - Get payments by order number
- `GET /api/payments/customer/{customerId}` - Get payments by customer ID
- `GET /api/payments/status/{status}` - Get payments by status
- `GET /api/payments/method/{paymentMethod}` - Get payments by payment method
- `GET /api/payments/customer/{customerId}/status/{status}` - Get payments by customer and status
- `GET /api/payments/order/{orderNumber}/status/{status}` - Get payments by order and status

### Payment Workflow Operations

- `POST /api/payments/{id}/process` - Process payment
- `POST /api/payments/{id}/complete` - Complete payment with transaction ID
- `POST /api/payments/{id}/fail` - Mark payment as failed
- `POST /api/payments/{id}/refund` - Refund payment
- `PUT /api/payments/{id}/status` - Update payment status

## Request Body Examples

### Create Payment
```json
{
  "orderNumber": "ORD-123456",
  "customerId": "CUST-001",
  "amount": 299.99,
  "paymentMethod": "CREDIT_CARD"
}
```

### Complete Payment
```json
POST /api/payments/{id}/complete?transactionId=TXN-ABC123DEF456
```

## Configuration

- **Server Port**: 8082
- **Database**: SQL Server (payment_db)
- **JPA**: Hibernate with SQL Server dialect

## Payment Workflow

1. **Create** payment with PENDING status
2. **Process** payment (changes to PROCESSING)
3. **Complete** payment with transaction ID (changes to COMPLETED)
4. **Refund** payment if needed (changes to REFUNDED)

## Validation Rules

- `orderNumber`: Cannot be blank
- `customerId`: Cannot be blank
- `amount`: Must be positive
- `paymentMethod`: Must be a valid enum value
- `paymentNumber`: Unique identifier (auto-generated if not provided)

## Error Handling

The service returns appropriate HTTP status codes:
- `201 Created` - Payment created successfully
- `200 OK` - Operation successful
- `404 Not Found` - Payment not found
- `400 Bad Request` - Validation errors
- `409 Conflict` - Duplicate payment number
- `500 Internal Server Error` - Server errors

## Running the Service

```bash
mvn spring-boot:run
```

The service will be available at `http://localhost:8082`

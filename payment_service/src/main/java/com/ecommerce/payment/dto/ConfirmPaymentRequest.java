package com.ecommerce.payment.dto;

import lombok.Data;

@Data
public class ConfirmPaymentRequest {

    private String paymentId;   // MOCK_PAY_xxx
    private String status;      // SUCCESS or FAILED
    private long amount;      // total amount
}
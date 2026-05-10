package com.ecommerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponse {
    private String razorpayOrderId;
    private long amount; // paise
    private String currency;
    private String keyId;
}
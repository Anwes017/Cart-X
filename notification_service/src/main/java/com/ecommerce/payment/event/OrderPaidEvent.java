package com.ecommerce.payment.event;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidEvent {
    private String orderId;
    private String userId;
    private double amount;
    private String email;
    private String phone;
}

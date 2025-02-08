package com.example.eventifyeventmanagment.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentConfirmRequest {

    private String paymentIntentId;
    private String paymentMethodId;
    private String ticketId;
}

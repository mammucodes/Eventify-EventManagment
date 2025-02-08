package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.stripe.exception.StripeException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
@Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public String createPayment(@RequestParam long amount, @RequestParam String currency) throws StripeException {
        return paymentService.createPaymentIntent(amount, currency);
    }
}

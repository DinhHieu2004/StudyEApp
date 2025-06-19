package com.example.myapplication.DTO.request;

public class PaymentRequest {
    private long amount;

    private String returnUrl;
    private String notifyUrl;
    public PaymentRequest(long amount) {
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }
}


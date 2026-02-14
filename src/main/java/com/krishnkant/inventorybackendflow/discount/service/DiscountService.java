package com.krishnkant.inventorybackendflow.discount.service;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    public double applyDiscount(double totalAmount) {

        if (totalAmount > 5000) {
            return totalAmount * 0.10;
        }

        return 0;
    }
}

package com.krishnkant.inventorybackendflow.discount.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DiscountService {

    public double applyDiscount(double totalAmount) {

        log.info("Calculating discount for amount={}", totalAmount);

        double discountRate = 0;

        if (totalAmount >= 10000) {
            discountRate = 0.15;
        } else if (totalAmount >= 5000) {
            discountRate = 0.10;
        } else if (totalAmount >= 1000) {
            discountRate = 0.05;
        }

        double discountAmount = totalAmount * discountRate;

        log.info("Discount applied={}, final discount amount={}",
                discountRate * 100 + "%",
                discountAmount);

        return discountAmount;
    }
}

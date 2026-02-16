package com.krishnkant.inventorybackendflow.discount.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class DiscountService {

    public BigDecimal applyDiscount(BigDecimal totalAmount) {

        log.info("Calculating discount for amount={}", totalAmount);

        BigDecimal discountRate = BigDecimal.ZERO;

        if (totalAmount.compareTo(new BigDecimal("10000")) >= 0) {
            discountRate = new BigDecimal("0.15");
        } else if (totalAmount.compareTo(new BigDecimal("5000")) >= 0) {
            discountRate = new BigDecimal("0.10");
        } else if (totalAmount.compareTo(new BigDecimal("1000")) >= 0) {
            discountRate = new BigDecimal("0.05");
        }

        BigDecimal discountAmount =
                totalAmount
                        .multiply(discountRate)
                        .setScale(2, RoundingMode.HALF_UP);

        log.info("Discount applied={}%, final discount amount={}",
                discountRate.multiply(new BigDecimal("100")),
                discountAmount);

        return discountAmount;
    }
}

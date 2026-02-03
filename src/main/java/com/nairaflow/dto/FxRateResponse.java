package com.nairaflow.dto;

import com.nairaflow.model.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FxRateResponse {

    private Wallet.Currency baseCurrency;
    private Wallet.Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal feePercentage;
    private LocalDateTime updatedAt;

    public FxRateResponse() {
    }

    public FxRateResponse(Wallet.Currency baseCurrency, Wallet.Currency targetCurrency,
            BigDecimal rate, BigDecimal feePercentage, LocalDateTime updatedAt) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.feePercentage = feePercentage;
        this.updatedAt = updatedAt;
    }

    public Wallet.Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Wallet.Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Wallet.Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Wallet.Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getFeePercentage() {
        return feePercentage;
    }

    public void setFeePercentage(BigDecimal feePercentage) {
        this.feePercentage = feePercentage;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
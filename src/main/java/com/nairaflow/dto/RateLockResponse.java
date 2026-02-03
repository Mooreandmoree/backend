package com.nairaflow.dto;

import com.nairaflow.model.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RateLockResponse {

    private String lockId;
    private Wallet.Currency baseCurrency;
    private Wallet.Currency targetCurrency;
    private BigDecimal lockedRate;
    private BigDecimal amount;
    private LocalDateTime expiresAt;
    private long secondsRemaining;

    public RateLockResponse() {
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
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

    public BigDecimal getLockedRate() {
        return lockedRate;
    }

    public void setLockedRate(BigDecimal lockedRate) {
        this.lockedRate = lockedRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(long secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }
}
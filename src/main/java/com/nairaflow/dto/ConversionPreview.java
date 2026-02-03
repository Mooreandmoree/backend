package com.nairaflow.dto;

import com.nairaflow.model.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConversionPreview {
    
    private Wallet.Currency sourceCurrency;
    private Wallet.Currency targetCurrency;
    private BigDecimal sourceAmount;
    private BigDecimal fxRate;
    private BigDecimal fee;
    private BigDecimal feePercentage;
    private BigDecimal netAmount;
    private BigDecimal convertedAmount;
    private String rateLockId;
    private LocalDateTime rateLockExpiresAt;
    
    public ConversionPreview() {}
    
    public Wallet.Currency getSourceCurrency() { return sourceCurrency; }
    public void setSourceCurrency(Wallet.Currency sourceCurrency) { this.sourceCurrency = sourceCurrency; }
    
    public Wallet.Currency getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(Wallet.Currency targetCurrency) { this.targetCurrency = targetCurrency; }
    
    public BigDecimal getSourceAmount() { return sourceAmount; }
    public void setSourceAmount(BigDecimal sourceAmount) { this.sourceAmount = sourceAmount; }
    
    public BigDecimal getFxRate() { return fxRate; }
    public void setFxRate(BigDecimal fxRate) { this.fxRate = fxRate; }
    
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    
    public BigDecimal getFeePercentage() { return feePercentage; }
    public void setFeePercentage(BigDecimal feePercentage) { this.feePercentage = feePercentage; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }
    
    public String getRateLockId() { return rateLockId; }
    public void setRateLockId(String rateLockId) { this.rateLockId = rateLockId; }
    
    public LocalDateTime getRateLockExpiresAt() { return rateLockExpiresAt; }
    public void setRateLockExpiresAt(LocalDateTime rateLockExpiresAt) { this.rateLockExpiresAt = rateLockExpiresAt; }
}
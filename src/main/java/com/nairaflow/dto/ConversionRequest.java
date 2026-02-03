package com.nairaflow.dto;

import com.nairaflow.model.Wallet;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ConversionRequest {
    
    @NotNull(message = "Source currency is required")
    private Wallet.Currency sourceCurrency;
    
    @NotNull(message = "Target currency is required")
    private Wallet.Currency targetCurrency;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum conversion is 1.00")
    private BigDecimal amount;
    
    private String rateLockId;
    private String idempotencyKey;
    
    public ConversionRequest() {}
    
    public Wallet.Currency getSourceCurrency() { return sourceCurrency; }
    public void setSourceCurrency(Wallet.Currency sourceCurrency) { this.sourceCurrency = sourceCurrency; }
    
    public Wallet.Currency getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(Wallet.Currency targetCurrency) { this.targetCurrency = targetCurrency; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getRateLockId() { return rateLockId; }
    public void setRateLockId(String rateLockId) { this.rateLockId = rateLockId; }
    
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
package com.nairaflow.dto;

import com.nairaflow.model.Wallet;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DepositRequest {
    
    @NotNull(message = "Currency is required")
    private Wallet.Currency currency;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum deposit is 1.00")
    @DecimalMax(value = "1000000.00", message = "Maximum deposit is 1,000,000.00")
    private BigDecimal amount;
    
    private String idempotencyKey;
    private String description;
    
    public DepositRequest() {}
    
    public Wallet.Currency getCurrency() { return currency; }
    public void setCurrency(Wallet.Currency currency) { this.currency = currency; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
package com.nairaflow.dto;

import com.nairaflow.model.Transaction;
import com.nairaflow.model.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    
    private Long id;
    private String referenceId;
    private Transaction.TransactionType type;
    private Transaction.TransactionStatus status;
    private Wallet.Currency sourceCurrency;
    private Wallet.Currency targetCurrency;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
    private BigDecimal fxRate;
    private BigDecimal fee;
    private BigDecimal netAmount;
    private String description;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    
    public TransactionResponse() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public Transaction.TransactionType getType() { return type; }
    public void setType(Transaction.TransactionType type) { this.type = type; }
    
    public Transaction.TransactionStatus getStatus() { return status; }
    public void setStatus(Transaction.TransactionStatus status) { this.status = status; }
    
    public Wallet.Currency getSourceCurrency() { return sourceCurrency; }
    public void setSourceCurrency(Wallet.Currency sourceCurrency) { this.sourceCurrency = sourceCurrency; }
    
    public Wallet.Currency getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(Wallet.Currency targetCurrency) { this.targetCurrency = targetCurrency; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }
    
    public BigDecimal getFxRate() { return fxRate; }
    public void setFxRate(BigDecimal fxRate) { this.fxRate = fxRate; }
    
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
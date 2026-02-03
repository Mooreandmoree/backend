package com.nairaflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String referenceId;
    
    @Column(unique = true)
    private String idempotencyKey;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wallet.Currency sourceCurrency;
    
    @Enumerated(EnumType.STRING)
    private Wallet.Currency targetCurrency;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal convertedAmount;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal fxRate;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal fee;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal netAmount;
    
    private String description;
    
    private String failureReason;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime updatedAt;
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, CONVERSION
    }
    
    public enum TransactionStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, ROLLED_BACK
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Transaction() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
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
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
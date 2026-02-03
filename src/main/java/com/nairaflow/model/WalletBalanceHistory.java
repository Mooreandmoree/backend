package com.nairaflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_balance_history")
public class WalletBalanceHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal previousBalance;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal newBalance;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal changeAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeType changeType;
    
    private String transactionReference;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum ChangeType {
        DEPOSIT, WITHDRAWAL, CONVERSION_IN, CONVERSION_OUT, FEE, LOCK, UNLOCK
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public WalletBalanceHistory() {}
    
    public WalletBalanceHistory(Wallet wallet, BigDecimal previousBalance, BigDecimal newBalance,
                                 BigDecimal changeAmount, ChangeType changeType, String transactionReference) {
        this.wallet = wallet;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.changeAmount = changeAmount;
        this.changeType = changeType;
        this.transactionReference = transactionReference;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    
    public BigDecimal getPreviousBalance() { return previousBalance; }
    public void setPreviousBalance(BigDecimal previousBalance) { this.previousBalance = previousBalance; }
    
    public BigDecimal getNewBalance() { return newBalance; }
    public void setNewBalance(BigDecimal newBalance) { this.newBalance = newBalance; }
    
    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
    
    public ChangeType getChangeType() { return changeType; }
    public void setChangeType(ChangeType changeType) { this.changeType = changeType; }
    
    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
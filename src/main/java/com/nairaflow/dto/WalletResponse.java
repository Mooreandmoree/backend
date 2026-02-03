package com.nairaflow.dto;

import com.nairaflow.model.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletResponse {
    
    private Long id;
    private Wallet.Currency currency;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal lockedBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public WalletResponse() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Wallet.Currency getCurrency() { return currency; }
    public void setCurrency(Wallet.Currency currency) { this.currency = currency; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    
    public BigDecimal getLockedBalance() { return lockedBalance; }
    public void setLockedBalance(BigDecimal lockedBalance) { this.lockedBalance = lockedBalance; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
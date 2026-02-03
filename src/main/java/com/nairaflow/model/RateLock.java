package com.nairaflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rate_locks")
public class RateLock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String lockId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wallet.Currency baseCurrency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wallet.Currency targetCurrency;
    
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal lockedRate;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !used && !isExpired();
    }
    
    public RateLock() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getLockId() { return lockId; }
    public void setLockId(String lockId) { this.lockId = lockId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Wallet.Currency getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(Wallet.Currency baseCurrency) { this.baseCurrency = baseCurrency; }
    
    public Wallet.Currency getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(Wallet.Currency targetCurrency) { this.targetCurrency = targetCurrency; }
    
    public BigDecimal getLockedRate() { return lockedRate; }
    public void setLockedRate(BigDecimal lockedRate) { this.lockedRate = lockedRate; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
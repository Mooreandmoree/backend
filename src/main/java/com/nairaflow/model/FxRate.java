package com.nairaflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fx_rates")
public class FxRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wallet.Currency baseCurrency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wallet.Currency targetCurrency;
    
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;
    
    @Column(nullable = false)
    private boolean active = true;
    
    private String updatedBy;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public FxRate() {}
    
    public FxRate(Wallet.Currency baseCurrency, Wallet.Currency targetCurrency, BigDecimal rate, String updatedBy) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.updatedBy = updatedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Wallet.Currency getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(Wallet.Currency baseCurrency) { this.baseCurrency = baseCurrency; }
    
    public Wallet.Currency getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(Wallet.Currency targetCurrency) { this.targetCurrency = targetCurrency; }
    
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}

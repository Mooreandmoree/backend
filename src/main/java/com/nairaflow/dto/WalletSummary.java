package com.nairaflow.dto;

import java.math.BigDecimal;
import java.util.List;

public class WalletSummary {
    
    private List<WalletResponse> wallets;
    private BigDecimal totalBalanceInNGN;
    private BigDecimal currentFxRate;
    
    public WalletSummary() {}
    
    public WalletSummary(List<WalletResponse> wallets, BigDecimal totalBalanceInNGN, BigDecimal currentFxRate) {
        this.wallets = wallets;
        this.totalBalanceInNGN = totalBalanceInNGN;
        this.currentFxRate = currentFxRate;
    }
    
    public List<WalletResponse> getWallets() { return wallets; }
    public void setWallets(List<WalletResponse> wallets) { this.wallets = wallets; }
    
    public BigDecimal getTotalBalanceInNGN() { return totalBalanceInNGN; }
    public void setTotalBalanceInNGN(BigDecimal totalBalanceInNGN) { this.totalBalanceInNGN = totalBalanceInNGN; }
    
    public BigDecimal getCurrentFxRate() { return currentFxRate; }
    public void setCurrentFxRate(BigDecimal currentFxRate) { this.currentFxRate = currentFxRate; }
}
package com.nairaflow.dto;

import java.math.BigDecimal;

public class DashboardStats {

    private long totalUsers;
    private long totalTransactions;
    private long pendingTransactions;
    private long completedTransactions;
    private long failedTransactions;
    private BigDecimal currentFxRate;

    public DashboardStats() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(long pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public long getCompletedTransactions() {
        return completedTransactions;
    }

    public void setCompletedTransactions(long completedTransactions) {
        this.completedTransactions = completedTransactions;
    }

    public long getFailedTransactions() {
        return failedTransactions;
    }

    public void setFailedTransactions(long failedTransactions) {
        this.failedTransactions = failedTransactions;
    }

    public BigDecimal getCurrentFxRate() {
        return currentFxRate;
    }

    public void setCurrentFxRate(BigDecimal currentFxRate) {
        this.currentFxRate = currentFxRate;
    }
}
package com.nairaflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RateHistoryResponse {

    private BigDecimal rate;
    private LocalDateTime timestamp;
    private String updatedBy;

    public RateHistoryResponse() {
    }

    public RateHistoryResponse(BigDecimal rate, LocalDateTime timestamp, String updatedBy) {
        this.rate = rate;
        this.timestamp = timestamp;
        this.updatedBy = updatedBy;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
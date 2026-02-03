package com.nairaflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UpdateRateRequest {

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "1.00", message = "Rate must be at least 1.00")
    private BigDecimal rate;

    public UpdateRateRequest() {
    }

    public UpdateRateRequest(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
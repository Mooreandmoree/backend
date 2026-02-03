package com.nairaflow.service;

import com.nairaflow.dto.FxRateResponse;
import com.nairaflow.dto.RateHistoryResponse;
import com.nairaflow.dto.RateLockResponse;
import com.nairaflow.exception.RateLockExpiredException;
import com.nairaflow.exception.ResourceNotFoundException;
import com.nairaflow.model.FxRate;
import com.nairaflow.model.RateLock;
import com.nairaflow.model.User;
import com.nairaflow.model.Wallet;
import com.nairaflow.repository.FxRateRepository;
import com.nairaflow.repository.RateLockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FxService {
    
    private final FxRateRepository fxRateRepository;
    private final RateLockRepository rateLockRepository;
    private final AuditService auditService;
    
    @Value("${fx.default-rate}")
    private BigDecimal defaultRate;
    
    @Value("${fx.fee-percentage}")
    private BigDecimal feePercentage;
    
    @Value("${fx.rate-lock-duration}")
    private int rateLockDuration;
    
    public FxService(FxRateRepository fxRateRepository, RateLockRepository rateLockRepository, AuditService auditService) {
        this.fxRateRepository = fxRateRepository;
        this.rateLockRepository = rateLockRepository;
        this.auditService = auditService;
    }
    
    public BigDecimal getCurrentRate(Wallet.Currency base, Wallet.Currency target) {
        if (base == target) {
            return BigDecimal.ONE;
        }
        
        Optional<FxRate> rateOpt = fxRateRepository.findFirstByBaseCurrencyAndTargetCurrencyAndActiveTrueOrderByCreatedAtDesc(base, target);
        
        if (rateOpt.isPresent()) {
            return rateOpt.get().getRate();
        }
        
        // Check reverse rate
        if (base == Wallet.Currency.NGN && target == Wallet.Currency.USD) {
            Optional<FxRate> reverseRate = fxRateRepository.findFirstByBaseCurrencyAndTargetCurrencyAndActiveTrueOrderByCreatedAtDesc(
                Wallet.Currency.USD, Wallet.Currency.NGN);
            if (reverseRate.isPresent()) {
                return BigDecimal.ONE.divide(reverseRate.get().getRate(), 6, RoundingMode.HALF_UP);
            }
            return BigDecimal.ONE.divide(defaultRate, 6, RoundingMode.HALF_UP);
        }
        
        return defaultRate;
    }
    
    public FxRateResponse getCurrentRateInfo() {
        Optional<FxRate> rateOpt = fxRateRepository.findFirstByBaseCurrencyAndTargetCurrencyAndActiveTrueOrderByCreatedAtDesc(
            Wallet.Currency.USD, Wallet.Currency.NGN);
        
        if (rateOpt.isPresent()) {
            FxRate rate = rateOpt.get();
            return new FxRateResponse(rate.getBaseCurrency(), rate.getTargetCurrency(), 
                rate.getRate(), feePercentage, rate.getCreatedAt());
        }
        
        return new FxRateResponse(Wallet.Currency.USD, Wallet.Currency.NGN, 
            defaultRate, feePercentage, LocalDateTime.now());
    }
    
    @Transactional
    public FxRateResponse updateRate(BigDecimal newRate, String updatedBy) {
        // Deactivate current rate
        Optional<FxRate> currentRate = fxRateRepository.findFirstByBaseCurrencyAndTargetCurrencyAndActiveTrueOrderByCreatedAtDesc(
            Wallet.Currency.USD, Wallet.Currency.NGN);
        
        currentRate.ifPresent(rate -> {
            rate.setActive(false);
            fxRateRepository.save(rate);
        });
        
        // Create new rate
        FxRate newFxRate = new FxRate(Wallet.Currency.USD, Wallet.Currency.NGN, newRate, updatedBy);
        newFxRate = fxRateRepository.save(newFxRate);
        
        auditService.log("FX_RATE_UPDATED", "FxRate", newFxRate.getId(), null, 
            currentRate.map(r -> r.getRate().toString()).orElse("N/A"), 
            newRate.toString(), null, null);
        
        return new FxRateResponse(newFxRate.getBaseCurrency(), newFxRate.getTargetCurrency(),
            newFxRate.getRate(), feePercentage, newFxRate.getCreatedAt());
    }
    
    public List<RateHistoryResponse> getRateHistory(int limit) {
        List<FxRate> rates = fxRateRepository.findRateHistory(
            Wallet.Currency.USD, Wallet.Currency.NGN, PageRequest.of(0, limit));
        
        return rates.stream()
            .map(rate -> new RateHistoryResponse(rate.getRate(), rate.getCreatedAt(), rate.getUpdatedBy()))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public String createRateLock(User user, Wallet.Currency base, Wallet.Currency target,
                                  BigDecimal rate, BigDecimal amount) {
        String lockId = UUID.randomUUID().toString();
        
        RateLock rateLock = new RateLock();
        rateLock.setLockId(lockId);
        rateLock.setUser(user);
        rateLock.setBaseCurrency(base);
        rateLock.setTargetCurrency(target);
        rateLock.setLockedRate(rate);
        rateLock.setAmount(amount);
        rateLock.setExpiresAt(LocalDateTime.now().plusSeconds(rateLockDuration));
        rateLock.setUsed(false);
        
        rateLockRepository.save(rateLock);
        
        return lockId;
    }
    
    public RateLock getRateLock(User user, String lockId) {
        RateLock rateLock = rateLockRepository.findByLockIdAndUserAndUsedFalse(lockId, user)
            .orElseThrow(() -> new ResourceNotFoundException("Rate lock not found or expired"));
        
        if (rateLock.isExpired()) {
            throw new RateLockExpiredException("Rate lock has expired");
        }
        
        return rateLock;
    }
    
    public RateLockResponse getRateLockInfo(User user, String lockId) {
        RateLock rateLock = getRateLock(user, lockId);
        long secondsRemaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), rateLock.getExpiresAt());
        
        RateLockResponse response = new RateLockResponse();
        response.setLockId(rateLock.getLockId());
        response.setBaseCurrency(rateLock.getBaseCurrency());
        response.setTargetCurrency(rateLock.getTargetCurrency());
        response.setLockedRate(rateLock.getLockedRate());
        response.setAmount(rateLock.getAmount());
        response.setExpiresAt(rateLock.getExpiresAt());
        response.setSecondsRemaining(Math.max(0, secondsRemaining));
        
        return response;
    }
    
    @Transactional
    public void useRateLock(RateLock rateLock) {
        rateLock.setUsed(true);
        rateLockRepository.save(rateLock);
    }
    
    public BigDecimal getFeePercentage() {
        return feePercentage;
    }
    
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredLocks() {
        rateLockRepository.deleteExpiredOrUsedLocks(LocalDateTime.now());
    }
}
package com.nairaflow.service;

import com.nairaflow.dto.ConversionPreview;
import com.nairaflow.dto.ConversionRequest;
import com.nairaflow.dto.DepositRequest;
import com.nairaflow.dto.TransactionResponse;
import com.nairaflow.dto.WithdrawRequest;
import com.nairaflow.exception.InvalidRequestException;
import com.nairaflow.exception.ResourceNotFoundException;
import com.nairaflow.exception.TransactionFailedException;
import com.nairaflow.model.RateLock;
import com.nairaflow.model.Transaction;
import com.nairaflow.model.User;
import com.nairaflow.model.Wallet;
import com.nairaflow.model.WalletBalanceHistory;
import com.nairaflow.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final FxService fxService;
    private final AuditService auditService;
    
    public TransactionService(TransactionRepository transactionRepository,
                              WalletService walletService,
                              FxService fxService,
                              AuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.fxService = fxService;
        this.auditService = auditService;
    }
    
    @Transactional
    public TransactionResponse deposit(User user, DepositRequest request) {
        // Check idempotency
        if (request.getIdempotencyKey() != null) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return mapToTransactionResponse(existing.get());
            }
        }
        
        String referenceId = generateReferenceId("DEP");
        
        Transaction transaction = new Transaction();
        transaction.setReferenceId(referenceId);
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
        transaction.setSourceCurrency(request.getCurrency());
        transaction.setAmount(request.getAmount());
        transaction.setNetAmount(request.getAmount());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : 
            "Simulated deposit - " + request.getCurrency());
        
        transaction = transactionRepository.save(transaction);
        
        try {
            Wallet wallet = walletService.getWalletForUpdate(user, request.getCurrency());
            walletService.creditWallet(wallet, request.getAmount(), 
                WalletBalanceHistory.ChangeType.DEPOSIT, referenceId);
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            auditService.log("DEPOSIT_COMPLETED", "Transaction", transaction.getId(), 
                user.getId(), null, "Amount: " + request.getAmount() + " " + request.getCurrency(), null, null);
            
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            
            throw new TransactionFailedException("Deposit failed: " + e.getMessage());
        }
        
        return mapToTransactionResponse(transaction);
    }
    
    @Transactional
    public TransactionResponse withdraw(User user, WithdrawRequest request) {
        // Check idempotency
        if (request.getIdempotencyKey() != null) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return mapToTransactionResponse(existing.get());
            }
        }
        
        String referenceId = generateReferenceId("WTH");
        BigDecimal fxRate = BigDecimal.ONE;
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal convertedAmount = request.getAmount();
        
        boolean requiresConversion = request.getSourceCurrency() != request.getTargetCurrency();
        
        if (requiresConversion) {
            if (request.getRateLockId() != null) {
                RateLock rateLock = fxService.getRateLock(user, request.getRateLockId());
                fxRate = rateLock.getLockedRate();
                fxService.useRateLock(rateLock);
            } else {
                fxRate = fxService.getCurrentRate(request.getSourceCurrency(), request.getTargetCurrency());
            }
            
            fee = request.getAmount().multiply(fxService.getFeePercentage().divide(BigDecimal.valueOf(100)));
            BigDecimal amountAfterFee = request.getAmount().subtract(fee);
            convertedAmount = amountAfterFee.multiply(fxRate).setScale(4, RoundingMode.HALF_UP);
        }
        
        Transaction transaction = new Transaction();
        transaction.setReferenceId(referenceId);
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
        transaction.setSourceCurrency(request.getSourceCurrency());
        transaction.setTargetCurrency(request.getTargetCurrency());
        transaction.setAmount(request.getAmount());
        transaction.setFxRate(fxRate);
        transaction.setFee(fee);
        transaction.setNetAmount(request.getAmount().subtract(fee));
        transaction.setConvertedAmount(convertedAmount);
        transaction.setDescription(request.getDescription());
        
        transaction = transactionRepository.save(transaction);
        
        try {
            Wallet sourceWallet = walletService.getWalletForUpdate(user, request.getSourceCurrency());
            walletService.debitWallet(sourceWallet, request.getAmount(),
                WalletBalanceHistory.ChangeType.WITHDRAWAL, referenceId);
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            auditService.log("WITHDRAWAL_COMPLETED", "Transaction", transaction.getId(),
                user.getId(), null, "Amount: " + request.getAmount(), null, null);
            
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            
            throw new TransactionFailedException("Withdrawal failed: " + e.getMessage());
        }
        
        return mapToTransactionResponse(transaction);
    }
    
    @Transactional
    public TransactionResponse convert(User user, ConversionRequest request) {
        // Check idempotency
        if (request.getIdempotencyKey() != null) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return mapToTransactionResponse(existing.get());
            }
        }
        
        if (request.getSourceCurrency() == request.getTargetCurrency()) {
            throw new InvalidRequestException("Source and target currencies must be different");
        }
        
        String referenceId = generateReferenceId("CNV");
        BigDecimal fxRate;
        
        if (request.getRateLockId() != null) {
            RateLock rateLock = fxService.getRateLock(user, request.getRateLockId());
            fxRate = rateLock.getLockedRate();
            fxService.useRateLock(rateLock);
        } else {
            fxRate = fxService.getCurrentRate(request.getSourceCurrency(), request.getTargetCurrency());
        }
        
        BigDecimal fee = request.getAmount().multiply(fxService.getFeePercentage().divide(BigDecimal.valueOf(100)));
        BigDecimal amountAfterFee = request.getAmount().subtract(fee);
        BigDecimal convertedAmount = amountAfterFee.multiply(fxRate).setScale(4, RoundingMode.HALF_UP);
        
        Transaction transaction = new Transaction();
        transaction.setReferenceId(referenceId);
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.CONVERSION);
        transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
        transaction.setSourceCurrency(request.getSourceCurrency());
        transaction.setTargetCurrency(request.getTargetCurrency());
        transaction.setAmount(request.getAmount());
        transaction.setFxRate(fxRate);
        transaction.setFee(fee);
        transaction.setNetAmount(amountAfterFee);
        transaction.setConvertedAmount(convertedAmount);
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Debit source wallet
            Wallet sourceWallet = walletService.getWalletForUpdate(user, request.getSourceCurrency());
            walletService.debitWallet(sourceWallet, request.getAmount(),
                WalletBalanceHistory.ChangeType.CONVERSION_OUT, referenceId);
            
            // Credit target wallet
            Wallet targetWallet = walletService.getWalletForUpdate(user, request.getTargetCurrency());
            walletService.creditWallet(targetWallet, convertedAmount,
                WalletBalanceHistory.ChangeType.CONVERSION_IN, referenceId);
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            auditService.log("CONVERSION_COMPLETED", "Transaction", transaction.getId(),
                user.getId(), null, "Converted " + request.getAmount() + " " + request.getSourceCurrency() + 
                " to " + convertedAmount + " " + request.getTargetCurrency(), null, null);
            
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            
            throw new TransactionFailedException("Conversion failed: " + e.getMessage());
        }
        
        return mapToTransactionResponse(transaction);
    }
    
    public ConversionPreview getConversionPreview(User user, Wallet.Currency source, 
                                                   Wallet.Currency target, BigDecimal amount) {
        BigDecimal fxRate = fxService.getCurrentRate(source, target);
        BigDecimal feePercentage = fxService.getFeePercentage();
        BigDecimal fee = amount.multiply(feePercentage.divide(BigDecimal.valueOf(100)));
        BigDecimal netAmount = amount.subtract(fee);
        BigDecimal convertedAmount = netAmount.multiply(fxRate).setScale(4, RoundingMode.HALF_UP);
        
        String rateLockId = fxService.createRateLock(user, source, target, fxRate, amount);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(60);
        
        ConversionPreview preview = new ConversionPreview();
        preview.setSourceCurrency(source);
        preview.setTargetCurrency(target);
        preview.setSourceAmount(amount);
        preview.setFxRate(fxRate);
        preview.setFee(fee);
        preview.setFeePercentage(feePercentage);
        preview.setNetAmount(netAmount);
        preview.setConvertedAmount(convertedAmount);
        preview.setRateLockId(rateLockId);
        preview.setRateLockExpiresAt(expiresAt);
        
        return preview;
    }
    
    public Page<TransactionResponse> getUserTransactions(User user, Pageable pageable) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user, pageable)
            .map(this::mapToTransactionResponse);
    }
    
    public Page<TransactionResponse> getUserTransactionsByType(User user, 
            Transaction.TransactionType type, Pageable pageable) {
        return transactionRepository.findByUserAndType(user, type, pageable)
            .map(this::mapToTransactionResponse);
    }
    
    public TransactionResponse getTransactionByReference(User user, String referenceId) {
        Transaction transaction = transactionRepository.findByReferenceId(referenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction not found");
        }
        
        return mapToTransactionResponse(transaction);
    }
    
    private String generateReferenceId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + 
               "-" + System.currentTimeMillis();
    }
    
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setReferenceId(transaction.getReferenceId());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setSourceCurrency(transaction.getSourceCurrency());
        response.setTargetCurrency(transaction.getTargetCurrency());
        response.setAmount(transaction.getAmount());
        response.setConvertedAmount(transaction.getConvertedAmount());
        response.setFxRate(transaction.getFxRate());
        response.setFee(transaction.getFee());
        response.setNetAmount(transaction.getNetAmount());
        response.setDescription(transaction.getDescription());
        response.setFailureReason(transaction.getFailureReason());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setCompletedAt(transaction.getCompletedAt());
        return response;
    }
}
package com.nairaflow.service;

import com.nairaflow.dto.BalanceHistoryResponse;
import com.nairaflow.dto.WalletResponse;
import com.nairaflow.dto.WalletSummary;
import com.nairaflow.exception.InsufficientBalanceException;
import com.nairaflow.exception.ResourceNotFoundException;
import com.nairaflow.model.User;
import com.nairaflow.model.Wallet;
import com.nairaflow.model.WalletBalanceHistory;
import com.nairaflow.repository.WalletBalanceHistoryRepository;
import com.nairaflow.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final WalletBalanceHistoryRepository balanceHistoryRepository;
    private final FxService fxService;
    
    public WalletService(WalletRepository walletRepository, 
                         WalletBalanceHistoryRepository balanceHistoryRepository,
                         FxService fxService) {
        this.walletRepository = walletRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
        this.fxService = fxService;
    }
    
    public List<WalletResponse> getUserWallets(User user) {
        return walletRepository.findByUser(user).stream()
            .map(this::mapToWalletResponse)
            .collect(Collectors.toList());
    }
    
    public WalletSummary getWalletSummary(User user) {
        List<Wallet> wallets = walletRepository.findByUser(user);
        List<WalletResponse> walletResponses = wallets.stream()
            .map(this::mapToWalletResponse)
            .collect(Collectors.toList());
        
        BigDecimal fxRate = fxService.getCurrentRate(Wallet.Currency.USD, Wallet.Currency.NGN);
        
        BigDecimal totalInNGN = wallets.stream()
            .map(w -> {
                if (w.getCurrency() == Wallet.Currency.NGN) {
                    return w.getBalance();
                } else {
                    return w.getBalance().multiply(fxRate);
                }
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new WalletSummary(walletResponses, totalInNGN, fxRate);
    }
    
    public Wallet getWalletByCurrency(User user, Wallet.Currency currency) {
        return walletRepository.findByUserAndCurrency(user, currency)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for currency: " + currency));
    }
    
    @Transactional
    public Wallet getWalletForUpdate(User user, Wallet.Currency currency) {
        return walletRepository.findByUserAndCurrencyForUpdate(user, currency)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for currency: " + currency));
    }
    
    @Transactional
    public void creditWallet(Wallet wallet, BigDecimal amount, 
                            WalletBalanceHistory.ChangeType changeType, String reference) {
        BigDecimal previousBalance = wallet.getBalance();
        wallet.setBalance(previousBalance.add(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);
        
        recordBalanceHistory(wallet, previousBalance, wallet.getBalance(), amount, changeType, reference);
    }
    
    @Transactional
    public void debitWallet(Wallet wallet, BigDecimal amount,
                           WalletBalanceHistory.ChangeType changeType, String reference) {
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in " + wallet.getCurrency() + " wallet");
        }
        
        BigDecimal previousBalance = wallet.getBalance();
        wallet.setBalance(previousBalance.subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        walletRepository.save(wallet);
        
        recordBalanceHistory(wallet, previousBalance, wallet.getBalance(), amount.negate(), changeType, reference);
    }
    
    public Page<BalanceHistoryResponse> getBalanceHistory(User user, Wallet.Currency currency, Pageable pageable) {
        Wallet wallet = getWalletByCurrency(user, currency);
        return balanceHistoryRepository.findByWalletOrderByCreatedAtDesc(wallet, pageable)
            .map(this::mapToBalanceHistoryResponse);
    }
    
    @Transactional
    public void createWalletsForUser(User user) {
        Wallet usdWallet = new Wallet(user, Wallet.Currency.USD);
        Wallet ngnWallet = new Wallet(user, Wallet.Currency.NGN);
        walletRepository.save(usdWallet);
        walletRepository.save(ngnWallet);
    }
    
    private void recordBalanceHistory(Wallet wallet, BigDecimal previousBalance,
                                      BigDecimal newBalance, BigDecimal changeAmount,
                                      WalletBalanceHistory.ChangeType changeType, String reference) {
        WalletBalanceHistory history = new WalletBalanceHistory(
            wallet, previousBalance, newBalance, changeAmount, changeType, reference);
        balanceHistoryRepository.save(history);
    }
    
    private WalletResponse mapToWalletResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setCurrency(wallet.getCurrency());
        response.setBalance(wallet.getBalance());
        response.setAvailableBalance(wallet.getAvailableBalance());
        response.setLockedBalance(wallet.getLockedBalance());
        response.setCreatedAt(wallet.getCreatedAt());
        response.setUpdatedAt(wallet.getUpdatedAt());
        return response;
    }
    
    private BalanceHistoryResponse mapToBalanceHistoryResponse(WalletBalanceHistory history) {
        BalanceHistoryResponse response = new BalanceHistoryResponse();
        response.setId(history.getId());
        response.setPreviousBalance(history.getPreviousBalance());
        response.setNewBalance(history.getNewBalance());
        response.setChangeAmount(history.getChangeAmount());
        response.setChangeType(history.getChangeType().name());
        response.setTransactionReference(history.getTransactionReference());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }
}

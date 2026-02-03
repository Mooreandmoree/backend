package com.nairaflow.controller;

import com.nairaflow.dto.BalanceHistoryResponse;
import com.nairaflow.dto.WalletResponse;
import com.nairaflow.dto.WalletSummary;
import com.nairaflow.model.User;
import com.nairaflow.model.Wallet;
import com.nairaflow.repository.UserRepository;
import com.nairaflow.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    
    private final WalletService walletService;
    private final UserRepository userRepository;
    
    public WalletController(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<WalletResponse>> getWallets(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(walletService.getUserWallets(user));
    }
    
    @GetMapping("/summary")
    public ResponseEntity<WalletSummary> getWalletSummary(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(walletService.getWalletSummary(user));
    }
    
    @GetMapping("/{currency}")
    public ResponseEntity<WalletResponse> getWallet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Wallet.Currency currency) {
        User user = getUser(userDetails);
        Wallet wallet = walletService.getWalletByCurrency(user, currency);
        
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setCurrency(wallet.getCurrency());
        response.setBalance(wallet.getBalance());
        response.setAvailableBalance(wallet.getAvailableBalance());
        response.setLockedBalance(wallet.getLockedBalance());
        response.setCreatedAt(wallet.getCreatedAt());
        response.setUpdatedAt(wallet.getUpdatedAt());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{currency}/history")
    public ResponseEntity<Page<BalanceHistoryResponse>> getBalanceHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Wallet.Currency currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(walletService.getBalanceHistory(user, currency, PageRequest.of(page, size)));
    }
    
    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
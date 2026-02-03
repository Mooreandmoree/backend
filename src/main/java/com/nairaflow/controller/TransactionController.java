package com.nairaflow.controller;

import com.nairaflow.dto.*;
import com.nairaflow.model.Transaction;
import com.nairaflow.model.User;
import com.nairaflow.model.Wallet;
import com.nairaflow.repository.UserRepository;
import com.nairaflow.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    
    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DepositRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.deposit(user, request));
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WithdrawRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.withdraw(user, request));
    }
    
    @PostMapping("/convert")
    public ResponseEntity<TransactionResponse> convert(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ConversionRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.convert(user, request));
    }
    
    @GetMapping("/convert/preview")
    public ResponseEntity<ConversionPreview> getConversionPreview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Wallet.Currency source,
            @RequestParam Wallet.Currency target,
            @RequestParam BigDecimal amount) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.getConversionPreview(user, source, target, amount));
    }
    
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.getUserTransactions(user, PageRequest.of(page, size)));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByType(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Transaction.TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.getUserTransactionsByType(user, type, PageRequest.of(page, size)));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String referenceId) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(transactionService.getTransactionByReference(user, referenceId));
    }
    
    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
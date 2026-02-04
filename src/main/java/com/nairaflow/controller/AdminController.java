package com.nairaflow.controller;

import com.nairaflow.dto.DashboardStats;
import com.nairaflow.dto.FxRateResponse;
import com.nairaflow.dto.RateHistoryResponse;
import com.nairaflow.dto.UpdateRateRequest;
import com.nairaflow.model.AuditLog;
import com.nairaflow.model.Transaction;
import com.nairaflow.repository.TransactionRepository;
import com.nairaflow.repository.UserRepository;
import com.nairaflow.service.AuditService;
import com.nairaflow.service.FxService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FxService fxService;
    private final AuditService auditService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AdminController(FxService fxService,
            AuditService auditService,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {
        this.fxService = fxService;
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @PutMapping("/fx/rate")
    public ResponseEntity<FxRateResponse> updateFxRate(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateRateRequest request) {
        return ResponseEntity.ok(fxService.updateRate(request.getRate(), userDetails.getUsername()));
    }

    @GetMapping("/fx/rate/history")
    public ResponseEntity<List<RateHistoryResponse>> getRateHistory(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(fxService.getRateHistory(limit));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalTransactions(transactionRepository.count());
        // You can add more stats as needed
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/audit/logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(auditService.getAuditLogs(pageRequest));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(transactionRepository.findAll(pageRequest));
    }
}

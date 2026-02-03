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

}

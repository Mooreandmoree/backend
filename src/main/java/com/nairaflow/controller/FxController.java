package com.nairaflow.controller;

import com.nairaflow.dto.FxRateResponse;
import com.nairaflow.dto.RateHistoryResponse;
import com.nairaflow.dto.RateLockResponse;
import com.nairaflow.model.User;
import com.nairaflow.repository.UserRepository;
import com.nairaflow.service.FxService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fx")
public class FxController {
    
    private final FxService fxService;
    private final UserRepository userRepository;
    
    public FxController(FxService fxService, UserRepository userRepository) {
        this.fxService = fxService;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/current")
    public ResponseEntity<FxRateResponse> getCurrentRate() {
        return ResponseEntity.ok(fxService.getCurrentRateInfo());
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<RateHistoryResponse>> getRateHistory(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(fxService.getRateHistory(limit));
    }
    
    @GetMapping("/lock/{lockId}")
    public ResponseEntity<RateLockResponse> getRateLock(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String lockId) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(fxService.getRateLockInfo(user, lockId));
    }
    
    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
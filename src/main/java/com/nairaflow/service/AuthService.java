package com.nairaflow.service;

import com.nairaflow.dto.AuthResponse;
import com.nairaflow.dto.AuthRequest;
import com.nairaflow.dto.RegisterRequest;
import com.nairaflow.exception.DuplicateResourceException;
import com.nairaflow.model.User;
import com.nairaflow.repository.UserRepository;
import com.nairaflow.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final WalletService walletService;
    private final AuditService auditService;
    
    public AuthService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuthenticationManager authenticationManager,
                       WalletService walletService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.walletService = walletService;
        this.auditService = auditService;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        User user = new User(
            request.getFullName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            User.Role.USER
        );
        user.setKycVerified(true); // Soft KYC - auto-verified
        
        user = userRepository.save(user);
        
        // Create USD and NGN wallets
        walletService.createWalletsForUser(user);
        
        String token = tokenProvider.generateToken(user.getEmail());
        
        auditService.log("USER_REGISTERED", "User", user.getId(), user.getId(), 
            null, "New user registered: " + user.getEmail(), null, null);
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = tokenProvider.generateToken(authentication);
        
        auditService.log("USER_LOGIN", "User", user.getId(), user.getId(),
            null, "User logged in", null, null);
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
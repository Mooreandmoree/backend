package com.nairaflow.repository;

import com.nairaflow.model.Wallet;
import com.nairaflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUser(User user);
    
    Optional<Wallet> findByUserAndCurrency(User user, Wallet.Currency currency);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user = :user AND w.currency = :currency")
    Optional<Wallet> findByUserAndCurrencyForUpdate(
        @Param("user") User user, 
        @Param("currency") Wallet.Currency currency
    );
}
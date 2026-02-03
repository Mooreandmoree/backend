package com.nairaflow.repository;

import com.nairaflow.model.FxRate;
import com.nairaflow.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FxRateRepository extends JpaRepository<FxRate, Long> {
    
    Optional<FxRate> findFirstByBaseCurrencyAndTargetCurrencyAndActiveTrueOrderByCreatedAtDesc(
        Wallet.Currency baseCurrency, 
        Wallet.Currency targetCurrency
    );
    
    @Query("SELECT f FROM FxRate f WHERE f.baseCurrency = :base AND f.targetCurrency = :target ORDER BY f.createdAt DESC")
    List<FxRate> findRateHistory(
        @Param("base") Wallet.Currency base,
        @Param("target") Wallet.Currency target,
        Pageable pageable
    );
}
package com.nairaflow.repository;

import com.nairaflow.model.WalletBalanceHistory;
import com.nairaflow.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletBalanceHistoryRepository extends JpaRepository<WalletBalanceHistory, Long> {
    Page<WalletBalanceHistory> findByWalletOrderByCreatedAtDesc(Wallet wallet, Pageable pageable);
}
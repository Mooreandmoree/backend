package com.nairaflow.repository;

import com.nairaflow.model.Transaction;
import com.nairaflow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    Optional<Transaction> findByReferenceId(String referenceId);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.type = :type ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserAndType(
            @Param("user") User user,
            @Param("type") Transaction.TransactionType type,
            Pageable pageable);

    long countByStatus(Transaction.TransactionStatus status);
}
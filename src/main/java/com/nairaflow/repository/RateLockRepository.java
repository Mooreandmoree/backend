package com.nairaflow.repository;

import com.nairaflow.model.RateLock;
import com.nairaflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RateLockRepository extends JpaRepository<RateLock, Long> {
    
    Optional<RateLock> findByLockIdAndUserAndUsedFalse(String lockId, User user);
    
    @Modifying
    @Query("DELETE FROM RateLock r WHERE r.expiresAt < :now OR r.used = true")
    void deleteExpiredOrUsedLocks(@Param("now") LocalDateTime now);
}

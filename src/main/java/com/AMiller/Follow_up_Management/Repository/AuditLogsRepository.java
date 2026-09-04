package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.AuditLogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface AuditLogsRepository  extends JpaRepository <AuditLogs, Integer> {
    Page<AuditLogs> findByUserId_UserName(String userName, Pageable pageable);

    Page<AuditLogs> findByAction(String action,Pageable pageable);

    Page<AuditLogs> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end,Pageable pageable);
}

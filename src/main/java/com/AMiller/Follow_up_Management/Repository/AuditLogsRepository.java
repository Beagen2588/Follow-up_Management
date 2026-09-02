package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.AuditLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface AuditLogsRepository  extends JpaRepository <AuditLogs, Integer> {
    List<AuditLogs> findByUserId_UserName(String userName);

    List<AuditLogs> findByAction(String action);

    List<AuditLogs> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}

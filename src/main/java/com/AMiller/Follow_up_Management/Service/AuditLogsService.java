package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.AuditLogsRepository;
import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.AuditLogs;
import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuditLogsService {

    private final UsersRepository usersRepository;
    private final AuditLogsRepository auditLogsRepository;
    public AuditLogsService(AuditLogsRepository auditLogsRepository, UsersRepository usersRepository) {
        this.auditLogsRepository = auditLogsRepository;
        this.usersRepository = usersRepository;
    }

    public Page<AuditLogs> findAll(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending());
        return auditLogsRepository.findAll(pageable);
    }

    public AuditLogs addAuditLog(
            int userId,
            String entityType,
            int entityId,
            String action,
            String reason) {

        Optional<Users> optionalUser = usersRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        AuditLogs auditLog = new AuditLogs();

        auditLog.setUserId(optionalUser.get());
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setCreatedAt(LocalDateTime.now());

        return auditLogsRepository.save(auditLog);
    }

    public AuditLogs findById(int id) {
        Optional<AuditLogs> existingLog = auditLogsRepository.findById(id);
        if (existingLog.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Audit Log not found"
            );
        }
        return existingLog.get();
    }

    public Page<AuditLogs> searchByUser(String userName, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return auditLogsRepository.findByUserId_UserName(userName,pageable);
    }

    public Page<AuditLogs> searchByAction(String action,int page, int size) {


        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return auditLogsRepository.findByAction(action,pageable);
    }

    public Page<AuditLogs> searchByDate(LocalDateTime start, LocalDateTime end, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return auditLogsRepository.findByCreatedAtBetween(start, end,pageable);
    }


}

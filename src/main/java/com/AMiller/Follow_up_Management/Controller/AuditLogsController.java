package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.DTO.AuditLogsDTO;
import com.AMiller.Follow_up_Management.Repository.AuditLogsRepository;
import com.AMiller.Follow_up_Management.Service.AuditLogsService;
import com.AMiller.Follow_up_Management.entities.AuditLogs;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogsController {

    private AuditLogsService auditLogsService;

    public AuditLogsController(AuditLogsService auditLogsService) {
        this.auditLogsService = auditLogsService;

    }

    @GetMapping
    public List<AuditLogs> findAllLogs() {
        return auditLogsService.findAll();
    }

    @GetMapping("/{id}")
    public AuditLogs findById(@PathVariable int id) {
        return auditLogsService.findById(id);
    }

    @GetMapping("/search/user")
    public List<AuditLogs> searchByUser(@RequestParam String userName) {
        return auditLogsService.searchByUser(userName);
    }

    @GetMapping("/search/action")
    public List<AuditLogs> searchByAction(@RequestParam String action) {
        return auditLogsService.searchByAction(action);
    }

    @GetMapping("/search/date")
    public List<AuditLogs> searchByDate(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return auditLogsService.searchByDate(start,end);
    }



}

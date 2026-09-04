package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.DTO.AuditLogsDTO;
import com.AMiller.Follow_up_Management.Repository.AuditLogsRepository;
import com.AMiller.Follow_up_Management.Service.AuditLogsService;
import com.AMiller.Follow_up_Management.entities.AuditLogs;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


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
    public Page<AuditLogs> findAllLogs(@RequestParam (defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size ) {
        return auditLogsService.findAll(page,size);
    }

    @GetMapping("/{id}")
    public AuditLogs findById(@PathVariable int id) {
        return auditLogsService.findById(id);
    }

    @GetMapping("/search/user")
    public Page<AuditLogs> searchByUser(@RequestParam String userName,@RequestParam (defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size ) {
        return auditLogsService.searchByUser(userName,page,size);
    }

    @GetMapping("/search/action")
    public Page<AuditLogs> searchByAction(@RequestParam String action,@RequestParam (defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size ) {
        return auditLogsService.searchByAction(action,page,size);
    }

    @GetMapping("/search/date")
    public Page<AuditLogs> searchByDate(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end,@RequestParam (defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size ) {
        return auditLogsService.searchByDate(start,end,page,size);
    }



}

package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.DTO.FollowUpRequestsDTO;
import com.AMiller.Follow_up_Management.Repository.FollowUpRequestsRepository;
import com.AMiller.Follow_up_Management.Service.FollowUpRequestsService;
import com.AMiller.Follow_up_Management.entities.FollowUpRequests;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/follow-up-requests")
public class FollowUpRequestsController {

    private final FollowUpRequestsService followUpRequestsService;
    public FollowUpRequestsController(FollowUpRequestsService followUpRequestsService) {
        this.followUpRequestsService = followUpRequestsService;
    }

    @GetMapping
    public List<FollowUpRequests> getAllFollowUpRequests() {
        return followUpRequestsService.getAllFollowUpRequests();

    }

    @PostMapping
    public FollowUpRequests addFollowUpRequests(@RequestBody FollowUpRequestsDTO requestsDTO, Authentication authentication) {



        return followUpRequestsService.addFollowUpRequest(authentication.getName(),
                requestsDTO.getPatientId(), requestsDTO.getDepartmentId(), requestsDTO.getReason(), requestsDTO.getDueDate(),
                requestsDTO.getStatus(), requestsDTO.getNotes()
        );

    }
    @PutMapping("/{id}")
    public FollowUpRequests updateFollowUpRequests(@PathVariable int id, Authentication authentication, @RequestBody FollowUpRequestsDTO requestsDTO) {
        return followUpRequestsService.updateFollowUpRequest(id ,authentication.getName(),requestsDTO.getPatientId(), requestsDTO.getDepartmentId(),
                requestsDTO.getReason(), requestsDTO.getDueDate(),
                requestsDTO.getStatus(), requestsDTO.getNotes());

    }
    @DeleteMapping("/{id}")
    public void deleteFollowUpRequest(@PathVariable int id, Authentication authentication) {
        followUpRequestsService.deleteFollowUpRequest(id, authentication.getName());

    }

    @GetMapping("/search")
    public List<FollowUpRequests> searchFollowUpRequests(@RequestParam String status){
        return followUpRequestsService.searchFollowUpRequests(status);
    }

    @GetMapping("/{id}")
    public FollowUpRequests getRequestById(@PathVariable int id){
        return followUpRequestsService.findById(id);
    }

    @PutMapping("/{id}/status")
    public FollowUpRequests updateRequestStatus(@PathVariable int id, @RequestParam String status, Authentication authentication){
        return followUpRequestsService.updateStatus(id, authentication.getName(),status);
    }

}




package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.DTO.CommunicationDTO;
import com.AMiller.Follow_up_Management.Service.CommunicationsService;
import com.AMiller.Follow_up_Management.entities.Communication;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {

    private final CommunicationsService communicationsService;

    public CommunicationController(CommunicationsService communicationsService) {
        this.communicationsService = communicationsService;

    }

    @GetMapping
    public List<Communication> getAllCommunications() {
        return communicationsService.findAllCommunications();
    }

    @PostMapping
    public Communication addCommunication(@RequestBody CommunicationDTO communicationDTO,Authentication authentication) {
        System.out.println("COMMUNICATION CONTROLLER REACHED");
        return communicationsService.addCommunication(communicationDTO.getFollowUpId(),communicationDTO.getTemplateId(),
                authentication.getName(), communicationDTO.getStatus());

    }

    @PutMapping("/{id}")
    public Communication updateCommunication(@PathVariable int id, @RequestBody CommunicationDTO  communicationDTO,Authentication authentication) {
        return communicationsService.updateCommunication(id,communicationDTO.getFollowUpId(),communicationDTO.getTemplateId(),
                authentication.getName(),
                communicationDTO.getStatus());

    }
    @DeleteMapping("/{id}")
    public void deleteCommunication(@PathVariable int id, Authentication authentication) {
        communicationsService.deleteCommunication(id, authentication.getName());
    }

    @GetMapping("/search")
    public List<Communication> searchCommunications(@RequestParam String lastName) {
        return communicationsService.findByPatientName(lastName);
    }
}


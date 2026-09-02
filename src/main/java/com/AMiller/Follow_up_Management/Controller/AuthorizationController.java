package com.AMiller.Follow_up_Management.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {

    @GetMapping("/me")
    public Map<String,String> getCurrentUser(Authentication authentication) {

        String role = authentication.getAuthorities().stream().map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst().orElse("ROLE_UNKNOWN");

        return Map.of("username", authentication.getName(),
                       "role", role);
    }

}

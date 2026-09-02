package com.AMiller.Follow_up_Management.Config;


import com.AMiller.Follow_up_Management.Service.UserLogInService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login.html",
                                "/login.js",
                                "/styles.css",
                                "/images/**"

                        ).permitAll()

                        .requestMatchers("/api/audit-logs/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,"/api/email-templates/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,"/api/email-templates/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,"/api/email-templates/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,"/api/email-templates/**")
                        .authenticated()

                        .requestMatchers(
                                "/templates.html",
                                "/audit.html"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html")
                        .permitAll()
                )
                .build();

        }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }
    @Bean
    public AuthenticationProvider authenticationProvider(UserLogInService userLogInService, PasswordEncoder passwordEncoder){

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userLogInService);

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;

    }
}

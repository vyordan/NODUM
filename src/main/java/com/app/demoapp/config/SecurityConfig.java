package com.app.demoapp.config;

import com.app.demoapp.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
               .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos — SIEMPRE primero
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/error",
                    "/error/**"
                ).permitAll()

                // Imágenes servidas desde controller
                .requestMatchers("/imagenes/**").permitAll()

                // Rutas públicas
                .requestMatchers("/").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/trabajos").permitAll()
                .requestMatchers("/trabajos/{id}").permitAll()
                .requestMatchers("/perfil/{id}").permitAll()

                // Solo admin
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Solo empleador
                .requestMatchers(
                    "/trabajos/nuevo",
                    "/trabajos/editar/**",
                    "/trabajos/mis-trabajos",
                    "/trabajos/*/estado",
                    "/trabajos/*/postulaciones",
                    "/postulaciones/*/rechazar",
                    "/contratos/aceptar/**"
                ).hasRole("EMPLEADOR")

                // Solo trabajador
                .requestMatchers(
                    "/mis-postulaciones",
                    "/trabajos/*/postular",
                    "/postulaciones/*/retirar"
                ).hasRole("TRABAJADOR")

                // Cualquier usuario autenticado
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .sessionManagement(session -> session
                .maximumSessions(5)
                .expiredUrl("/auth/login?expired=true")
            );

        return http.build();
    }
}
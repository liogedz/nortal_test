package com.nortal.library.api.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Value("${library.security.enforce:true}")
    private boolean enforceSecurity;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults());

        if (enforceSecurity) {
            http.authorizeHttpRequests(
                            auth ->
                                    auth.requestMatchers("/api/health")
                                            .permitAll()
                                            .requestMatchers(HttpMethod.GET,
                                                    "/api/**")
                                            .permitAll()
                                            .anyRequest()
                                            .authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                List.of("http://localhost:4200",
                        "http://localhost:8080",
                        "*"));
        configuration.setAllowedMethods(List.of("GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",
                configuration);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() throws Exception {
        RSAPublicKey publicKey = loadPublicKey(PUBLIC_KEY_PEM);
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private RSAPublicKey loadPublicKey(String pem) throws Exception {
        String normalized =
                pem.replace("-----BEGIN PUBLIC KEY-----",
                                "")
                        .replace("-----END PUBLIC KEY-----",
                                "")
                        .replaceAll("\\s",
                                "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private static final String PUBLIC_KEY_PEM = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3stATF7kEbQsIoGbY6AJ
            pnB10hmla/yhKoI7Qs7oYB+oQVv2tTynlvj4snSas2eZNKx/b+WNYZrgSN0mIdF7
            OxPGnvsp7W3XB8lLpPt3+bRiTCXAMiPvXJZqaMl34EmYmrJKAosAEqheuFQnp9IN
            +1RftioV2Rjt+2yply1vprNqODwGp3vBPfsxLe9ZSGSIQAGv51nVzRdFr8c6qZSy
            6fHMRfkjGiWjD6WLdlgRQ8VX1YlG2WBOIqCd7OjHXLK3s5ITJQwECf1h4E2yXDDa
            Ld5vHUMHG2zalSk1qje0T/AUltjU+qh4GdsWzwLvCILPFl27tbAleXUZoVnyQzE4
            bQIDAQAB
            -----END PUBLIC KEY-----
            """;
}

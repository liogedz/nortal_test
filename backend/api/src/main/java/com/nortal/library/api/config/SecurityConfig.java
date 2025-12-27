package com.nortal.library.api.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${library.security.enforce:true}")
    private boolean enforceSecurity;

    @Value("${library.security.jwt.public-key-location}")
    private Resource publicKey;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());

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
        if (enforceSecurity) {
            RSAPublicKey key = loadPublicKey(readPem(publicKey));
            return NimbusJwtDecoder.withPublicKey(key).build();
        } else {
            log.warn("Public key not found, skipping JwtDecoder creation");
            return null;
        }
    }

    private String readPem(Resource resource) throws IOException {
        return new String(resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
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
}

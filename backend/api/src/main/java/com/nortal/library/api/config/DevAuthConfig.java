package com.nortal.library.api.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class DevAuthConfig {
    private static final Logger log = LoggerFactory.getLogger(DevAuthConfig.class);

    @Value("${library.security.print-demo-token:false}")
    private boolean printDemoToken;

    public static String DEMO_JWT;

    @Value("${library.security.jwt.public-key-location}")
    private Resource publicKeyResource;

    @Value("${library.security.jwt.private-key-location}")
    private Resource privateKeyResource;

    @Bean
    CommandLineRunner demoTokenPrinter() {
        return args -> {
            if (!printDemoToken) {
                return;
            }
            try {
                RSAPublicKey publicKey = toPublicKey(readPem(publicKeyResource));
                RSAPrivateKey privateKey = toPrivateKey(readPem(privateKeyResource));
                RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
                JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
                Instant now = Instant.now();
                JwtClaimsSet claims =
                        JwtClaimsSet.builder()
                                .subject("m1")
                                .issuer("dev-local")
                                .issuedAt(now)
                                .expiresAt(now.plusSeconds(3600))
                                .build();
                String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                DEMO_JWT = token;
                log.info("Demo JWT (Bearer): {}",
                        token);
            } catch (Exception e) {
                log.warn("Could not generate demo token: {}",
                        e.getMessage());
            }
        };
    }

    private RSAPublicKey toPublicKey(String pem) throws Exception {
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

    private RSAPrivateKey toPrivateKey(String pem) throws Exception {
        String normalized =
                pem.replace("-----BEGIN PRIVATE KEY-----",
                                "")
                        .replace("-----END PRIVATE KEY-----",
                                "")
                        .replaceAll("\\s",
                                "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    private String readPem(Resource resource) throws IOException {
        return new String(resource.getContentAsByteArray(),
                StandardCharsets.UTF_8);
    }
}

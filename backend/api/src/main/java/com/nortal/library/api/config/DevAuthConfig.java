package com.nortal.library.api.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

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
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class DevAuthConfig {
    private static final Logger log = LoggerFactory.getLogger(DevAuthConfig.class);

    @Value("${library.security.print-demo-token:false}")
    private boolean printDemoToken;

    @Bean
    CommandLineRunner demoTokenPrinter() {
        return args -> {
            if (!printDemoToken) {
                return;
            }
            try {
                RSAPublicKey publicKey = toPublicKey(PUBLIC_KEY_PEM);
                RSAPrivateKey privateKey = toPrivateKey(PRIVATE_KEY_PEM);
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

    private static final String PRIVATE_KEY_PEM = """
            -----BEGIN PRIVATE KEY-----
            MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDey0BMXuQRtCwi
            gZtjoAmmcHXSGaVr/KEqgjtCzuhgH6hBW/a1PKeW+PiydJqzZ5k0rH9v5Y1hmuBI
            3SYh0Xs7E8ae+yntbdcHyUuk+3f5tGJMJcAyI+9clmpoyXfgSZiaskoCiwASqF64
            VCen0g37VF+2KhXZGO37bKmXLW+ms2o4PAane8E9+zEt71lIZIhAAa/nWdXNF0Wv
            xzqplLLp8cxF+SMaJaMPpYt2WBFDxVfViUbZYE4ioJ3s6MdcsrezkhMlDAQJ/WHg
            TbJcMNot3m8dQwcbbNqVKTWqN7RP8BSW2NT6qHgZ2xbPAu8Igs8WXbu1sCV5dRmh
            WfJDMThtAgMBAAECggEAHmmz9gjpDMKh2LcFEYE1+Aa3iq3uDzLuBDnBYbIRdhe0
            NfHaGGz2eq0zc9bnjFB60T4v6kQ1e8qyzbeEnk60igC91xN1qn/ZP+qaXSPnwYo+
            deMcXKvbn7G3xmSkLNWXE5z9beHwKVvbDEIOpMHPn0yqARm3IErfJ7hyYRz2dhSc
            Em9xq0AMaZVsuP5KitiVUsgJ5wGaI7yBb0UgPgsHP1PoHX540TpozCqKcBAYiX7S
            Wzr1+XVoHGRAY5s0Tn12luCdjoNMf3qiwPmU1MK3ElPvLOx1SvWMPPb/J0niIMFM
            laUcmIqiFCYWUdYP6gUd/RJw3Zsbo2ijDDf5A5DZIQKBgQD2SM4s/pKyaS/5cVSH
            SJT54yDiRh0bYe/iH+7t27MR2Lnrmc6x0sW1iaHiTY/3EvOhzlsRN3Rkq+FyfdtG
            UmuOzBf33WmJILMAdr+Jop6J+N+5+Vu03NeFsMgFN3BFZiWZM2JJOLXhJejCwlJw
            UxwH5MhOscqNH17nAAzon4/hYQKBgQDnlTgB6oE8oFUOY9FwVpyfg7EPePJaTeP5
            MqvwUInWDXyZWC82XHqs30NVhysbyIyF/RH5f+pks5u+NLmQPl8jHTmcBhLsfe+v
            R1YJH68elGJPcgUIOAydvT+kO2+X/JJksrqmlIHOVkV08Bz9cY+OAfZvAoKAF7+b
            5ySrR2vWjQKBgQCDBkS5503AIPnm6QYhWtn2/4DVIJwHn1jxoi+I16My0WxIDXHL
            ZOjOJcS8EquOtMRsxs3oIOqJTHAKay6nAN48ABSYR3EIBR92Fbbc0Gkr2f2cgS7q
            z7rRYzVmoRHXufoywQV/Eu6gM3zbcGpPW8fD41E1nJy364KfvoUflRQEwQKBgAzR
            6ePRQ45DofHF/NYnNUxvUeH1ZBUzsqcc8v++taKv5HHou9Rakj/3rBaUAQLsuzq/
            o7sYJbWla72/1XXyvfmHqKTGgU2uOxKM9GpU4rDirf6P5U9rKuegjmFdGPRk+wWw
            Dz0hg34UsIukrzYojzXhTe8fSHIm3miXBySkM4gRAoGADG+WvgD4p9TAUai9X+vn
            i6bCEL76aGR6mVvBhbTkWhxjOb2pFN5vBgxmOyH8GakgLC03j7hyBlCDCnhOeUhe
            DZUFxH0KVIOQh4Y40Il509bTzKtrZT5sIZUFeclrcODgb4CuFaXlU8T2YcBg/pov
            Rgael6n33wC1p/KkSV7TfnA=
            -----END PRIVATE KEY-----
            """;
}

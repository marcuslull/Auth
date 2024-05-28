package com.marcuslull.auth.configurations;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Slf4j
@Configuration
public class TokenConfiguration {

    private final static String KEYPAIR_ALGORITHM = "RSA";
    private final static int INITIALIZATION_SIZE = 2048;

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        log.info("AUTH_START: TokenConfiguration.jwtDecoder()");
        // Spring needs to know how
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        log.info("AUTH_START: TokenConfiguration.jwkSource()");
        // Spring needs to know where/how we get our signing keys for the JWT
        // TODO: This needs a permanent key source, currently a new pair every reboot for development only
        KeyPair keyPair = generateRsaKey(); // generate a key pair
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // extract the public key from the key pair
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // extract the private key from the key pair
        RSAKey rsaKey = new RSAKey.Builder(publicKey) // build the RSA key from the public/private
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey); // convert it to a JWK set
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        // helper for jwkSource()
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEYPAIR_ALGORITHM);
            keyPairGenerator.initialize(INITIALIZATION_SIZE);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + KEYPAIR_ALGORITHM, e);
        } catch (InvalidParameterException e) {
            throw new RuntimeException("Invalid key size: " + INITIALIZATION_SIZE, e);
        }
        log.warn("AUTH_JWT: TokenConfiguration.generateRsaKey() - new RSA keypair generated");
        return keyPair;
    }
}

package cl.nutrivida.usuarios.config;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;

import java.nio.file.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class TokenConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtEncoder encoder(
            @Value("${app.jwt.private-key}") String privateFile,
            @Value("${app.jwt.public-key}") String publicFile)
            throws Exception {
        RSAPrivateKey privateKey;
        RSAPublicKey publicKey;
        try (var in = Files.newInputStream(Path.of(privateFile))) {
            privateKey = RsaKeyConverters.pkcs8().convert(in);
        }
        try (var in = Files.newInputStream(Path.of(publicFile))) {
            publicKey = RsaKeyConverters.x509().convert(in);
        }
        var key = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("nutrivida-1").build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
    }
}

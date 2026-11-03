package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expirationMs) {
        this.signingKey = createSigningKey(secret);
        this.expirationMs = expirationMs;
    }

    public String generarToken(Long userId, String email, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Claims parsearToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esValido(String token) {
        try {
            parsearToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static SecretKey createSigningKey(String secret) {
        if (secret == null || secret.isBlank()) {
            secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        }
        byte[] keyBytes;
        if (isHexString(secret) && secret.length() >= 64 && secret.length() % 2 == 0) {
            keyBytes = hexToBytes(secret);
        } else {
            try {
                byte[] decoded = Base64.getDecoder().decode(secret);
                if (decoded.length >= 32) {
                    keyBytes = decoded;
                } else {
                    keyBytes = hashTo256Bits(secret);
                }
            } catch (IllegalArgumentException e) {
                keyBytes = hashTo256Bits(secret);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static boolean isHexString(String s) {
        return s != null && s.matches("^[0-9a-fA-F]+$");
    }

    private static byte[] hashTo256Bits(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}

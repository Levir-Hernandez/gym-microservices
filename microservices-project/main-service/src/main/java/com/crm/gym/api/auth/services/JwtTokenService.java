package com.crm.gym.api.auth.services;

import com.crm.gym.api.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService
{
    private SecretKey signingKey;
    private Long accessTokenExpirationMillis;
    private Long refreshTokenExpirationMillis;

    public JwtTokenService(
            @Value("${auth.jwt.secret-key}") String secretKey,
            @Value("${auth.jwt.access-token-expiration}") Long accessTokenExpiration,
            @Value("${auth.jwt.refresh-token-expiration}") Long refreshTokenExpiration
    )
    {
        this.signingKey = toSigningKey(secretKey);
        this.accessTokenExpirationMillis = accessTokenExpiration*1000;
        this.refreshTokenExpirationMillis = refreshTokenExpiration*1000;
    }

    public String issueAccessToken(String username, String role)
    {
        return buildToken(username, role, "access", accessTokenExpirationMillis);
    }

    public String issueRefreshToken(String username, String role)
    {
        return buildToken(username, role, "refresh", refreshTokenExpirationMillis);
    }

    public Claims decodeToken(String token)
    {
        try
        {
            return Jwts
                    .parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (JwtException ex) {throw new InvalidTokenException(ex);}
    }

    private SecretKey toSigningKey(String secretKey)
    {
        if(secretKey.isEmpty()){return Jwts.SIG.HS256.key().build();}

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(String subject, String role, String tokenType, Long tokenExpirationMillis)
    {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .claim("role", role)
                .claim("type", tokenType)
                .issuedAt(new Date(currentTimeMillis))
                .expiration(new Date(currentTimeMillis+tokenExpirationMillis))
                .signWith(signingKey)
                .compact();
    }
}

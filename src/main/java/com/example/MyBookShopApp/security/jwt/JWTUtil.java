package com.example.MyBookShopApp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;


@Service
public class JWTUtil {

    @Value("${auth.secret}")
    private String secret;

    @Value("${auth.expirationTimeInSeconds}")
    private Integer expirationTime;

    private List<String> authorizedTokens = new CopyOnWriteArrayList<>();

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + this.expirationTime * 1000))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername());
        for (String expiredToken : authorizedTokens) {
            try {
                if (isTokenExpired(expiredToken)) {
                    authorizedTokens.removeIf(s -> authorizedTokens.contains(expiredToken));
                }
            } catch (ExpiredJwtException e) {
                authorizedTokens.removeIf(s -> authorizedTokens.contains(expiredToken));
            }
        }
        authorizedTokens.add(token);
        return token;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExparation(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExparation(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token) && authorizedTokens.contains(token);
    }

    public void deleteToken(String token) {
        authorizedTokens.removeIf(s -> authorizedTokens.contains(token));
    }

}
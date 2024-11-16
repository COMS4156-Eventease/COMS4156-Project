package com.eventease.eventease_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  /**
   * Generates a JWT token for the given username and role
   * @param username The username
   * @param role The user's role
   * @return JWT token string
   */
  public String generateToken(String username, String role) {
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    return Jwts.builder()
        .subject(username)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
//        .signWith(key, SignatureAlgorithm.HS512)
        .signWith(key, Jwts.SIG.HS512)
        .compact();
  }

  /**
   * Validates the JWT token and returns the claims
   * @param token The JWT token to validate
   * @return Claims if valid
   * @throws JwtException if token is invalid
   */
  public Claims validateTokenAndGetClaims(String token) {
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Validates the JWT token
   * @param token The JWT token to validate
   * @return true if valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Extracts username from token
   * @param token The JWT token
   * @return username
   */
  public String getUsername(String token) {
    return validateTokenAndGetClaims(token).getSubject();
  }

  /**
   * Extracts role from token
   * @param token The JWT token
   * @return role
   */
  public String getRole(String token) {
    return validateTokenAndGetClaims(token).get("role", String.class);
  }
}

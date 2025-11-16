//package com.example.demo;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//
//    private static final String SECRET_STRING = "my_super_strong_secret_key_of_32_bytes!";
//    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
//
//    private static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 10; // 10 hours
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public boolean isTokenValid(String token, String username) {
//        return username.equals(extractUsername(token)) && !isTokenExpired(token);
//    }
//
//    public String generateToken(String username) {
//        return createToken(new HashMap<>(), username);
//    }
//
//    private String createToken(Map<String, Object> claims, String username) {
//        Date now = new Date();
//        Date expiry = new Date(now.getTime() + TOKEN_VALIDITY);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiry)
//                .signWith(SECRET_KEY)
//                .compact();
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(SECRET_KEY)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractAllClaims(token).getExpiration().before(new Date());
//    }
//}

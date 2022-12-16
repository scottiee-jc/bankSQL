package com.example.da_bank.auth;

import com.example.da_bank.models.AccountUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    public String generateAccessToken(AccountUser accountUser) {
        byte[] keyBytes = Decoders.BASE64.decode((SECRET_KEY));
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .setSubject(String.format("%s,%s", accountUser.getId(), accountUser.getUsername()))
                .setIssuer("CodeJava")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

//     Here, the generateAccessToken() method creates a JSON Web Token with the following details:
// ---- Subject is combination of the user’s ID and email, separated by a comma.
// ---- Issuer name is CodeJava
// ---- The token is issued at the current date and time
// ---- The token should expire after 24 hours
// ---- The token is signed using a secret key, which you can specify in the application.properties file or from system environment variable.
// And the signature algorithm is HMAC using SHA-512.


    public static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    public boolean validateAccessToken(String token) {
        byte[] keyBytes = Decoders.BASE64.decode((SECRET_KEY));
        Key key = Keys.hmacShaKeyFor(keyBytes);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex){
            LOGGER.error(ex.getMessage(), "JWT EXPIRED");
        } catch (IllegalArgumentException ex){
            LOGGER.error(ex.getMessage(), "Token is null, empty or whitespace");
        } catch (MalformedJwtException ex){
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (SignatureException ex){
            LOGGER.error("Signature validation failed");
        }
        return false;
    }

    public String getSubject(String token){
        return parseClaims(token).getSubject();
    }

    Claims parseClaims(String token) {
        byte[] keyBytes = Decoders.BASE64.decode((SECRET_KEY));
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}


// To access the secure REST APIs, the client must include an access token in the Authorization header of the request.
// So we need to insert our own filter in the middle of Spring Security filters chain,
// before the UsernameAndPasswordAuthenticationFilter, in order to check the Authorization header of each request.



// Here we add 2 public methods:
// validateAccessToken(): used to verify a given JWT. It returns true if the JWT is verified, or false otherwise.
// getSubject(): gets the value of the subject field of a given token. The subject contains User ID and email, which will be used to recreate a User object.

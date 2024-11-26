package taskmanager.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;

public class JWTUtil {

    private static final String SECRET_KEY = "my-secret-key"; // Secret key for signing JWT

    // Generate JWT Token
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)  // Set claims (user data)
                .setIssuedAt(new Date())  // Set issue date
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // Set expiration time (e.g., 1 day)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Sign with secret key
                .compact();
    }

    // Parse JWT Token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY) // Set secret key to validate the token
                .parseClaimsJws(token)
                .getBody(); // Returns the claims from the token
    }

    // Validate Token (Check if it's expired or invalid)
    public static boolean isValidToken(String token) {
        try {
            parseToken(token); // Try to parse the token
            return true;
        } catch (Exception e) {
            return false;  // Invalid or expired token
        }
    }
}

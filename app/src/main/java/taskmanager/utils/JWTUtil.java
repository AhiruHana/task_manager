package taskmanager.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "your_secret_key";

    public static String generateToken(Long userId) {
        long expirationTime = 1000 * 60 * 60 * 24; // Token expires in 1 day
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Optionally, you could add a method to parse and validate the token
    public static long parseToken(String token) {
        try {
            return Long.parseLong(Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("It happened");
            e.printStackTrace();
            return 0;
        } catch (io.jsonwebtoken.JwtException e) {
            System.err.println("It happened");
            e.printStackTrace();
            return 0;
        }
    }
}

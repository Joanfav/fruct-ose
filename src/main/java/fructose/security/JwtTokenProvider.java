package fructose.security;

import fructose.security.exception.InvalidJwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
	@Value("${application.security.jwt.expiration}")
	private int expirationInMs;
	@Value("${application.security.jwt.secret-key}")
	private final String jwtSecret = "f1010ddd5248cf015267e355fed260207ddbbb3448bd13ae360b8a63a9fd4522c32ff6573756b4460f1e0c052f0c1ecf14caa8d9a454dbae9eb1994be309633e3a930879f186ac536554c07e11c84c0a7f7c8301564e2ba6f8c3ec8da682c7731dfef09b9416de14625f906a58de8a14c72c99f83fb8e1d7750f931f67a5e10d5f47f804fe790e48183d6a9adaff03c0d9dade160bbdc0f8aa65988387ebab41544fb7e950d97d90520e08caf93896ac100f3c1fe49e1426733db46b743e5d4c4fff45487abce6720af1a80cc1d82489fe7c1140a703d127f8314d10203cee28be14b12bd26787c13e5b454f34a4a310a6b891b4df5d664e5a292e30622680a0";
	
	public String generateToken(Authentication authentication) {
		long nowMillis = System.currentTimeMillis();
		JwtBuilder builder = Jwts.builder()
				.setSubject(authentication.getName())
				.setIssuedAt(new Date(nowMillis))
				.setExpiration(new Date(nowMillis + expirationInMs))
				.claim("authorities", authentication.getAuthorities())
				.signWith(key());
		return builder.compact();
	}
	
	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	public String getEmailFromJWT(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	public void validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
		} catch (SecurityException | MalformedJwtException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
		}
	}
}
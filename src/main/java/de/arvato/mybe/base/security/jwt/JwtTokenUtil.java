package de.arvato.mybe.base.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * see as well https://developer.okta.com/blog/2018/10/31/jwts-with-java
 */
@Component
public class JwtTokenUtil implements InitializingBean, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final long serialVersionUID = -3301605591108950415L;

    public static final String CLAIM_KEY_USERNAME = "sub";
    public static final String CLAIM_KEY_CREATED = "created";
    public static final String CLAIM_KEY_EXPIRED = "exp";

    HashMap<String, Claims> cache = new HashMap(100);
    private Key key;

    public static Long EXPIRATION = 5 * 60L; // 5min
    public static Long EXPIRATION_DEVICE = 60 * 60L; // 60min

    @Autowired
    private TimeProvider timeProvider;

    public JwtTokenUtil()
    {

    }

    public JwtTokenUtil(@Autowired TimeProvider timeProvider)
    {
        this.timeProvider = timeProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String secret = "2tXyY8tvnsi1xtEC'ht*+PsiQ:fg`OQ*^+p\\OwiienlrB5yHd;P4J$1";
        secret += secret; // we need at least 512 bits for HS512 security
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getCustomClaim(String token, String claimname) {
        String claim;
        try {
            final Claims claims = getClaimsFromToken(token);
            claim = (String)claims.get(claimname);
        } catch (Exception e) {
            claim = null;
        }
        return claim;
    }


    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            LOGGER.info("TOKEN-Expiration: {}", e.toString());
            expiration = null;
        }
        return expiration;
    }

    public Claims getClaimsFromToken(String token) {

        Claims claims = cache.get(token);

        if(claims != null)
            return claims;

        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOGGER.info("TOKEN-Claims: {}", e.toString());
            claims = null;
        }

        if(cache.size() > 100)
            cache.clear();

        cache.put(token, claims);

        return claims;
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(timeProvider.now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }


    public String generateToken(String username, Map<String, Object> optclaims, Long expiration)
    {
        Map<String, Object> claims = new HashMap<>();

        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, timeProvider.now());

        claims.putAll(optclaims);

        return doGenerateToken(claims, expiration);
    }

    private String doGenerateToken(Map<String, Object> claims, Long expiration)
    {
        final Date createdDate = (Date) claims.get(CLAIM_KEY_CREATED);
        final Date expirationDate = new Date(createdDate.getTime() + expiration * 1000);

        LOGGER.debug("doGenerateToken() createdDate='{}'", createdDate);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token));
    }

    public String refreshToken(String token, Long expiration) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, timeProvider.now());
            refreshedToken = doGenerateToken(claims, expiration);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        //JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token));
    }
}
package pro.iamgamer.routing.csrf;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * Created by Sergey Kobets on 12.03.2016.
 */
class PersistCSRFHandlerImp implements PersistCSRFHandler {
    private static final Logger log = LoggerFactory.getLogger(PersistCSRFHandlerImp.class);
    private static final Base64.Encoder BASE64 = Base64.getMimeEncoder();

    private final Random RAND = new SecureRandom();
    private final Mac mac;
    private long invalidationTimeout = DEFAULT_INVALIDATION_TIMEOUT;
    private long regenerationTimeout = DEFAULT_REGENERATION_TIMEOUT;
    private boolean nagHttps;
    private String headerName = DEFAULT_HEADER_NAME;

    PersistCSRFHandlerImp(final String secret) {
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateToken() {
        byte[] salt = new byte[32];
        RAND.nextBytes(salt);

        String saltPlusToken = BASE64.encodeToString(salt) + "." + Long.toString(System.currentTimeMillis());
        String signature = BASE64.encodeToString(mac.doFinal(saltPlusToken.getBytes()));

        return saltPlusToken + "." + signature;
    }

    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpMethod method = request.method();
        if (method != HttpMethod.POST && method != HttpMethod.PUT && method != HttpMethod.DELETE) {
            context.next();
            return;
        }

        String token = request.getHeader(headerName);
        if (token == null) {
            context.fail(403);
            return;
        }
        String[] tokenParts = token.split("\\.");

        if (tokenParts.length != 3 || !validateToken(tokenParts)) {
            context.fail(403);
            return;
        }

        try {
            final long parseTs = Long.parseLong(tokenParts[1]);

            if (isValidTS(parseTs)) {
                context.next();
                return;
            }

            if (isNedRegenerate(parseTs)) {
                context.response().putHeader(DEFAULT_HEADER_NAME, generateToken());
                context.next();
                return;
            }

        } catch (NumberFormatException e) {
            context.fail(403);
            return;
        }

        if (isLastKnownToken()) {
            context.response().putHeader(DEFAULT_HEADER_NAME, generateToken());
            context.next();
        } else {
            context.fail(403);
        }
    }

    private boolean validateToken(String[] tokenParts) {
        String saltPlusToken = tokenParts[0] + "." + tokenParts[1];
        String signature = BASE64.encodeToString(mac.doFinal(saltPlusToken.getBytes()));

        if (!signature.equals(tokenParts[2])) {
            return false;
        }
        return signature.equals(tokenParts[2]);
    }

    public boolean isLastKnownToken() {
        return false;
    }

    public boolean isValidTS(Long tokenTs) {
        return System.currentTimeMillis() < tokenTs + regenerationTimeout;
    }

    public boolean isNedRegenerate(long tokenTs) {
        long now = System.currentTimeMillis();
        return (now > tokenTs + regenerationTimeout
                && now < tokenTs + invalidationTimeout);
    }

    @Override
    public PersistCSRFHandler setHeaderName(String headerName) {
        this.headerName = headerName;
        return this;
    }

    @Override
    public PersistCSRFHandler setNagHttps(boolean nag) {
        this.nagHttps = nag;
        return this;
    }

    @Override
    public PersistCSRFHandler setTimeouts(long regeneration, long invalidation) {
        if (regeneration > invalidation) {
            throw new IllegalArgumentException();
        }
        this.invalidationTimeout = invalidation;
        this.regenerationTimeout = regeneration;
        return this;
    }

    @Override
    public String getHeaderName() {
        return headerName;
    }
}

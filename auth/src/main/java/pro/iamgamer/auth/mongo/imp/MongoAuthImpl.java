package pro.iamgamer.auth.mongo.imp;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.User;
import io.vertx.ext.mongo.MongoClient;
import pro.iamgamer.auth.mongo.AuthenticationException;
import pro.iamgamer.auth.mongo.HashStrategy;
import pro.iamgamer.auth.mongo.MongoAuth;

import java.util.Arrays;
import java.util.List;

public class MongoAuthImpl implements MongoAuth {
    private static final Logger log = LoggerFactory.getLogger(MongoAuthImpl.class);
    private MongoClient mongoClient;
    private String usernameField = DEFAULT_USERNAME_FIELD;
    private String passwordField = DEFAULT_PASSWORD_FIELD;
    private String roleField = DEFAULT_ROLE_FIELD;
    private String permissionField = DEFAULT_PERMISSION_FIELD;
    private String usernameCredentialField = DEFAULT_CREDENTIAL_USERNAME_FIELD;
    private String passwordCredentialField = DEFAULT_CREDENTIAL_PASSWORD_FIELD;
    private String saltField = DEFAULT_SALT_FIELD;
    private String collectionName = DEFAULT_COLLECTION_NAME;

    private JsonObject config;

    private HashStrategy hashStrategy;

    public MongoAuthImpl(MongoClient mongoClient, JsonObject config) {
        this.mongoClient = mongoClient;
        this.config = config;
        init();
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String username = authInfo.getString(this.usernameCredentialField);
        String password = authInfo.getString(this.passwordCredentialField);

        // Null username is invalid
        if (username == null) {
            resultHandler.handle((Future.failedFuture("Username must be set for authentication.")));
            return;
        }
        if (password == null) {
            resultHandler.handle((Future.failedFuture("Password must be set for authentication.")));
            return;
        }
        AuthToken token = new AuthToken(username, password);

        JsonObject query = createQuery(username);
        mongoClient.find(this.collectionName, query, res -> {

            try {
                if (res.succeeded()) {
                    User user = handleSelection(res, token);
                    resultHandler.handle(Future.succeededFuture(user));
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
            } catch (Throwable e) {
                log.warn(e);
                resultHandler.handle(Future.failedFuture(e));
            }

        });

    }

    protected JsonObject createQuery(String username) {
        return new JsonObject().put(usernameField, username);
    }

    private User handleSelection(AsyncResult<List<JsonObject>> resultList, AuthToken authToken)
            throws AuthenticationException {
        switch (resultList.result().size()) {
            case 0: {
                String message = "No account found for user [" + authToken.username + "]";
                // log.warn(message);
                throw new AuthenticationException(message);
            }
            case 1: {
                JsonObject json = resultList.result().get(0);
                User user = new MongoUser(json, this);
                if (examinePassword(user, authToken))
                    return user;
                else {
                    String message = "Invalid username/password [" + authToken.username + "]";
                    // log.warn(message);
                    throw new AuthenticationException(message);
                }
            }
            default: {
                // More than one row returned!
                String message = "More than one user row found for user [" + authToken.username + "( "
                        + resultList.result().size() + " )]. Usernames must be unique.";
                // log.warn(message);
                throw new AuthenticationException(message);
            }
        }
    }

    private boolean examinePassword(User user, AuthToken authToken) {
        byte[] storedPassword = getHashStrategy().getStoredPwd(user);
        byte[] givenPassword = getHashStrategy().computeHash(authToken.password, user);
        return storedPassword != null && Arrays.equals(storedPassword, givenPassword);
    }

    private void init() {

        String collectionName = config.getString(PROPERTY_COLLECTION_NAME);
        if (collectionName != null) {
            setCollectionName(collectionName);
        }

        String usernameField = config.getString(PROPERTY_USERNAME_FIELD);
        if (usernameField != null) {
            setUsernameField(usernameField);
        }

        String passwordField = config.getString(PROPERTY_PASSWORD_FIELD);
        if (passwordField != null) {
            setPasswordField(passwordField);
        }

        String roleField = config.getString(PROPERTY_ROLE_FIELD);
        if (roleField != null) {
            setRoleField(roleField);
        }

        String permissionField = config.getString(PROPERTY_PERMISSION_FIELD);
        if (roleField != null) {
            setPermissionField(permissionField);
        }

        String usernameCredField = config.getString(PROPERTY_CREDENTIAL_USERNAME_FIELD);
        if (usernameCredField != null) {
            setUsernameCredentialField(usernameCredField);
        }

        String passwordCredField = config.getString(PROPERTY_CREDENTIAL_PASSWORD_FIELD);
        if (passwordCredField != null) {
            setPasswordCredentialField(passwordCredField);
        }

        String saltField = config.getString(PROPERTY_SALT_FIELD);
        if (saltField != null) {
            setSaltField(saltField);
        }
    }

    @Override
    public MongoAuth setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    @Override
    public MongoAuth setUsernameField(String fieldName) {
        this.usernameField = fieldName;
        return this;
    }

    @Override
    public MongoAuth setPasswordField(String fieldName) {
        this.passwordField = fieldName;
        return this;
    }

    @Override
    public MongoAuth setRoleField(String fieldName) {
        this.roleField = fieldName;
        return this;
    }

    @Override
    public MongoAuth setUsernameCredentialField(String fieldName) {
        this.usernameCredentialField = fieldName;
        return this;
    }

    @Override
    public MongoAuth setPasswordCredentialField(String fieldName) {
        this.passwordCredentialField = fieldName;
        return this;
    }

    @Override
    public MongoAuth setSaltField(String fieldName) {
        this.saltField = fieldName;
        return this;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public final String getUsernameField() {
        return usernameField;
    }

    @Override
    public final String getPasswordField() {
        return passwordField;
    }

    @Override
    public final String getRoleField() {
        return roleField;
    }

    @Override
    public final String getUsernameCredentialField() {
        return usernameCredentialField;
    }

    @Override
    public final String getPasswordCredentialField() {
        return passwordCredentialField;
    }

    @Override
    public final String getSaltField() {
        return saltField;
    }

    @Override
    public MongoAuth setPermissionField(String fieldName) {
        this.permissionField = fieldName;
        return this;
    }

    @Override
    public String getPermissionField() {
        return this.permissionField;
    }

    @Override
    public MongoAuth setHashStrategy(HashStrategy hashStrategy) {
        this.hashStrategy = hashStrategy;
        return this;
    }

    @Override
    public HashStrategy getHashStrategy() {
        if (hashStrategy == null)
            hashStrategy = new DefaultHashStrategy();
        return hashStrategy;
    }

    static class AuthToken {
        String username;
        String password;

        AuthToken(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(hashStrategy);
    }
}

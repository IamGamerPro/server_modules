package pro.iamgamer.auth.mongo;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.mongo.MongoClient;
import pro.iamgamer.auth.mongo.imp.MongoAuthImpl;


public interface MongoAuth extends AuthProvider {

    String PROPERTY_COLLECTION_NAME = "collectionName";
    String PROPERTY_USERNAME_FIELD = "usernameField";
    String PROPERTY_ROLE_FIELD = "roleField";
    String PROPERTY_PERMISSION_FIELD = "permissionField";
    String PROPERTY_PASSWORD_FIELD = "passwordField";
    String PROPERTY_CREDENTIAL_USERNAME_FIELD = "usernameCredentialField";
    String PROPERTY_CREDENTIAL_PASSWORD_FIELD = "passwordCredentialField";
    String PROPERTY_SALT_FIELD = "saltField";
    String DEFAULT_COLLECTION_NAME = "user";
    String DEFAULT_USERNAME_FIELD = "username";
    String DEFAULT_PASSWORD_FIELD = "password";
    String DEFAULT_ROLE_FIELD = "roles";
    String DEFAULT_PERMISSION_FIELD = "permissions";
    String DEFAULT_SALT_FIELD = "salt";

    String DEFAULT_CREDENTIAL_USERNAME_FIELD = DEFAULT_USERNAME_FIELD;
    String DEFAULT_CREDENTIAL_PASSWORD_FIELD = DEFAULT_PASSWORD_FIELD;

    String ROLE_PREFIX = "role:";


    static MongoAuth create(MongoClient mongoClient, JsonObject config) {
        return new MongoAuthImpl(mongoClient, config);
    }

    MongoAuth setCollectionName(String collectionName);

    MongoAuth setUsernameField(String fieldName);

    MongoAuth setPasswordField(String fieldName);

    MongoAuth setRoleField(String fieldName);

    MongoAuth setPermissionField(String fieldName);

    MongoAuth setUsernameCredentialField(String fieldName);

    MongoAuth setPasswordCredentialField(String fieldName);

    MongoAuth setSaltField(String fieldName);

    String getCollectionName();

    String getUsernameField();

    String getPasswordField();

    String getRoleField();

    String getPermissionField();

    String getUsernameCredentialField();

    String getPasswordCredentialField();

    String getSaltField();

    MongoAuth setHashStrategy(HashStrategy hashStrategy);

    HashStrategy getHashStrategy();

}

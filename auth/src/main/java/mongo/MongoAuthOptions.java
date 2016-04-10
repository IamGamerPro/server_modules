package mongo;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoAuthOptions implements io.vertx.ext.auth.AuthOptions {
    private boolean shared;
    private String datasourceName;
    private String collectionName;
    private String usernameField;
    private String passwordField;
    private String roleField;
    private String permissionField;
    private String usernameCredentialField;
    private String saltField;
    private HashSaltStyle saltStyle;
    private JsonObject config;

    public MongoAuthOptions() {
        shared = false;
        datasourceName = null;
        collectionName = MongoAuth.DEFAULT_COLLECTION_NAME;
        usernameField = MongoAuth.DEFAULT_USERNAME_FIELD;
        passwordField = MongoAuth.DEFAULT_PASSWORD_FIELD;
        roleField = MongoAuth.DEFAULT_ROLE_FIELD;
        permissionField = MongoAuth.DEFAULT_PERMISSION_FIELD;
        usernameCredentialField = MongoAuth.DEFAULT_CREDENTIAL_USERNAME_FIELD;
        saltField = MongoAuth.DEFAULT_SALT_FIELD;
        saltStyle = null;
    }

    public MongoAuthOptions(MongoAuthOptions that) {
        shared = that.shared;
        datasourceName = that.datasourceName;
        datasourceName = that.datasourceName;
        collectionName = that.collectionName;
        usernameField = that.usernameField;
        passwordField = that.passwordField;
        roleField = that.roleField;
        permissionField = that.permissionField;
        usernameCredentialField = that.usernameCredentialField;
        saltField = that.saltField;
        saltStyle = that.saltStyle;
        config = that.config != null ? that.config.copy() : null;
    }

    public MongoAuthOptions(JsonObject json) {
        this();
        MongoAuthOptionsConverter.fromJson(json, this);
    }

    @Override
    public MongoAuthOptions clone() {
        return new MongoAuthOptions(this);
    }

    @Override
    public MongoAuth createProvider(Vertx vertx) {
        MongoClient client;
        if (shared) {
            if (datasourceName != null) {
                client = MongoClient.createShared(vertx, config, datasourceName);
            } else {
                client = MongoClient.createShared(vertx, config);
            }
        } else {
            client = MongoClient.createNonShared(vertx, config);
        }
        JsonObject authConfig = new JsonObject();
        MongoAuthOptionsConverter.toJson(this, authConfig);
        return MongoAuth.create(client, authConfig);
    }

    public boolean getShared() {
        return shared;
    }

    public MongoAuthOptions setShared(boolean shared) {
        this.shared = shared;
        return this;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public MongoAuthOptions setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
        return this;
    }

    public JsonObject getConfig() {
        return config;
    }

    public MongoAuthOptions setConfig(JsonObject config) {
        this.config = config;
        return this;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public MongoAuthOptions setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public String getUsernameField() {
        return usernameField;
    }


    public MongoAuthOptions setUsernameField(String usernameField) {
        this.usernameField = usernameField;
        return this;
    }

    public String getPasswordField() {
        return passwordField;
    }

    public MongoAuthOptions setPasswordField(String passwordField) {
        this.passwordField = passwordField;
        return this;
    }

    public String getRoleField() {
        return roleField;
    }

    public MongoAuthOptions setRoleField(String roleField) {
        this.roleField = roleField;
        return this;
    }

    public String getPermissionField() {
        return permissionField;
    }

    public MongoAuthOptions setPermissionField(String permissionField) {
        this.permissionField = permissionField;
        return this;
    }

    public String getUsernameCredentialField() {
        return usernameCredentialField;
    }


    public MongoAuthOptions setUsernameCredentialField(String usernameCredentialField) {
        this.usernameCredentialField = usernameCredentialField;
        return this;
    }

    public String getSaltField() {
        return saltField;
    }

    public MongoAuthOptions setSaltField(String saltField) {
        this.saltField = saltField;
        return this;
    }

    public HashSaltStyle getSaltStyle() {
        return saltStyle;
    }


    public MongoAuthOptions setSaltStyle(HashSaltStyle saltStyle) {
        this.saltStyle = saltStyle;
        return this;
    }
}

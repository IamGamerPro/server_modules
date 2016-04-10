package mongo.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import mongo.MongoAuth;

public class MongoUser extends AbstractUser {
    private JsonObject principal;
    private MongoAuth mongoAuth;

    public MongoUser() {
    }

    public MongoUser(String username, MongoAuth mongoAuth) {
        this.principal = new JsonObject().put(mongoAuth.getUsernameField(), username);
        this.mongoAuth = mongoAuth;
    }

    public MongoUser(JsonObject principal, MongoAuth mongoAuth) {
        this.principal = principal;
        this.mongoAuth = mongoAuth;
    }

    @Override
    public void doIsPermitted(String permissionOrRole, Handler<AsyncResult<Boolean>> resultHandler) {
        if (permissionOrRole != null && permissionOrRole.startsWith(MongoAuth.ROLE_PREFIX)) {
            String roledef = permissionOrRole.substring(MongoAuth.ROLE_PREFIX.length());
            doHasRole(roledef, resultHandler);
        } else {
            doHasPermission(permissionOrRole, resultHandler);
        }
    }

    @Override
    public JsonObject principal() {
        return principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        this.mongoAuth = (MongoAuth) authProvider;
    }

    protected void doHasRole(String role, Handler<AsyncResult<Boolean>> resultHandler) {
        try {
            JsonArray roles = principal.getJsonArray(mongoAuth.getRoleField());
            resultHandler.handle(Future.succeededFuture(roles != null && roles.contains(role)));
        } catch (Throwable e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    public String getSalt() {
        return principal.getString(mongoAuth.getSaltField());
    }

    public String getPassword() {
        return principal.getString(mongoAuth.getPasswordField());
    }

    protected void doHasPermission(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        try {
            JsonArray userPermissions = principal.getJsonArray(mongoAuth.getPermissionField());
            resultHandler.handle(Future.succeededFuture(userPermissions != null && userPermissions.contains(permission)));
        } catch (Throwable e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public String toString() {
        return principal.toString();
    }
}

package auth.imp;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

/**
 * Created by Sergey Kobets on 05.02.2016.
 */
public class OrientDBAuthProviderImp implements OrientDBAuthProvider {

    public OrientDBAuthProviderImp(OrientClient client) {

    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String username = authInfo.getString("username");
        if (username == null) {
            resultHandler.handle(Future.failedFuture("authInfo must contain username in 'username' field"));
            return;
        }
        String password = authInfo.getString("password");
        if (password == null) {
            resultHandler.handle(Future.failedFuture("authInfo must contain password in 'password' field"));
            return;
        }
    }
}

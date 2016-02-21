package auth.imp;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import client.OrientGraphAsync;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

/**
 * Created by Sergey Kobets on 05.02.2016.
 */
public class OrientDBAuthProviderImp implements OrientDBAuthProvider {
    private OrientClient orientClient;

    public OrientDBAuthProviderImp(OrientClient orientClient) {
        this.orientClient = orientClient;
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
        orientClient.getGraph((graphAsyncAsyncHandler) -> {
            if (graphAsyncAsyncHandler.succeeded()) {
                OrientGraphAsync orientGraphAsync = graphAsyncAsyncHandler.result();
                orientGraphAsync.command((orientGraph -> {
                    UserCredentials userCredentials = new UserCredentials();

                    return userCredentials;

                }), event -> {
                    if (event.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(event.result()));
                    } else {
                        resultHandler.handle(Future.failedFuture(event.cause()));
                    }
                });

            } else {
                resultHandler.handle(Future.failedFuture(graphAsyncAsyncHandler.cause()));
            }
        });

    }
}

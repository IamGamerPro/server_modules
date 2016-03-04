package auth.imp;

import auth.OrientDBAuthProvider;
import auth.imp.security.PasswordUtils;
import client.OrientClient;
import client.OrientGraphAsync;
import client.imp.ParamsRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

import java.util.Arrays;
import java.util.Optional;


/**
 * Created by Sergey Kobets on 05.02.2016.
 */
public class OrientDBAuthProviderImp implements OrientDBAuthProvider {
    private final OrientClient orientClient;
    private static final OCommandSQL loginQuery = new OCommandSQL("select from User where login = ?");

    public OrientDBAuthProviderImp(OrientClient orientClient) {
        this.orientClient = orientClient;
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String username = authInfo.getString("login");
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
                orientGraphAsync.query(ParamsRequest.buildRequest(loginQuery, username), event -> {
                    if (event.succeeded()) {
                        Optional<Vertex> first = event.result().findFirst();
                        if (first.isPresent()) {
                            Vertex userInDb = first.get();
                            final byte[] passwordInDb = userInDb.getProperty("password");
                            final byte[] salt = userInDb.getProperty("salt");
                            final byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);
                            final boolean equals = Arrays.equals(passwordInDb, hash);
                            if (equals) {
                                resultHandler.handle(Future.succeededFuture(new OrientUser(username, this)));
                            } else {
                                resultHandler.handle(Future.failedFuture("Не правильное имя пользователя или пароль"));
                            }
                        } else {
                            resultHandler.handle(Future.failedFuture("Не правильное имя пользователя или пароль"));
                        }
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

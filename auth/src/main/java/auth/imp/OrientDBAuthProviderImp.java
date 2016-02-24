package auth.imp;

import auth.OrientDBAuthProvider;
import client.OrientClient;
import client.OrientGraphAsync;
import com.orientechnologies.orient.core.sql.OCommandSQL;
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
    private static final OCommandSQL loginQuery = new OCommandSQL("select from AppUser where login = ?");

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
                    Object execute = orientGraph.command(loginQuery).execute(username);
                    System.out.println(execute);
                    return new UserCredentials();
                    /*����� �������� �� ������� �� �����, ��� ����� �����������*/
                    /*final byte[] passwordInDb = execute.field("password");
                    final byte[] salt = execute.field("salt");
                    final byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);
                    final boolean equals = Arrays.equals(passwordInDb, hash);
                    if (equals) {
                        return userCredentials;
                    } else {
                        throw new RuntimeException();
                    }*/
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

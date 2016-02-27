package services.register;

import client.OrientClient;
import client.OrientGraphAsync;
import client.imp.ParamsRequest;
import co.paralleluniverse.fibers.Suspendable;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;

import java.util.stream.Stream;

import static io.vertx.ext.sync.Sync.awaitResult;

/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterService {
    public static final OCommandSQL SELECT_BY_LOGIN = new OCommandSQL("select from User where login = ?");
    private final OrientClient orientClient;

    public RegisterService(OrientClient orientClient) {
        this.orientClient = orientClient;
    }

    @Suspendable
    public boolean isUniqueLogin(String loginName) {
        OrientGraphAsync orientGraphAsync = awaitResult(orientClient::getGraph);
        Stream<Vertex> o = awaitResult(x -> orientGraphAsync.query(ParamsRequest.buildRequest(SELECT_BY_LOGIN, loginName), x));
        return o.count() == 0;
    }

}

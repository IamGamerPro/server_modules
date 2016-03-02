package services.register;

import client.OrientClient;
import client.OrientGraphAsync;
import client.imp.ParamsRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import io.vertx.ext.web.RoutingContext;

import java.util.stream.Stream;

/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterService {
    public static final OCommandSQL SELECT_BY_LOGIN = new OCommandSQL("select 1 from User where login = ?");
    public static final OCommandSQL SELECT_BY_EMAIL = new OCommandSQL("select 1 from User where email = ?");
    private final OrientClient orientClient;

    public RegisterService(OrientClient orientClient) {
        this.orientClient = orientClient;
    }

    public void isUniqueLogin(RoutingContext requestHandler) {
        String login = requestHandler.request().getParam("value");
        isExist(requestHandler, ParamsRequest.buildRequest(SELECT_BY_LOGIN, login));
    }
    public void isUniqueEmail(RoutingContext requestHandler) {
        String login = requestHandler.request().getParam("value");
        isExist(requestHandler, ParamsRequest.buildRequest(SELECT_BY_EMAIL, login));
    }

    private void isExist(RoutingContext requestHandler, ParamsRequest request) {
        orientClient.getGraph(connection -> {
            if (connection.succeeded()) {
                OrientGraphAsync result = connection.result();
                result.query(request, requestResult -> {
                    if (requestResult.succeeded()) {
                        Stream<Vertex> result1 = requestResult.result();
                        System.out.println(result1);
                        Boolean b = result1.count() > 0;
                        requestHandler.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .setStatusCode(200).end(b.toString());
                    } else {
                        requestHandler.response().setStatusCode(500).end();
                    }
                });
            } else {
                requestHandler.response().setStatusCode(500).end();
            }
        });
    }


}

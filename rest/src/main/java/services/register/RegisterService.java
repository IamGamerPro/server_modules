package services.register;

import auth.imp.security.PasswordUtils;
import client.OrientClient;
import client.OrientGraphAsync;
import client.imp.ParamsRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import services.Responses;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Sergey Kobets on 27.02.2016.
 */
public class RegisterService {
    private static final OCommandSQL SELECT_BY_LOGIN = new OCommandSQL("select 1 from User where login = ?");
    private static final OCommandSQL SELECT_BY_EMAIL = new OCommandSQL("select 1 from User where email = ?");
    /*private static final Pattern validPassword = Pattern.compile("(?=^.{6,18}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");*/
    private static final String MANDATORY_REGISTER_PARAM = Responses.errorMessage("Поля: имя пользователя, пароль и email обязательны для заполнения");
    private static final String WEAK_PASSWORD = Responses.errorMessage("Пароль должен иметь длинну не менее 8 символов, состоять из латинских букв верхнего и нижнего регистра, и содержать цифры");

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

    public void register(RoutingContext routingContext) {
        JsonObject bodyAsJson = routingContext.getBodyAsJson();
        String login = bodyAsJson.getString("login");
        String password = bodyAsJson.getString("password");
        String email = bodyAsJson.getString("email");
        if (login == null || email == null || password == null) {
            routingContext.response().setStatusCode(400).putHeader("content-type", "application/json; charset=utf-8").end(MANDATORY_REGISTER_PARAM);
            return;
        }
        if (/*!validPassword.matcher(password).matches()*/false) {
            routingContext.response().setStatusCode(400).putHeader("content-type", "application/json; charset=utf-8").end(WEAK_PASSWORD);
            return;
        }
        orientClient.getGraph(connection -> {
            if (connection.succeeded()) {
                OrientGraphAsync graphAsync = connection.result();
                graphAsync.command(
                        orientGraph -> {
                            final byte[] salt = PasswordUtils.randomSalt();
                            final byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);
                            orientGraph.begin();
                            OrientVertex orientVertex = orientGraph.addVertex("class:User", "login", login, "salt", salt, "password", hash);
                            orientGraph.commit();
                            return orientVertex.getId();
                        },
                        requestResult -> {
                            if (requestResult.succeeded()) {
                                routingContext.response().setStatusCode(201).end();
                            } else {
                                routingContext.response().setStatusCode(500).end();
                            }
                        });
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void isExist(RoutingContext routingContext, ParamsRequest request) {
        orientClient.getGraph(connection -> {
            if (connection.succeeded()) {
                OrientGraphAsync result = connection.result();
                result.query(request, requestResult -> {
                    if (requestResult.succeeded()) {
                        Stream<Vertex> result1 = requestResult.result();
                        System.out.println(result1);
                        Boolean b = result1.count() > 0;
                        routingContext.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .setStatusCode(200).end(Responses.resultMessage(b.toString()));
                    } else {
                        routingContext.response().setStatusCode(500).end();
                    }
                });
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }


}

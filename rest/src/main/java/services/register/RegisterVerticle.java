package services.register;

import com.google.common.net.MediaType;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pro.iamgamer.core.security.PasswordUtils;
import pro.iamgamer.routing.RouteOrchestrator;
import services.Responses;

/**
 * Created by Sergey Kobets on 10.04.2016.
 */
public class RegisterVerticle extends AbstractVerticle {
    private MongoClient shared;
    private MailClient mailValidation;
    private Integer port;

    @Override
    public void start() throws Exception {
        JsonObject config = context.config();
        JsonObject databaseConfig = config.getJsonObject("databaseConfig");
        JsonObject mailClientConfig = config.getJsonObject("mailClientConfig");
        mailValidation = MailClient.createShared(vertx, new MailConfig(mailClientConfig), "mailValidation");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        shared = MongoClient.createShared(vertx, databaseConfig);
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");

        Router router = Router.router(vertx);
        router.post().handler(this::register);
        router.get("/user-exists").handler(routingContext -> {
            String login = routingContext.request().getParam("value");
            JsonObject byLogin = new JsonObject().put("login", login);
            checkExist(routingContext, byLogin);
        });
        router.get("/email-exists").handler(routingContext -> {
            String email = routingContext.request().getParam("value");
            JsonObject byEmail = new JsonObject().put("emails.email", email);
            checkExist(routingContext, byEmail);
        });
        instance.mountPublicSubRouter("/register/v1/", router);
        vertx.createHttpServer().requestHandler(instance::accept).listen(port);
    }

    private void register(RoutingContext routingContext) {
        JsonObject registerRequest = routingContext.getBodyAsJson();
        final String login = registerRequest.getString("login");
        final String password = registerRequest.getString("password");
        final String email = registerRequest.getString("email");
        vertx.<byte[][]>executeBlocking(future -> {
            try {
                final byte[] salt = PasswordUtils.randomSalt();
                final byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);
                final byte[][] bytes = {hash, salt};
                future.complete(bytes);
            } catch (Exception e) {
                future.fail(e);
            }
        }, false, calc -> {
            if (calc.succeeded()) {
                final byte[] hash = calc.result()[0];
                final byte[] salt = calc.result()[1];
                shared.createCollection("users",
                        v -> {
                            JsonObject document = new JsonObject();
                            document.put("login", login);
                            JsonArray emails = new JsonArray().add(
                                    new JsonObject()
                                            .put("mail", email)
                                            .put("primary", true)
                                            .put("checked", false));
                            document.put("emails", emails);
                            document.put("password", new JsonObject().put("$binary", hash));
                            document.put("salt", new JsonObject().put("$binary", salt));
                            shared.insert("users", document, generatedId -> {
                                        if (generatedId.succeeded()) {
                                            mailValidation(email, generatedId);
                                            routingContext.response().setStatusCode(201);
                                            routingContext.reroute(HttpMethod.POST, "/login");
                                        } else {
                                            routingContext.fail(generatedId.cause());
                                        }
                                    }
                            );
                        }
                );
            }
        });
    }

    private void mailValidation(String email, AsyncResult<String> generatedId) {
        shared.createCollection("callbacks", v2 -> {
            JsonObject callback = new JsonObject()
                            .put("type", "mailValidation")
                            .put("user_id", generatedId.result())
                            .put("email", email);
            shared.insert("callbacks", callback, res -> {
                if (res.succeeded()) {
                    String message = String.format("lolalhost:%d/confirmation/%s", port, res.result());
                    mailValidation.sendMail(
                            new MailMessage("developer@iamgamer.pro", email, "IamGamer Account Confirmation", message),
                            sentResult -> {
                                if (sentResult.failed()) {
                                    System.err.println(sentResult.cause().getMessage());
                                }
                            });

                }
            });
        });
    }

    private void checkExist(RoutingContext routingContext, final JsonObject query) {
        shared.count("users", query, result -> {
            if (result.succeeded()) {
                Boolean b = result.result() > 0;
                routingContext.response()
                        .putHeader("content-type", MediaType.JSON_UTF_8.toString())
                        .setStatusCode(200).end(Responses.resultMessage(b));
            } else {
                routingContext.fail(500);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        shared.close();
    }
}



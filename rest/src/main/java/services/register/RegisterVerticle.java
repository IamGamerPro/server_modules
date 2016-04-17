package services.register;

import com.google.common.net.MediaType;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResultHandler;
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

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Sergey Kobets on 10.04.2016.
 */
public class RegisterVerticle extends AbstractVerticle {
    private MongoClient shared;
    private MailClient mailClient;
    private Integer port;

    @Override
    public void start() throws Exception {
        JsonObject config = context.config();
        JsonObject databaseConfig = config.getJsonObject("databaseConfig");
        JsonObject mailClientConfig = config.getJsonObject("mailClientConfig");
        mailClient = MailClient.createShared(vertx, new MailConfig(mailClientConfig), "mailValidation");
        port = config.getJsonObject("httServerConfig").getInteger("port");
        shared = MongoClient.createShared(vertx, databaseConfig);
        RouteOrchestrator instance = RouteOrchestrator.getInstance(vertx, "/api");

        Router router = Router.router(vertx);
        router.post().handler(this::register);
        router.post("/change-password").handler(this::changePassword);
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

        Router mails = Router.router(vertx);
        mails.post().handler(this::addEmail);
        mails.delete().handler(this::deleteEmail);
        instance.mountRequiresAuthorizationSubRouter("/mail", mails);
        vertx.createHttpServer().requestHandler(instance::accept).listen(port);
    }

    private void changePassword(RoutingContext routingContext) {
        Optional<String> userId = getUserId(routingContext);
        if (userId.isPresent()) {
            final JsonObject userById = new JsonObject().put("_id", new JsonObject().put("$oid", userId.get()));
            JsonObject bodyAsJson = routingContext.getBodyAsJson();
            final String currentPwd = bodyAsJson.getString("currentPwd");
            final String newPwd = bodyAsJson.getString("newPwd");

            shared.findOne("users",
                    userById,
                    new JsonObject().put("salt", 1).put("password", 1),
                    result -> {
                        if (result.succeeded()) {
                            vertx.<byte[][]>executeBlocking(future -> {
                                try {
                                    byte[] salt = result.result().getBinary("salt");
                                    byte[] passwords = result.result().getBinary("password");
                                    final byte[] hash = PasswordUtils.hash(currentPwd.toCharArray(), salt);
                                    if (!Arrays.equals(hash, passwords)) {
                                        future.fail("incorrectPwd");
                                    }
                                    byte[] newSalt = PasswordUtils.randomSalt();
                                    byte[] newPassword = PasswordUtils.hash(newPwd.toCharArray(), newSalt);
                                    final byte[][] bytes = {newPassword, newSalt};
                                    future.complete(bytes);
                                } catch (Exception e) {
                                    future.fail(e);
                                }
                            }, false, result2 -> {
                                if (result2.succeeded()) {
                                    byte[][] newHash = result2.result();
                                    JsonObject document = new JsonObject();
                                    document.put("password", new JsonObject().put("$binary", newHash[0]));
                                    document.put("salt", new JsonObject().put("$binary", newHash[1]));
                                    JsonObject update = new JsonObject().put("$set", document);
                                    shared.update("users", userById, update, (AsyncResultHandler<Void>) event -> {
                                        System.out.println("треш оно работает!");
                                        routingContext.response().end();
                                    });

                                } else {
                                    routingContext.fail(403);
                                }
                            });
                        }
                    });
        } else {
            routingContext.fail(403);
        }
    }

    private Optional<String> getUserId(RoutingContext routingContext) {
        String id;
        if (routingContext.user() != null) {
            JsonObject principal = routingContext.user().principal();
            if (principal != null) {
                id = principal.getString("userId");
            } else {
                id = null;
            }
        } else {
            id = null;
        }
        return Optional.ofNullable(id);
    }

    private void deleteEmail(RoutingContext routingContext) {
        if (routingContext.user() != null) {
            JsonObject principal = routingContext.user().principal();
            if (principal != null) {
                String id = principal.getString("userId");
                if (id != null) {
                    String email = routingContext.request().getParam("value");
                    JsonObject query = new JsonObject()
                            .put("_id",
                                    new JsonObject()
                                            .put("$oid", id))
                            .put("emails.1",
                                    new JsonObject()
                                            .put("$exists", true))
                            .put("emails.mail", email);
                    JsonObject remove = new JsonObject()
                            .put("$pull",
                                    new JsonObject()
                                            .put("emails",
                                                    new JsonObject()
                                                            .put("mail", email)));
                    shared.update("users", query, remove,
                            event -> routingContext.response().end());
                    return;
                }
            }
        }
        routingContext.fail(500);


    }

    private void addEmail(RoutingContext routingContext) {
        String email = routingContext.request().getParam("value");
        if (routingContext.user() != null && email != null) {
            JsonObject principal = routingContext.user().principal();
            if (principal != null) {
                String id = principal.getString("userId");
                if (id != null) {
                    JsonObject query = new JsonObject()
                            .put("_id",
                                    new JsonObject()
                                            .put("$oid", id));
                    JsonObject update =
                            new JsonObject()
                                    .put("$push", new JsonObject().put("emails",
                                            new JsonObject()
                                                    .put("mail", email)
                                                    .put("primary", false)
                                                    .put("checked", false)));
                    shared.update("users", query, update, res -> {
                        if (res.succeeded()) {
                            mailValidation(email, id);
                            routingContext.response().end();
                        } else {
                            routingContext.fail(res.cause());
                        }
                    });
                } else {
                    routingContext.fail(500);
                }
            } else {
                routingContext.fail(500);
            }
        } else {
            routingContext.fail(500);
        }
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
                                            mailValidation(email, generatedId.result());
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

    private void mailValidation(String email, String generatedId) {
        shared.createCollection("callbacks", v2 -> {
            JsonObject callback = new JsonObject()
                    .put("type", "mailValidation")
                    .put("user_id", generatedId)
                    .put("email", email);
            shared.insert("callbacks", callback, res -> {
                if (res.succeeded()) {

                    String format = String.format("http://localhost:%d/confirmation/%s", port, res.result());
                    String message = " <div> <a rel=\"noopener\" href=\"" + format
                            + "\" target=\"_blank\">" + format + "</a> <div>";

                    mailClient.sendMail(
                            new MailMessage()
                                    .setFrom("develop@iamgamer.pro")
                                    .setTo(email)
                                    .setSubject("IamGamer Account Confirmation")
                                    .setHtml(message),
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
        mailClient.close();
    }
}



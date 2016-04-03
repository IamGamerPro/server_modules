import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;

/**
 * Created by Sergey Kobets on 03.04.2016.
 */
class ConfigurationImp implements Configuration, Shareable {
    final String dbUrl;

    final String dbPassword;

    final String dbUser;

    final Integer httpServerPort;


    ConfigurationImp(Vertx vertx, String filePatch) {
        Buffer buffer = vertx.fileSystem().readFileBlocking(filePatch);
        JsonObject jsonObject = buffer.toJsonObject();
        dbUrl = jsonObject.getString("databaseUrl", "plocal:/test");
        dbPassword = jsonObject.getString("databasePwd");
        dbUser = jsonObject.getString("databaseLogin");
        httpServerPort = jsonObject.getInteger("httpServerPort", 8080);
    }

    @Override
    public String getDbUrl() {
        return dbUrl;
    }

    @Override
    public String getDbPassword() {
        return dbPassword;
    }

    @Override
    public String getDbUser() {
        return dbPassword;
    }

    @Override
    public Integer getHttpServerPort() {
        return httpServerPort;
    }

    @Override
    public JsonObject getDatabaseConfig() {
        return null;
    }
}

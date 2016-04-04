package pro.iamgamer.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by Sergey Kobets on 02.04.2016.
 */
public interface Configuration {

    String IAMGAMER_CONFIGURATION = "iamgamer.configuration";

    String getDbUrl();

    String getDbPassword();

    String getDbUser();

    Integer getHttpServerPort();

    JsonObject getDatabaseConfig();

    static void publishBaseConfiguration(Vertx vertx, String filePatch) {
        ConfigurationImp configurationImp = new ConfigurationImp(vertx, filePatch);
        vertx.sharedData().getLocalMap(Configuration.IAMGAMER_CONFIGURATION).put("default", configurationImp);
    }

    static Configuration getBaseConfiguration(Vertx vertx) {
        return vertx.sharedData().<String, Configuration>getLocalMap(IAMGAMER_CONFIGURATION).get("default");
    }
}

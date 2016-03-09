package services;

import io.vertx.core.json.JsonObject;

/**
 * Created by Sergey Kobets on 09.03.2016.
 */
public class Errors {
    public static String responseError(String s) {
        JsonObject entries = new JsonObject();
        entries.put("error", s);
        return entries.encode();
    }
}

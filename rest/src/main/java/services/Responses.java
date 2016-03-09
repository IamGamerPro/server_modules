package services;

import io.vertx.core.json.JsonObject;

/**
 * Created by Sergey Kobets on 09.03.2016.
 */
public class Responses {
    public static String errorMessage(String s) {
        JsonObject entries = new JsonObject();
        entries.put("error", s);
        return entries.encode();
    }
    public static String resultMessage(String s){
        JsonObject entries = new JsonObject();
        entries.put("result", s);
        return entries.encode();
    }
}

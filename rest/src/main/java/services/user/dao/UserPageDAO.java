package services.user.dao;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import services.user.model.Avatar;
import services.user.model.BaseUserPageData;
import services.user.model.ThumbRectangle;

import java.util.Objects;

/**
 * Created by k_s_a on 03.05.2016.
 */
public class UserPageDAO {
    private final MongoClient mongoClient;

    private UserPageDAO(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public static UserPageDAO getDAO(MongoClient mongoClient) {
        return new UserPageDAO(mongoClient);
    }

    public void update(BaseUserPageData userPageData, String id, RoutingContext callback) {
        Objects.requireNonNull(id);
        JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", id));
        JsonObject update = new JsonObject();

        JsonObject set = new JsonObject();
        if (userPageData.getFirstName() != null) {
            set.put("firstName", userPageData.getFirstName());
        }
        if (userPageData.getLastName() != null) {
            set.put("lastName", userPageData.getLastName());
        }
        if (userPageData.getSex() != null) {
            set.put("sex", userPageData.getSex());
        }
        if (userPageData.getDb() != null) {
            set.put("db", userPageData.getDb());
        }
        if (userPageData.getMb() != null) {
            set.put("mb", userPageData.getMb());
        }
        if (userPageData.getYb() != null) {
            set.put("yb", userPageData.getYb());
        }
        if (userPageData.getHometown() != null) {
            set.put("hometown", userPageData.getHometown());
        }
        if (userPageData.getAboutMe() != null) {
            set.put("aboutMe", userPageData.getAboutMe());
        }
        if (userPageData.getFavoriteGames() != null) {
            set.put("favoriteGames", userPageData.getFavoriteGames());
        }
        if (userPageData.getFavoriteMusic() != null) {
            set.put("favoriteMusic", userPageData.getFavoriteMusic());
        }
        if (userPageData.getStatus() != null) {
            set.put("status", userPageData.getStatus());
        }

        if (userPageData.getTz() != null) {
            set.put("tz", userPageData.getTz());
        }

        if (userPageData.getCountry() != null) {
            set.put("country", userPageData.getCountry());
        }

        if (userPageData.getLang() != null) {
            set.put("lang", userPageData.getLang());
        }

        if (userPageData.getAvatar() != null) {
            Avatar avatar = userPageData.getAvatar();
            if (avatar.getXxs() != null) {
                set.put("avatar.xxs", avatar.getXxs());
            }
            if (avatar.getXs() != null) {
                set.put("avatar.xs", avatar.getXs());
            }
            if (avatar.getL() != null) {
                set.put("avatar.l", avatar.getL());
            }
            if (avatar.getM() != null) {
                set.put("avatar.m", avatar.getM());
            }
            if (avatar.getS() != null) {
                set.put("avatar.s", avatar.getS());
            }
        }

        if (userPageData.getThumbRect() != null) {
            ThumbRectangle thumbRect = userPageData.getThumbRect();
            if(thumbRect.getHeight() != null){
               set.put("thumbRect.height", thumbRect.getHeight());
            }
            if(thumbRect.getWidth() != null){
                set.put("thumbRect.width", thumbRect.getWidth());
            }
            if(thumbRect.getX() != null){
                set.put("thumbRect.x", thumbRect.getX());
            }
            if(thumbRect.getY() != null){
                set.put("thumbRect.y", thumbRect.getY());
            }
        }

        update.put("$set", set);

        mongoClient.update("users", query, update, res -> {
            if (res.succeeded()) {
                callback.response().end();
            } else {
                callback.fail(400);
            }

        });
    }

    public void deleteAvatar(String id, RoutingContext callback) {
        Objects.requireNonNull(id);
        JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", id));
        JsonObject update = new JsonObject().put("$unset", new JsonObject().put("avatar", ""));
        mongoClient.update("users", query, update, res -> {
            if (res.succeeded()) {
                callback.response().end();
            } else {
                callback.fail(400);
            }

        });
    }


}

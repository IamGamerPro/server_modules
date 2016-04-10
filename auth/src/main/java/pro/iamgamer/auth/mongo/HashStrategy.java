package pro.iamgamer.auth.mongo;

import io.vertx.ext.auth.User;

public interface HashStrategy {

    String computeHash(String password, User user);

    String getStoredPwd(User user);

    String getSalt(User user);

    void setExternalSalt(String salt);

    void setSaltStyle(HashSaltStyle saltStyle);

    HashSaltStyle getSaltStyle();

}

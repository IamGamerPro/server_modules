package pro.iamgamer.auth.mongo;

import io.vertx.ext.auth.User;

public interface HashStrategy {

    byte[] computeHash(String password, User user);

    byte[] getStoredPwd(User user);

    byte[] getSalt(User user);
}

package pro.iamgamer.auth.orient.imp;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

import java.nio.charset.StandardCharsets;

/**
 * Created by Sergey Kobets on 05.02.2016.
 */
public class OrientUser extends AbstractUser {
    private String username;
    private OrientDBAuthProviderImp authProvider;
    private JsonObject principal;


    public OrientUser() {
    }

    OrientUser(String username, OrientDBAuthProviderImp authProvider) {
        this.username = username;
        this.authProvider = authProvider;
    }

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {

    }

    @Override
    public JsonObject principal() {
        if (principal == null) {
            principal = new JsonObject().put("username", username);
        }
        return principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        if (authProvider instanceof OrientDBAuthProviderImp) {
            this.authProvider = (OrientDBAuthProviderImp) authProvider;
        } else {
            throw new IllegalArgumentException("Not a OrientDBAuthProviderImp");
        }
    }

    @Override
    public void writeToBuffer(Buffer buff) {
        super.writeToBuffer(buff);
        byte[] bytes = username.getBytes(StandardCharsets.UTF_8);
        buff.appendInt(bytes.length);
        buff.appendBytes(bytes);
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        pos = super.readFromBuffer(pos, buffer);
        int len = buffer.getInt(pos);
        pos += 4;
        byte[] bytes = buffer.getBytes(pos, pos + len);
        username = new String(bytes, StandardCharsets.UTF_8);
        pos += len;
        return pos;
    }
}

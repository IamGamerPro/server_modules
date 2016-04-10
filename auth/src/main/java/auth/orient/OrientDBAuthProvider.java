package auth.orient;

import auth.orient.imp.OrientDBAuthProviderImp;
import client.OrientClient;
import io.vertx.ext.auth.AuthProvider;


/**
 * Created by Sergey Kobets on 05.02.2016.
 */
public interface OrientDBAuthProvider extends AuthProvider {
    static OrientDBAuthProvider create(OrientClient orientClient) {
        return new OrientDBAuthProviderImp(orientClient);
    }

}

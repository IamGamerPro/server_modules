package pro.iamgamer.core.database.dao;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import pro.iamgamer.core.model.User;
import ru.vyarus.guice.persist.orient.db.PersistentContext;

import javax.inject.Inject;


/**
 * Created by sergey.kobets on 01.12.2015.
 */
public class UserTest {
    @Inject
    PersistentContext<ODatabaseDocumentTx> tx;

    /*test2*/
    public User login(String login, String pwd){
        final User user = tx.<User>doWithoutTransaction(db -> {
            OSQLSynchQuery<OResultSet> query = new OSQLSynchQuery<>("select from User where login = " +
                                                                    "?");
            final OResultSet execute = db.command(query).<OResultSet>execute(login);
            return null;
        });

        return null;
    }
}

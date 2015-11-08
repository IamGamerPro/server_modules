package pro.iamgamer.core;


import com.google.inject.persist.PersistService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Sergey Kobets on 08.11.2015.
 */
@Singleton
public class DatabaseManagement {
    private PersistService orientService;

    @Inject
    public DatabaseManagement(PersistService orientService) {
        this.orientService = orientService;
    }

    public void stop(){
        orientService.stop();
    }
    public void start(){
        orientService.start();
    }
}

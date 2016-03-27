package schema;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Created by Sergey Kobets on 12.03.2016.
 */
public class Property {
    private Property(String name) {
    }

    public static Property property(String name){
        return new Property(name);
    }

    public Property type(OType type){
        return this;
    }

    public Property unical(){
        return this;
    }

}

package schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Kobets on 12.03.2016.
 */
public class Schema {
    List<VertexType> vertexTypeList = new ArrayList<>();

    public static Schema schema(){
        return new Schema();
    }

    private Schema() {
    }

    Schema addVertexType(VertexType vertexType){
        vertexTypeList.add(vertexType);
        return this;
    }

    void init(){

    }
}

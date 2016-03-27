package schema;

/**
 * Created by Sergey Kobets on 12.03.2016.
 */
public class VertexType {

    private VertexType(String className) {

    }

    public static VertexType vertexType(String className){
        return new VertexType(className);
    }

    public VertexType addProperty(Property property){
        return this;
    }
}

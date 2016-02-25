package client.imp;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Sergey Kobets on 23.02.2016.
 * Обертка для запроса и его параметров.
 */
public class ParamsRequest {
    private final OCommandSQL request;
    private final Object[] params;

    private ParamsRequest(OCommandSQL request, Object... params) {
        this.request = request;
        this.params = params;
    }

    public static ParamsRequest buildRequest(OCommandSQL request, Object... params) {
        return new ParamsRequest(request, params);
    }

    public OCommandRequest getRequest() {
        return request;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamsRequest that = (ParamsRequest) o;
        return Objects.equals(request, that.request) &&
                Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, params);
    }
}

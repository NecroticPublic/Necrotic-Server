package ca.momoperes.canarywebhooks;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class PayloadMap extends HashMap<String, Object> {

    @Override
    public Object put(String key, Object value) {
        if (value instanceof Payload) {
            return put(key, ((Payload) value).toObject());
        } else if (value instanceof ArrayList) {
            PayloadArray array = new PayloadArray();
            @SuppressWarnings("rawtypes")
			ArrayList list = (ArrayList) value;
            for (Object o : list) {
                array.add(o);
            }
            return super.put(key, array);
        }
        return super.put(key, value);
    }

    public Object putIfExists(String key, Object value) {
        if (value == null) {
            return null;
        }
        return put(key, value);
    }

}

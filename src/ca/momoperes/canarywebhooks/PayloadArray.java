package ca.momoperes.canarywebhooks;

import org.json.simple.JSONArray;

public class PayloadArray extends JSONArray {
    @Override
    public boolean add(Object o) {
        if (o instanceof Payload) {
            o = ((Payload) o).toObject();
        }
        return super.add(o);
    }

    @Override
    public void add(int index, Object element) {
        if (element instanceof Payload) {
            element = ((Payload) element).toObject();
        }
        super.add(index, element);
    }
}

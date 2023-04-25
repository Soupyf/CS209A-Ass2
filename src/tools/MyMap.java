package tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class MyMap extends Observable {
    private final Map<String, Boolean> myMap;

    public MyMap() {
        super();
        myMap = new HashMap<>();
    }

    public void put(String key, boolean value) {
        myMap.put(key, value);
        setChanged();
        notifyObservers();
    }

    public void replace(String key, boolean value) {
        myMap.remove(key);
        myMap.put(key, value);
        setChanged();
        notifyObservers();
    }

    public boolean getValue(String key) {
        return myMap.get(key);
    }

}

package rainbow.cache.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rainbow.cache.Cache;
import rainbow.cache.CacheLoader;
import rainbow.core.model.object.SimpleNameObject;

public class MemoryCache<K, V> extends SimpleNameObject implements Cache<K, V> {

    private CacheLoader<K, V> loader;

    private Map<K, V> temp = new ConcurrentHashMap<K, V>();

    public void setLoader(CacheLoader<K, V> loader) {
        this.loader = loader;
    }

    @Override
    public void put(K key, V value) {
        temp.put(key, value);
    }

    @Override
    public V get(K key) {
        V value = null;
        value = temp.get(key);
        if (value == null) {
            value = loader.load(key);
            if (value != null) {
                temp.put(key, value);
            }
        }
        return value;
    }

    @Override
    public void remove(K key) {
        temp.remove(key);
    }

    @Override
    public void removeAll() {
        temp.clear();
    }

    @Override
    public void removeAll(Collection<K> keys) {
        for (K key : keys)
            remove(key);
    }

    @Override
    public V apply(K input) {
        return get(input);
    }

}

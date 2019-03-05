package rainbow.cache;

public interface CacheLoader<K, V> {

    V load(K key);

}

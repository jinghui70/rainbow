package rainbow.cache;

public interface CacheManager {

    <K, V> Cache<K, V> createCache(String name, CacheLoader<K, V> loader);

    <K, V> Cache<K, V> createCache(String name, CacheLoader<K, V> loader, CacheConfig config);

    void destoryCache(String name);
}

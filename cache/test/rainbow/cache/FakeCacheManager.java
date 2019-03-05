package rainbow.cache;

import rainbow.cache.Cache;
import rainbow.cache.CacheConfig;
import rainbow.cache.CacheLoader;
import rainbow.cache.CacheManager;
import rainbow.cache.internal.MemoryCache;

public class FakeCacheManager implements CacheManager {

    @Override
    public <K, V> Cache<K, V> createCache(String name, CacheLoader<K, V> loader) {
        return createCache(name, loader, null);
    }

    @Override
    public <K, V> Cache<K, V> createCache(String name, CacheLoader<K, V> loader, CacheConfig config) {
        MemoryCache<K, V> cache = new MemoryCache<K, V>();
        cache.setName(name);
        cache.setLoader(loader);
        return cache;
    }

    @Override
    public void destoryCache(String name) {
    }

}

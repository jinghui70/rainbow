package rainbow.cache;

import rainbow.cache.CacheManager;
import rainbow.cache.internal.CacheManagerImpl;

public final class CacheTestUtils {

    private CacheTestUtils() {
    }

    /**
     * 创建一个cacheManager
     * 
     * @return
     */
    public static CacheManager creatCacheManager() {
        return new CacheManagerImpl();
    }
}

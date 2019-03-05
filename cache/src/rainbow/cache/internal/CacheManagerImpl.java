package rainbow.cache.internal;

import rainbow.core.bundle.Bean;
import rainbow.core.util.ioc.InjectProvider;
import rainbow.cache.Cache;
import rainbow.cache.CacheConfig;
import rainbow.cache.CacheLoader;
import rainbow.cache.CacheManager;

@Bean(name = "cacheManager", extension = InjectProvider.class)
public class CacheManagerImpl extends InjectProvider implements CacheManager {

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

	@Override
	public Class<?> getInjectClass() {
		return CacheManager.class;
	}
	
	@Override
	public Object getInjectObject(String name, String destClassName) {
		return this;
	}

}

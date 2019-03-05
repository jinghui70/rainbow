package rainbow.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rainbow.cache.internal.CacheManagerImpl;

public class TestCacheManager {

    public static CacheManager manager;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        manager = new CacheManagerImpl();
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        final AtomicInteger time = new AtomicInteger(0);

        CacheLoader<Integer, String> loader = new CacheLoader<Integer, String>() {
            @Override
            public String load(Integer key) {
                time.incrementAndGet();
                return key.toString() + "T";
            }
        };
        Cache<Integer, String> cache = manager.createCache("test", loader);

        assertEquals("20T", cache.get(20));
        assertEquals(1, time.get());
        assertEquals("20T", cache.get(20));
        assertEquals(1, time.get());
        cache.remove(20);
        assertEquals("20T", cache.get(20));
        assertEquals(2, time.get());
        
    }
}

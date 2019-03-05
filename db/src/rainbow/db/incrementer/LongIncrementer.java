package rainbow.db.incrementer;

import java.util.concurrent.atomic.AtomicInteger;

import rainbow.core.platform.Platform;
import rainbow.core.util.Utils;

/**
 * 支持每秒并发10000
 * 
 * @author lijinghui
 * 
 */
public class LongIncrementer extends AbstractIncrementer {

	private AtomicInteger seed = new AtomicInteger(0);

	@Override
	protected long getNextKey() {
		seed.compareAndSet(10000, 0);
		return (System.currentTimeMillis() / 1000 * 100 + Platform.getId()) * 10000 + seed.getAndIncrement();
	}

	@Override
	public String nextStringValue() {
		return Utils.toString(getNextKey());
	}

}

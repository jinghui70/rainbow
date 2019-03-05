package rainbow.db.incrementer;

import java.util.concurrent.atomic.AtomicInteger;

import rainbow.db.dao.Dao;
import rainbow.db.dao.Select;

/**
 * 简单的Incrementer，使用这个Incrementer的代码不支持集群部署
 * 
 * @author lijinghui
 *
 */
public class SimpleIncrementer extends AbstractIncrementer {

	private AtomicInteger seed;

	public SimpleIncrementer(Dao dao, String entityName) {
		int value = dao.queryForInt(new Select("max(id)").from(entityName));
		seed = new AtomicInteger(value + 1);
	}

	@Override
	protected long getNextKey() {
		return seed.getAndIncrement();
	}
}

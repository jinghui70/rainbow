package rainbow.db.dao;

import org.junit.jupiter.api.Test;

import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.DataType;

class TestDialect {

	@Test
	void testAlterTable() {
		MemoryDao dao = new MemoryDao();

		Entity entity = new Entity("TestObj", new Column("id", DataType.VARCHAR, 22));
		dao.addEntity(entity);
	}

}

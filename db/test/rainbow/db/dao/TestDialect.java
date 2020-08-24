package rainbow.db.dao;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.Dialect;
import rainbow.db.database.H2;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.model.DataType;

class TestDialect {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ExtensionRegistry.registerExtensionPoint(null, Dialect.class);
		ExtensionRegistry.registerExtension(null, Dialect.class, new H2());
	}

	@Test
	void testAlterTable() {
		MemoryDao dao = new MemoryDao();

		Column c0 = new Column("id", DataType.VARCHAR, 22);
		c0.setKey(true);
		Column c1 = new Column("name", DataType.VARCHAR, 40);
		Column c2 = new Column("age", DataType.INT);
		Entity entity = new Entity("Person", Arrays.asList(c0, c1, c2));

		// 初始化
		dao.addEntity(entity);
		NeoBean neo = dao.newNeoBean("Person");
		neo.setValue("id", "007");
		neo.setValue("name", "James");
		neo.setValue("age", 50);
		dao.insert(neo);
		NeoBean neo1 = dao.fetch("Person", "007");
		assertEquals(neo1.getString("name"), "James");

		// drop table
		dao.dropTable("Person");
		try {
			dao.fetch("Person", "007");
			fail();
		} catch (DataAccessException e) {
			// Table should not found
		}

		// create Table
		dao.createTable("Person", Arrays.asList(c0, c1, c2));
		Column c3 = new Column("salary", 5, 2);
		Column c4 = new Column("birthday", DataType.DATE);
		entity.setColumns(Arrays.asList(c0, c1, c2, c3, c4));
		dao.addColumn("Person", c3, c4);
		neo.setValue("salary", 189.23);
		neo.setValue("birthday", LocalDate.of(1977, 1, 17));
		dao.insert(neo);
		neo1 = dao.fetch("Person", "007");
		assertEquals(Integer.valueOf(50), neo1.getInt("age"));
		assertEquals(BigDecimal.valueOf(189.23), neo1.getBigDecimal("salary"));
		LocalDate day = neo1.getValue("birthday", LocalDate.class);
		assertEquals(17, day.getDayOfMonth());
		assertEquals(1, day.getMonthValue());

		// alter column
		c3.setType(DataType.INT);
		c4.setType(DataType.VARCHAR);
		c4.setLength(11);
		dao.alterColumn("Person", c3, c4);
		neo1 = dao.fetch("Person", "007");
		assertEquals(Integer.valueOf(189), neo1.getInt("salary"));
		assertEquals("1977-01-17", neo1.getString("birthday"));

		// delete column
		dao.dropColumn("Person", "salary", "birthday");
		Map<String, Object> map = new Sql("select * from Person").fetchFirst(dao);
		assertEquals("James", map.get("name"));
		assertNull(map.get("salary"));
		assertNull(map.get("birthday"));
	}

}

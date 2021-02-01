package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import rainbow.core.extension.ExtensionRegistry;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.model.PureColumn;
import rainbow.db.database.Dialect;
import rainbow.db.database.H2;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.model.DataType;
import rainbow.db.model.Table;
import rainbow.db.model.TableBuilder;

class TestDialect {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ExtensionRegistry.registerExtensionPoint(null, Dialect.class);
		ExtensionRegistry.registerExtension(null, Dialect.class, new H2());
	}

	@Test
	void testAlterTable() {
		MemoryDao dao = new MemoryDao();
		Table table = new TableBuilder("Person") //
				.addField("ID").setName("id").setVarchar(22).setKey() //
				.addField("name").setVarchar(40) //
				.addField("age").setDataType(DataType.INT)//
				.build();
		dao.addTable(table);

		// 初始化
		NeoBean neo = dao.newNeoBean("Person");
		neo.setValue("id", "007");
		neo.setValue("name", "James");
		neo.setValue("age", 50);
		dao.insert(neo);
		NeoBean neo1 = dao.fetch("Person", "007");
		assertEquals(neo1.getString("name"), "James");

		// create Table
		PureColumn c3 = new PureColumn("salary", 5, 2);
		PureColumn c4 = new PureColumn("birthday", DataType.DATE);
		dao.addColumn("Person", c3, c4);

		neo.setValue("salary", 189.23);
		neo.setValue("birthday", LocalDate.of(1977, 1, 17));
		dao.update(neo);

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

		// drop table
		dao.dropTable("Person");
		try {
			dao.fetch("Person", "007");
			fail();
		} catch (DataAccessException e) {
			// Table should not found
		}
	}

}

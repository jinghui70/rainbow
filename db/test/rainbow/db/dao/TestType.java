package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rainbow.db.DBTest;
import rainbow.db.dao.memory.MemoryDao;

public class TestType {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestType.class.getResource("object/test.rdm"));
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		dao.destroy();
	}

	@BeforeEach
	public void setUp() throws Exception {
		dao.getJdbcTemplate().getTransactionManager().beginTransaction();
	}

	@AfterEach
	public void tearDown() throws Exception {
		dao.getJdbcTemplate().getTransactionManager().rollback();
	}

	@Test
	public void testChar() {
		NeoBean neo = dao.newNeoBean("TestType");
		neo.setValue("id", "1");
		
		// String
		neo.setValue("ch", "1"); 
		dao.insert(neo);
		neo = dao.fetch("TestType", "1");
		assertEquals(Boolean.TRUE, neo.getBool("ch"));
		assertEquals("1", neo.getString("ch"));

	}
}

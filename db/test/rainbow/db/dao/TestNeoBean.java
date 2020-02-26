package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rainbow.db.DBTest;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object._Person;

public class TestNeoBean {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestNeoBean.class.getResource("object/test.rdmx"));
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
	public void testFilter() {
		NeoBean neo = dao.makeNeoBean(_Person.li4());
		assertEquals(5, neo.valueColumns().size());
		neo.filterColumn((column) -> false);
		assertEquals(0, neo.valueColumns().size());
	}
}

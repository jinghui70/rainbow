package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rainbow.db.DBTest;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object._Gender;
import rainbow.db.dao.object._Person;

public class TestCondition {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestCondition.class.getResource("object/test.rdmx"));
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
	public void test() {
		dao.insert(_Person.zhang3());
		dao.insert(_Person.li4());
		dao.insert(_Person.wang5());

		C genderCnd = C.make("gender", _Gender.男).or("gender", _Gender.女);

		int count = dao.select().from("_Person").where("mobile", Op.GreaterEqual, 10).and(genderCnd).count();
		assertEquals(2, count);
		count = dao.select().from("_Person").where(genderCnd).and("mobile", Op.GreaterEqual, 10).count();
		assertEquals(2, count);
	}

}

package rainbow.db.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import rainbow.db.DBTest;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object._Gender;
import rainbow.db.dao.object._Person;
import rainbow.db.dao.object._SaleRecord;

public class TestDao {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestDao.class.getResource("object/test.rdmx"));
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

	private _Person createPerson(int id) {
		_Person p = new _Person();
		p.setId(id);
		p.setName("1");
		p.setBirthday(LocalDate.of(1970, 1, 27));
		p.setGender(_Gender.女);
		p.getScore()[0] = 5;
		p.getScore()[1] = 4;
		p.getScore()[2] = 3;
		return p;
	}

	@Test
	public void test() {
		_Person p = createPerson(10);
		dao.insert(p);
		assertEquals(1, dao.select().from("_Person").count());

		dao.delete(p);
		assertEquals(0, dao.select().from("_Person").count());

		dao.insert(p);
		p = createPerson(20);
		assertEquals(2, dao.select().from("_Person").count());

		// TODO 重新设计同名多列字段的方法并测试
	}

	@Test
	public void testBatch() {
		List<_Person> list = Lists.newArrayList();
		for (int i = 1; i <= 10; i++) {
			list.add(createPerson(i));
		}
		dao.insert(list, 5, null);
		assertEquals(10, dao.select().from("_Person").count());
	}

	@Test
	public void testDate() {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);

		NeoBean neo = dao.newNeoBean("_SaleRecord");
		neo.setValue("id", 1);
		neo.setValue("person", 1);
		neo.setValue("goods", 1);
		neo.setValue("qty", 1);
		neo.setValue("money", 1);
		neo.setValue("time", Dao.NOW);
		dao.insert(neo);

		_SaleRecord r = dao.select().from("_SaleRecord").where("time", Op.Greater, yesterday)
				.queryForObject(_SaleRecord.class);
		assertEquals(Integer.valueOf(1), r.getId());
		assertTrue(r.getTime().equals(today));

		neo = dao.newNeoBean("_SaleRecord");
		neo.setValue("id", 1);
		neo.setValue("time", Dao.NOW);
		dao.update(neo);
		r = dao.fetch(_SaleRecord.class, 1);
		assertEquals(today, r.getTime());

		r = dao.select().from("_SaleRecord").where("time", Op.LessEqual, Dao.NOW).queryForObject(_SaleRecord.class);
		assertNotNull(r);

	}
}

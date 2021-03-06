package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
import rainbow.db.dao.object._Org;
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

	private _Person createPerson(String id) {
		_Person p = new _Person();
		p.setId(id);
		p.setName("1");
		p.setBirthday(LocalDate.of(1970, 1, 27));
		p.setGender(_Gender.女);
		return p;
	}

	@Test
	public void test() {
		_Person p = createPerson("10");
		dao.insert(p);
		assertEquals(1, dao.select().from("_Person").count());

		dao.delete(p);
		assertEquals(0, dao.select().from("_Person").count());

		dao.insert(p);
		p = createPerson("20");
		assertEquals(1, dao.select().from("_Person").count());

		// TODO 重新设计同名多列字段的方法并测试
	}

	@Test
	public void testBatch() {
		List<_Person> list = Lists.newArrayList();
		for (int i = 1; i <= 10; i++) {
			list.add(createPerson(Integer.toString(i)));
		}
		dao.insert(list, 5, false);
		assertEquals(10, dao.select().from("_Person").count());
	}

	@Test
	public void testDate() {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);

		NeoBean neo = dao.newNeoBean("_SaleRecord");
		neo.setValue("id", 1);
		neo.setValue("inx", 1);
		neo.setValue("person", 1);
		neo.setValue("goods", 1);
		neo.setValue("qty", 1);
		neo.setValue("money", 1);
		neo.setValue("time", Dao.NOW);
		dao.insert(neo);

		_SaleRecord r = dao.select().from("_SaleRecord").where("time", Op.Greater, yesterday)
				.queryForObject(_SaleRecord.class);
		assertEquals("1", r.getId());
		assertTrue(r.getTime().equals(today));

		neo = dao.newNeoBean("_SaleRecord");
		neo.setValue("id", 1);
		neo.setValue("time", Dao.NOW);
		dao.update(neo);
		r = dao.fetch(_SaleRecord.class, "1", 1);
		assertEquals(today, r.getTime());

		r = dao.select().from("_SaleRecord").where("time", Op.LessEqual, Dao.NOW).queryForObject(_SaleRecord.class);
		assertNotNull(r);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testTree() {
		dao.insert(new _Org("1", null, "中国"));
		dao.insert(new _Org("2", "1", "北京"));
		dao.insert(new _Org("3", "1", "上海"));
		dao.insert(new _Org("2-1", "2", "东城区"));
		dao.insert(new _Org("2-2", "2", "朝阳区"));

		List<_Org> list = dao.select().from("_Org").orderBy("id").queryForTree(_Org.class, true);
		assertEquals(1, list.size());
		_Org first = list.get(0);
		assertEquals("中国", first.getName());
		assertEquals(2, first.getChildren().size());

		first = first.getChildren().get(0); // 北京
		first = first.getChildren().get(0);
		assertEquals("2-1", first.getId());
		assertEquals("东城区", first.getName());

		dao.insert(new _Org("4", "XXX", "NOWHERE"));
		List<Map<String, Object>> data = dao.select().from("_Org").orderBy("id").queryForTree(true);
		assertEquals(1, data.size());
		data = (List<Map<String, Object>>) data.get(0).get("children");
		assertEquals(2, data.size());

		data = (List<Map<String, Object>>) data.get(0).get("children");
		Map<String, Object> map = data.get(1);
		assertEquals("2-2", map.get("id"));
		assertEquals("朝阳区", map.get("name"));

	}
}

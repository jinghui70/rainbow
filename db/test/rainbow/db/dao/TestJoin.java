package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rainbow.db.DBTest;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object._Gender;
import rainbow.db.dao.object._Goods;
import rainbow.db.dao.object._Person;
import rainbow.db.dao.object._SaleRecord;

public class TestJoin {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestJoin.class.getResource("object/test.rdmx"));
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		dao.destroy();
	}

	@BeforeEach
	public void setUp() throws Exception {
		dao.getJdbcTemplate().getTransactionManager().beginTransaction();
		dao.insert(_Person.zhang3());
		dao.insert(_Person.li4());
		dao.insert(_Person.wang5());
		dao.insert(_Person.zhao6());
		dao.insert(_Goods.iPhone6());
		dao.insert(_Goods.iPhone7());
		dao.insert(_Goods.iPhoneX());
		dao.insert(_Goods.p30());
	}

	@AfterEach
	public void tearDown() throws Exception {
		dao.getJdbcTemplate().getTransactionManager().rollback();
	}

	@Test
	public void testSimple() {
		List<Map<String, Object>> list = dao.select("name,mobile.name:mobile").from("_Person")
				.where("gender", _Gender.男).orderBy("mobile.price").queryForMapList();
		assertEquals(2, list.size());
		Map<String, Object> data = list.get(0);
		assertEquals("李四", data.get("name"));
		assertEquals("P30", data.get("mobile"));
		data = list.get(1);
		assertEquals("张三", data.get("name"));
		assertEquals("iPhone7", data.get("mobile"));

		data = dao.select("name,mobile.name:mobile").from("_Person").where("id", 6).queryForMap();
		assertEquals("赵六", data.get("name"));
		assertNull(data.get("mobile"));

	}

	@Test
	public void testMany() {
		_SaleRecord r = new _SaleRecord("1", "3", "6", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		r = new _SaleRecord("2", "3", "7", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		r = new _SaleRecord("3", "3", "30", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
	}

}

package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import rainbow.db.dao.object._Goods;
import rainbow.db.dao.object._SaleRecord;

public class TestQuery {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestQuery.class.getResource("object/test.rdmx"));
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
	public void testSimple() {
		_SaleRecord r = new _SaleRecord("1", "3", "6", 1, 200, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		r = new _SaleRecord("2", "3", "6", 10, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		r = new _SaleRecord("3", "3", "30", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		
		_SaleRecord s = dao.select().from("_SaleRecord").where("id", "1").queryForObject(_SaleRecord.class);
		assertEquals("3", s.getPerson());
		assertEquals("6", s.getGoods());
		assertEquals(1, s.getQty());
		assertEquals(200d, s.getMoney());

		List<_SaleRecord> list = dao.select().from("_SaleRecord").where("person", "3").orderBy("qty desc, money")
				.queryForList(_SaleRecord.class);
		assertEquals(3, list.size());
		s = list.get(0);
		assertEquals("2", s.getId());
		assertEquals(10, s.getQty());
		assertEquals(100d, s.getMoney());
		s = list.get(2);
		assertEquals("1", s.getId());
		assertEquals(200d, s.getMoney());
		

		PageData<_SaleRecord> page = dao.select().from("_SaleRecord").orderBy("id").pageQuery(_SaleRecord.class, 2, 1);
		assertEquals(2, page.getData().size());
		assertEquals(3, page.getCount().intValue());

		list = dao.select().from("_SaleRecord").orderBy("id").pageQuery(_SaleRecord.class, 2, 2).getData();
		assertEquals(1, list.size());

		Select select = dao.select("goods,person,min(qty):min,max(qty):max").from("_SaleRecord")
				.groupBy("goods, person").orderBy("goods desc");

		// 用map的方式处理
		List<Map<String, Object>> l = select.queryForMapList();
		assertEquals(2, l.size());
		Map<String, Object> m = l.get(0);
		assertEquals("6", m.get("goods"));
		assertEquals(Double.valueOf(1), m.get("min"));
		assertEquals(Double.valueOf(10), m.get("max"));

		// 测试函数
		_Goods g = dao.select().from("_Goods").where("UPPER(name)", "IPHONE").queryForObject(_Goods.class);
		assertEquals(Integer.valueOf(3), g.getId());
	}

}

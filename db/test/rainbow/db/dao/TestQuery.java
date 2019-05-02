package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import rainbow.db.dao.object._JoinRecord;
import rainbow.db.dao.object._SaleRecord;
import rainbow.db.dao.object._SaleRecordCalc;

public class TestQuery {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestQuery.class.getResource("object/test.rdmx"));
		DBTest.loadDataFromExcel(dao, TestQuery.class.getResource("object/TestData.xlsx"));
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
		_SaleRecord s = dao.select().from("_SaleRecord").where("id", 1).queryForObject(_SaleRecord.class);
		assertEquals(1, s.getPerson());
		assertEquals(1, s.getGoods());
		assertEquals(1, s.getQty());
		assertEquals(100d, s.getMoney());

		List<_SaleRecord> list = dao.select().from("_SaleRecord").where("goods", 3).orderBy("qty desc, money")
				.queryForList(_SaleRecord.class);
		assertEquals(10, list.size());
		s = list.get(0);
		assertEquals(2, s.getPerson());
		assertEquals(10, s.getQty());
		assertEquals(10d, s.getMoney());

		PageData<_SaleRecord> page = dao.select().from("_SaleRecord").orderBy("id").pageQuery(_SaleRecord.class, 5);
		assertEquals(5, page.getRows().size());
		assertEquals(20, page.getTotal());

		list = dao.select().from("_SaleRecord").orderBy("id").queryForList(_SaleRecord.class, 11, 2);
		assertEquals(9, page.getRows().size());

		Select select = dao.select("goods,person,min(qty) as min,max(qty) as max").from("_SaleRecord")
				.groupBy("goods, person").orderBy("goods");

		// 用map的方式处理
		List<Map<String, Object>> l = select.queryForMapList();
		assertEquals(2, l.size());
		Map<String, Object> m = l.get(0);
		assertEquals(1, m.get("goods"));
		assertEquals(1, m.get("min"));
		assertEquals(10, m.get("max"));
		// 用对象方式处理
		List<_SaleRecordCalc> ll = select.queryForList(_SaleRecordCalc.class);
		assertEquals(2, ll.size());
		_SaleRecordCalc src = ll.get(0);
		assertEquals(1, src.getGoods());
		assertEquals(1, src.getMin());
		assertEquals(10, src.getMax());

		// 测试函数
		_Goods g = dao.select().from("_Goods").where("UPPER(name)", "IPHONE").queryForObject(_Goods.class);
		assertEquals(Integer.valueOf(3), g.getId());
	}

	@Test
	public void testSimpleJoin() {
		_JoinRecord j = dao.select("qty,person.name:person,goods.name:goods").from("_SaleRecord").where("id", 1)
				.queryForObject(_JoinRecord.class);
		assertEquals("张三", j.getPerson());
		assertEquals("Tesla ModelX", j.getGoods());
		assertEquals(1, j.getQty());

		List<Map<String, Object>> list = dao.select("person.name, goods.name").distinct().from("_SaleRecord")
				.orderBy("goods.name desc").queryForMapList();
		assertEquals(2, list.size());
		Map<String, Object> m = list.get(0);
		assertEquals("李四", m.get("person.name"));
		assertEquals("iPhone", m.get("goods.name"));
	}

}

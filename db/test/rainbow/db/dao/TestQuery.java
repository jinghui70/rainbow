package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
		dao = DBTest.createMemoryDao(TestQuery.class.getResource("object/test.rdm"));
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
		_SaleRecord s = dao.queryForObject(new Select().from("_SaleRecord").where("id", 1), _SaleRecord.class);
		assertEquals(1, s.getPerson());
		assertEquals(1, s.getGoods());
		assertEquals(1, s.getQty());
		assertEquals(100d, s.getMoney());

		List<_SaleRecord> list = dao.queryForList(
				new Select().from("_SaleRecord").where("goods", 3).orderBy("qty desc, money"), _SaleRecord.class);
		assertEquals(10, list.size());
		s = list.get(0);
		assertEquals(2, s.getPerson());
		assertEquals(10, s.getQty());
		assertEquals(10d, s.getMoney());

		PageData<_SaleRecord> page = dao.pageQuery(new Select().from("_SaleRecord").orderBy("id").paging(1, 5),
				_SaleRecord.class);
		assertEquals(5, page.getRows().size());
		assertEquals(20, page.getTotal());

		page = dao.pageQuery(new Select().from("_SaleRecord").orderBy("id").paging(2, 11), _SaleRecord.class);
		assertEquals(9, page.getRows().size());
		assertEquals(20, page.getTotal());

		Select select = new Select("goods,person,min(qty) as min,max(qty) as max").from("_SaleRecord")
				.groupBy("goods, person").orderBy("goods");

		// 用map的方式处理
		List<Map<String, Object>> l = dao.queryForMapList(select);
		assertEquals(2, l.size());
		Map<String, Object> m = l.get(0);
		assertEquals(1, m.get("goods"));
		assertEquals(1, m.get("min"));
		assertEquals(10, m.get("max"));
		// 用对象方式处理
		List<_SaleRecordCalc> ll = dao.queryForList(select, _SaleRecordCalc.class);
		assertEquals(2, ll.size());
		_SaleRecordCalc src = ll.get(0);
		assertEquals(1, src.getGoods());
		assertEquals(1, src.getMin());
		assertEquals(10, src.getMax());

		// 测试函数
		_Goods g = dao.queryForObject(new Select().from("_Goods").where("UPPER(name)", "IPHONE"), _Goods.class);
		assertEquals(Integer.valueOf(3), g.getId());
	}

	@Test
	public void testSimpleJoin() {
		_JoinRecord j = dao.queryForObject(
				new Select("P.name as person, G.name as goods, S.qty").from("_SaleRecord S, _Person P, _Goods G")
						.andJoin("S.person", "P.id").andJoin("S.goods", "G.id").and("S.id", 1),
				_JoinRecord.class);
		assertEquals("张三", j.getPerson());
		assertEquals("Tesla ModelX", j.getGoods());
		assertEquals(1, j.getQty());

		PageData<_JoinRecord> page = dao.pageQuery(
				new Select("P.name as person, G.name as goods, S.qty").from("_SaleRecord S, _Person P, _Goods G")
						.andJoin("S.person", "P.id").andJoin("S.goods", "G.id").paging(2, 10),
				_JoinRecord.class);
		assertEquals(10, page.getRows().size());
		assertEquals(20, page.getTotal());
		j = page.getRows().get(0);
		assertEquals("李四", j.getPerson());
		assertEquals("iPhone", j.getGoods());

		List<_JoinRecord> list = dao.queryForList(
				new Select("P.name as person, G.name as goods").distinct().from("_SaleRecord S, _Person P, _Goods G")
						.andJoin("S.person", "P.id").andJoin("S.goods", "G.id").orderBy("G.name desc"),
				_JoinRecord.class);
		assertEquals(2, list.size());
		j = list.get(0);
		assertEquals("李四", j.getPerson());
		assertEquals("iPhone", j.getGoods());

		list = dao.queryForList(
				new Select("P.name as person, G.name as goods").distinct().from("_SaleRecord S, _Person P, _Goods G")
						.andJoin("S.person", "P.id").andJoin("S.goods", "G.id").orderBy("goods desc"),
				_JoinRecord.class);
		assertEquals(2, list.size());
		j = list.get(0);
		assertEquals("李四", j.getPerson());
		assertEquals("iPhone", j.getGoods());

		list = dao.queryForList(
				new Select("P.name as person, G.name as goods, sum(S.qty) as qty").distinct()
						.from("_SaleRecord S, _Person P, _Goods G").andJoin("S.person", "P.id")
						.andJoin("S.goods", "G.id").groupBy("P.name, goods").orderBy("goods desc"),
				_JoinRecord.class);
		assertEquals(2, list.size());
		j = list.get(0);
		assertEquals("李四", j.getPerson());
		assertEquals("iPhone", j.getGoods());
		assertEquals(55, j.getQty());
	}

	@Test
	public void TestSimpleJoinEx() {
		Select select = new Select("P.name,S.*").from("_SaleRecord S, _Person P").andJoin("S.person", "P.id")
				.and("S.id", 1);
		Map<String, Object> m = dao.queryForMap(select);
		assertEquals("张三", m.get("name"));
		assertEquals(1, m.get("id"));
		assertEquals(1, m.get("qty"));
		assertEquals(Double.valueOf(100), m.get("money"));

		select = new Select().from("_SaleRecord S, _Person P").andJoin("S.person", "P.id").and("S.id", 1);
		m = dao.queryForMap(select);
		assertEquals("张三", m.get("name"));
		assertEquals(1, m.get("id"));
		assertEquals(1, m.get("qty"));
		assertEquals(Double.valueOf(100), m.get("money"));

		select = new Select("S.*,P.*,S.id as sid,P.id as pid").from("_SaleRecord S, _Person P")
				.andJoin("S.person", "P.id").and("S.id", 1);
		m = dao.queryForMap(select);
		assertEquals("张三", m.get("name"));
		assertEquals(1, m.get("sid"));
		assertEquals(1, m.get("pid"));
		assertEquals(1, m.get("qty"));
		assertEquals(Double.valueOf(100), m.get("money"));
	}

	@Test
	public void TestJoin() {
		Join join = Join.make("_SaleRecord S").join("_Person P").on("person", "id").join("_Goods G").on("goods", "id");
		_JoinRecord j = dao.queryForObject(
				new Select("P.name as person, G.name as goods, S.qty").from(join).where("S.id", 1), _JoinRecord.class);
		assertEquals("张三", j.getPerson());
		assertEquals("Tesla ModelX", j.getGoods());
		assertEquals(1, j.getQty());

		Select select = new Select("P.name, G.name AS gg");
		join = Join.make("_Person P").join("_Goods G").on("id", "id");
		int count = dao.count(select.from(join));
		assertEquals(1, count);
		join = Join.make("_Person P").leftJoin("_Goods G").on("id", "id");
		List<Map<String, Object>> list = dao.queryForMapList(select.from(join));
		assertEquals(2, list.size());
		assertEquals("李四", list.get(1).get("name"));
		assertEquals(null, list.get(1).get("gg"));
		join = Join.make("_Person P").rightJoin("_Goods G").on("id", "id");
		list = dao.queryForMapList(select.from(join));
		assertEquals(2, list.size());
		assertEquals(null, list.get(1).get("name"));
		assertEquals("iPhone", list.get(1).get("gg"));
		
		join = Join.make("_SaleRecord S").join("_Person P").on("person", "id").on("id", "id"); // 多字段join，借id一用
		j = dao.queryForObject(new Select("P.name as person, S.qty").from(join), _JoinRecord.class);
		assertNotNull(j);
	}
}

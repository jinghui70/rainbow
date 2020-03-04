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
import rainbow.db.dao.condition.C;
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
	}

	@AfterEach
	public void tearDown() throws Exception {
		dao.getJdbcTemplate().getTransactionManager().rollback();
	}

	@Test
	public void testSimple() {
		dao.insert(_Person.zhang3());
		dao.insert(_Person.li4());
		dao.insert(_Person.wang5());
		dao.insert(_Person.zhao6());
		dao.insert(_Goods.iPhone6());
		dao.insert(_Goods.iPhone7());
		dao.insert(_Goods.iPhoneX());
		dao.insert(_Goods.p30());

		List<Map<String, Object>> list = dao.select("name,mobile.name:mobile").from("_Person")
				.where("gender", _Gender.男).orderBy("mobile.price").queryForList();
		assertEquals(2, list.size());
		Map<String, Object> data = list.get(0);
		assertEquals("李四", data.get("name"));
		assertEquals("P30", data.get("mobile"));
		data = list.get(1);
		assertEquals("张三", data.get("name"));
		assertEquals("iPhone7", data.get("mobile"));

		data = dao.select("name,mobile.name:mobile").from("_Person").where("id", 6).queryForObject();
		assertEquals("赵六", data.get("name"));
		assertNull(data.get("mobile"));

	}

	@Test
	public void testMany() {
		_SaleRecord r = new _SaleRecord("1", 1, "3", "6", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		r = new _SaleRecord("1", 2, "3", "7", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
		r = new _SaleRecord("1", 3, "3", "30", 1, 100, LocalDate.of(2019, 5, 5));
		dao.insert(r);
	}

	/**
	 * 测试一下当链接的对象没有数据的时候，条件放在不同位置的处理结果
	 */
	@Test
	public void testJoinNull() {
		_Person zhang3 = _Person.zhang3();
		dao.insert(zhang3);
		_Person li4 = _Person.li4();
		dao.insert(li4);
		_Goods iPhone6 = _Goods.iPhone6();
		dao.insert(iPhone6);
		_Goods iPhoneX = _Goods.iPhoneX();
		dao.insert(iPhoneX);

		// 插入一条正常记录
		_SaleRecord r = new _SaleRecord("1", 1) // 第一条记录
				.person(zhang3.getId()) // 张三销售
				.goods(iPhone6.getId()) // 卖了IPhone6
				.qty(100) // 卖了100台
				.money(1000) // 挣了1000元
				.time(LocalDate.now());
		dao.insert(r);

		// 再插入一条正常记录
		r = new _SaleRecord("1", 2) // 第二条记录
				.person(li4.getId()) // 李四销售
				.goods(iPhoneX.getId()) // 卖了IPhoneX
				.qty(100) // 卖了100台
				.money(1000) // 挣了1000元
				.time(LocalDate.now());
		dao.insert(r);

		// 插入一条没有对应商品的记录
		r = new _SaleRecord("1", 3) // 第二条记录
				.person(zhang3.getId()) // 张三销售
				.goods("1") // 故意写错的商品
				.qty(100) // 也卖了100台
				.money(1000) // 挣了1000元
				.time(LocalDate.now());
		dao.insert(r);

		// 因为左链接，查出来两条记录
		Select select = dao.select("person.name,goods.name:goods").from("_SaleRecord").orderBy("inx");
		List<Map<String, Object>> list = select.queryForList();
		assertEquals(3, list.size());
		Map<String, Object> v = list.get(2);
		assertNull(v.get("goods"));

		// 条件加在where，只能查出来两条
		select.where("person.id", zhang3.getId());
		list = select.queryForList();
		assertEquals(2, list.size());

		// 条件加在join，能查出来三条
		select.where(C.make()); // 清空条件
		select.setLinkCnds("person", C.make("id", zhang3.getId()));
		list = select.queryForList();
		assertEquals(3, list.size());
	}

	@Test
	void testExternalLink() {
		_Person zhang3 = _Person.zhang3();
		dao.insert(zhang3);
		_Goods iPhone6 = _Goods.iPhone6();
		dao.insert(iPhone6);

		// 插入一条正常记录
		_SaleRecord r = new _SaleRecord("1", 1) // 第一条记录
				.person(zhang3.getId()) // 张三销售
				.goods(iPhone6.getId()) // 卖了IPhone6
				.qty(100) // 卖了100台
				.money(1000) // 挣了1000元
				.time(LocalDate.now());
		NeoBean neo = dao.makeNeoBean("_SaleRecordNoLink", r);
		dao.insert(neo);

		Select select = dao.select("id,person.name:person").from("_SaleRecordNoLink")
				.extraLink("person", "person", "_Person", "id").orderBy("inx");
		List<Map<String, Object>> list = select.queryForList();
		assertEquals(1, list.size());
		Map<String, Object> map = list.get(0);
		assertEquals("张三", map.get("person"));
	}
}

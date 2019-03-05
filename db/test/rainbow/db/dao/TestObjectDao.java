package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import rainbow.core.util.ioc.Bean;
import rainbow.core.util.ioc.Context;
import rainbow.db.DBTest;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object.ObjectDao;
import rainbow.db.dao.object._Report;
import rainbow.db.dao.object._Unit;

public class TestObjectDao {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestObjectDao.class.getResource("object/test.rdm"));
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

	// 测试直接创建使用
	@Test
	public void testDirectUse() {
		ObjectDao<_Report> obDao = new ObjectDao<_Report>(dao, _Report.class);
		obDao.insert(createR1());
		obDao.insert(createR2());
		_Report r = obDao.fetch(1, _Unit.Y);
		checkR1(r);

		List<_Report> list = obDao.getAll();
		checkR1(list.get(0));
		checkR2(list.get(1));
	}

	@Test
	public void testAsBean() {
		Map<String, Bean> beans = ImmutableMap.<String, Bean> of("dao", Bean.singleton(dao, Dao.class), //
				"reportDao", Bean.singleton(_ReportDao.class));
		Context c = new Context(beans);
		_ReportDao obDao = c.getBean("reportDao", _ReportDao.class);
		obDao.insert(createR1());
		obDao.insert(createR2());
		_Report r = obDao.fetch(1, _Unit.Y);
		checkR1(r);
	}

	private _Report createR1() {
		_Report r = new _Report();
		r.setCount(1);
		r.setUnit(_Unit.Y);
		r.setMoney(100);
		return r;
	}

	private _Report createR2() {
		_Report r = new _Report();
		r.setCount(2);
		r.setUnit(_Unit.M);
		r.setMoney(200);
		return r;
	}

	private void checkR1(_Report r) {
		assertEquals(1, r.getCount());
		assertEquals(_Unit.Y, r.getUnit());
		assertEquals(100.0, r.getMoney(), 0.0d);
	}

	private void checkR2(_Report r) {
		assertEquals(2, r.getCount());
		assertEquals(_Unit.M, r.getUnit());
		assertEquals(200.0, r.getMoney(), 0.0d);
	}

}

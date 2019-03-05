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

import com.google.common.collect.ImmutableMap;

import rainbow.core.util.ioc.Bean;
import rainbow.core.util.ioc.Context;
import rainbow.db.DBTest;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object.IdDao;
import rainbow.db.dao.object._Gender;
import rainbow.db.dao.object._Person;

public class TestIdDao {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestIdDao.class.getResource("object/test.rdm"));
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
		IdDao<Integer,_Person> obDao = new IdDao<Integer,_Person>(dao, _Person.class);
		obDao.insert(createR1());
		obDao.insert(createR2());
		_Person r = obDao.fetch(1);
		checkR1(r);

		List<_Person> list = obDao.getAll();
		checkR1(list.get(0));
		checkR2(list.get(1));
	}

	// 测试直接创建且不使用incrementer场景
	@Test
	public void testDirectUse1() {
		IdDao<Integer,_Person> obDao = new IdDao<Integer,_Person>(dao, _Person.class, null);
		obDao.insert(createR1());
		obDao.insert(createR2());
		List<_Person> list = obDao.getAll();
		assertEquals(Integer.valueOf(10), list.get(0).getId());
		assertEquals(Integer.valueOf(20), list.get(1).getId());
	}

	/**
	 * 测试派生后容器创建场景
	 */
	@Test
	public void testAsBean() {
		Map<String, Bean> beans = ImmutableMap.<String, Bean> of("dao", Bean.singleton(dao, Dao.class), //
				"personDao", Bean.singleton(_PersonDao.class));
		Context c = new Context(beans);
		_PersonDao obDao = c.getBean("personDao", _PersonDao.class);
		obDao.insert(createR1());
		obDao.insert(createR2());
		_Person r = obDao.fetch(1);
		checkR1(r);
		
		List<_Person> list = obDao.getAll();
		checkR1(list.get(0));
		checkR2(list.get(1));
	}

	/**
	 * 测试派生后容器创建场景，不使用Incrementer
	 */
	@Test
	public void testAsBean1() {
		Map<String, Bean> beans = ImmutableMap.<String, Bean> of("dao", Bean.singleton(dao, Dao.class), //
				"personDao", Bean.singleton(_PersonDao1.class));
		Context c = new Context(beans);
		_PersonDao1 obDao = c.getBean("personDao", _PersonDao1.class);
		obDao.insert(createR1());
		obDao.insert(createR2());
		List<_Person> list = obDao.getAll();
		assertEquals(Integer.valueOf(10), list.get(0).getId());
		assertEquals(Integer.valueOf(20), list.get(1).getId());
	}
	
	private _Person createR1() {
		_Person p = new _Person();
		p.setId(10);
		p.setName("1");
		p.setBirthday(LocalDate.of(1970,1,27));
		p.setGender(_Gender.女);
		p.getScore()[0] = 5;
		p.getScore()[1] = 4;
		p.getScore()[2] = 3;
		return p;
	}

	private void checkR1(_Person p) {
		assertEquals(Integer.valueOf(1), p.getId());
		assertEquals("1", p.getName());
		assertEquals("1970-01-27", p.getBirthday().toString());
		assertEquals(_Gender.女, p.getGender());
		assertEquals(Integer.valueOf(5),p.getScore()[0]);
		assertEquals(Integer.valueOf(4),p.getScore()[1]);
		assertEquals(Integer.valueOf(3),p.getScore()[2]);
	}

	private _Person createR2() {
		_Person p = new _Person();
		p.setId(20);
		p.setName("2");
		p.setBirthday(LocalDate.of(1974,11,9));
		p.setGender(_Gender.男);
		p.getScore()[0] = 98;
		p.getScore()[1] = 99;
		p.getScore()[2] = 100;
		return p;
	}

	private void checkR2(_Person p) {
		assertEquals(Integer.valueOf(2), p.getId());
		assertEquals("2", p.getName());
		assertEquals("1974-11-09", p.getBirthday().toString());
		assertEquals(_Gender.男, p.getGender());
		assertEquals(Integer.valueOf(98),p.getScore()[0]);
		assertEquals(Integer.valueOf(99),p.getScore()[1]);
		assertEquals(Integer.valueOf(100),p.getScore()[2]);
	}

}

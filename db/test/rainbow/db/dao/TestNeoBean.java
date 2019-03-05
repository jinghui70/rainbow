package rainbow.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rainbow.db.DBTest;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.object._Gender;
import rainbow.db.dao.object._Person;
import rainbow.db.dao.object._Person2;
import rainbow.db.dao.object._Score;

public class TestNeoBean {

	private static MemoryDao dao;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		dao = DBTest.createMemoryDao(TestNeoBean.class.getResource("object/test.rdm"));
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
	public void testArray() {
		_Person p = new _Person();
		p.setId(1);
		p.setBirthday(LocalDate.of(2009, 7, 13));
		p.setGender(_Gender.男);
		p.getScore()[0] = 98;
		p.getScore()[1] = 99;
		p.getScore()[2] = 100;
		dao.insert(p);
		p = dao.fetch(_Person.class, 1);
		assertEquals(LocalDate.of(2009, 7, 13), p.getBirthday());
		assertEquals(_Gender.男, p.getGender());
		assertEquals(Integer.valueOf(98), p.getScore()[0]);
		assertEquals(Integer.valueOf(99), p.getScore()[1]);
		assertEquals(Integer.valueOf(100), p.getScore()[2]);
	}

	@Test
	public void testSub() {
		_Person2 p = new _Person2();
		p.setId(1);
		p.setBirthday(LocalDate.of(2009, 7, 13));
		p.setGender(_Gender.男);
		_Score score = new _Score();
		p.setScore(score);
		score.setEnglish(100);
		score.setMath(99);
		score.setYuwen(98);
		dao.insert(p);
		p = dao.fetch(_Person2.class, 1);
		assertEquals(LocalDate.of(2009, 7, 13), p.getBirthday());
		assertEquals(_Gender.男, p.getGender());
		assertEquals(98, p.getScore().getYuwen());
		assertEquals(99, p.getScore().getMath());
		assertEquals(100, p.getScore().getEnglish());

	}
}

package rainbow.core.util.ioc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 测试IOC代码的正确性
 * 
 * @author lijinghui
 * 
 */
public class TestIOC {

	@Test
	public void testGetBean() {
		long now = System.currentTimeMillis();
		Context container = new Context(ImmutableMap.of( //
				"depend", Bean.singleton(Double.valueOf(1827.99)), //
				"timestamp", Bean.singleton(Long.valueOf(now)), //
				"number", Bean.singleton(Long.valueOf(10)), //
				"tom", Bean.singleton(TObject.class), //
				"kitty", Bean.prototype(TObject.class) //
		));
		TObject object1 = container.getBean("kitty", TObject.class);
		try {
			Thread.sleep(100); // 如果不这么写，多核的cpu有时候object2会先于object1设置时间
		} catch (InterruptedException e) {
		}
		TObject object2 = container.getBean("kitty", TObject.class);
		object2.setNumber(container.getBean("number", Long.class));
		assertNotSame(object1, object2);
		assertEquals(5, object1.getNumber().longValue()); // setNumber没写@Inject不会自动注入
		assertEquals(10, object2.getNumber().longValue());
		assertEquals(Double.valueOf(1827.99), object1.getDepend());
		assertEquals(now, object1.getTimestamp().longValue());
		assertEquals(object1.getTimestamp(), object2.getTimestamp());

		container.setBean("number", Long.valueOf(20));
		object1 = container.getBean("tom", TObject.class);
		object2 = (TObject) container.getBean("tom");
		object2.setNumber(container.getBean("number", Long.class));
		assertSame(object1, object2);
		assertEquals(20, object1.getNumber().longValue());
		assertEquals(now, object1.getTimestamp().longValue());

		object1 = container.getBean(TObject.class);
		assertNotNull(object1);
	}

	@Test
	public void testGetBeanNoDepend() {
		Context container = new Context(ImmutableMap.of( //
				"kitty", Bean.prototype(TDepend.class) //
		));
		try {
			container.getBean("kitty", TDepend.class);
			fail();
		} catch (BeanInitializationException e) {
		}
	}

	@Test
	public void testInject() {
		Context container = new Context(ImmutableMap.of( //
				"age", Bean.singleton(Integer.valueOf(26)), //
				"name", Bean.singleton(String.valueOf("rainbow")), //
				"email", Bean.singleton(String.valueOf("rayboy@rainbow.com")), //
				"kitty", Bean.singleton(TDepend.class) //
		));
		TDepend object = container.getBean("kitty", TDepend.class);
		assertEquals(Integer.valueOf(26), object.getAge());
		assertEquals(object.getName(), object.getName(), "rainbow");
		assertEquals(object.getEmail(), object.getEmail(), "rayboy@rainbow.com");
	}

	@Test
	public void testSetBean() {
		long now = System.currentTimeMillis();
		Context container = new Context(ImmutableMap.of( //
				"depend", Bean.singleton(Double.class), //
				"timestamp", Bean.singleton(Long.valueOf(now), Long.class), //
				"main", Bean.prototype(TObject.class) //
		));
		try {
			container.setBean("depend", Boolean.TRUE);
			fail();
		} catch (BeanNotOfRequiredTypeException e) {
		}
		container.setBean("depend", Double.valueOf(2011.10));
		TObject main = container.getBean(TObject.class);
		assertEquals(2011.1, main.getDepend(), 0.00001);
		assertEquals(now, main.getTimestamp().longValue());
	}

}

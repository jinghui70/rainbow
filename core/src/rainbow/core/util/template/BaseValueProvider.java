package rainbow.core.util.template;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class BaseValueProvider extends ValueProviderAdapter {

	private Map<String, IteratorWrap> loopContext;

	@Override
	public String getValue(String token) {
		if (token.startsWith("inx_")) {
			return Integer.toString(getLoopInx(token.substring(4)));
		}
		return token;
	}

	protected void putLoopContext(String loopName, Iterator<?> i) {
		if (loopContext == null)
			loopContext = Maps.newHashMap();
		loopContext.put(loopName, new IteratorWrap(i));
	}

	protected void putLoopContext(String loopName, Collection<?> c) {
		putLoopContext(loopName, c.iterator());
	}

	public final boolean inLoop(String loopName) {
		return loopContext != null && loopContext.containsKey(loopName);
	}

	@Override
	public void startLoop(String loopName) {
	}

	@Override
	public final boolean loopNext(String loopName) {
		if (!inLoop(loopName))
			return false;
		IteratorWrap i = loopContext.get(loopName);
		if (i.hasNext()) {
			i.next();
			return true;
		} else {
			loopContext.remove(loopName);
			return false;
		}
	}

	/**
	 * 返回指定循环的当前对象
	 * 
	 * @param loopName
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getLoopObject(String loopName, Class<T> clazz) {
		IteratorWrap i = loopContext.get(loopName);
		return (T) i.getCurObject();
	}

	/**
	 * 返回指定循环的循环次数
	 * 
	 * @param loopName
	 * @return
	 */
	protected int getLoopInx(String loopName) {
		IteratorWrap i = loopContext.get(loopName);
		return i.getInx();
	}

}

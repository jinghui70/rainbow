package rainbow.core.util.template;

import java.util.Iterator;

public class IteratorWrap {

	private int inx = 0;

	private Object curObject = null;

	private Iterator<?> iterator;

	public IteratorWrap(Iterator<?> i) {
		this.iterator = i;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Object next() {
		inx++;
		curObject = iterator.next();
		return curObject;
	}

	public int getInx() {
		return inx;
	}

	public Object getCurObject() {
		return curObject;
	}

}

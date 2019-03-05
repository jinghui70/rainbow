package rainbow.db.incrementer;

import com.google.common.base.Strings;

public abstract class AbstractIncrementer implements Incrementer {

	protected int paddingLength = 0;

	public int getPaddingLength() {
		return paddingLength;
	}

	public void setLength(int paddingLength) {
		this.paddingLength = paddingLength;
	}

	@Override
	public int nextIntValue() {
		return (int) getNextKey();
	}

	@Override
	public long nextLongValue() {
		return getNextKey();
	}

	@Override
	public String nextStringValue() {
		String s = Long.toString(getNextKey());
		return Strings.padStart(s, paddingLength, '0');
	}

	protected abstract long getNextKey();

}

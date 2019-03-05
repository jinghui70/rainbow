package rainbow.core.util.template;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import rainbow.core.util.Utils;

public class Parser {

	private String template;

	private String openFlag;

	private String closeFlag;

	public Parser(String template, String openFlag, String closeFlag) {
		this.template = template;
		this.openFlag = openFlag;
		this.closeFlag = closeFlag;
	}

	public List<Part> parse() {
		ImmutableList.Builder<Part> builder = ImmutableList.builder();
		int index = 0;
		int limit = template.length();
		while (index < limit) {
			TokenPos pos = findToken(index, limit);
			if (pos == null) {
				builder.add(new TextPart(template.substring(index)));
				break;
			} else {
				int len = pos.start - index;
				if (len > 0)
					builder.add(new TextPart(template.substring(index, pos.start)));
				index = pos.end;
				if ("loop".equals(pos.token)) {
					index = doLoopThings(index, limit, pos.flag, builder);
				} else if ("switch".equals(pos.token)) {
					index = doSwitchThings(index, limit, pos.flag, builder);
				} else if ("if".equals(pos.token)) {
					index = doIfThings(index, limit, pos.flag, builder);
				} else {
					builder.add(new TokenPart(pos.token));
				}
			}
		}
		return builder.build();
	}

	private void checkState(boolean state, String msg, int index) {
		if (!state)
			throw new IllegalArgumentException(String.format("%s [%s..]", msg, template.substring(0, index)));
	}

	private int doLoopThings(int index, int limit, String flag, ImmutableList.Builder<Part> builder) {
		String endLoop = flag.isEmpty() ? makeToken("end") : makeToken("end", flag);
		int end = findString(index, limit, endLoop);
		checkState(end > 0, "no [end] of loop found after", index);
		String sub = template.substring(index, end);
		List<Part> subParts = new Parser(sub, openFlag, closeFlag).parse();
		builder.add(new LoopPart(flag, subParts));
		return end + endLoop.length();
	}

	private int doSwitchThings(int index, int limit, String flag, ImmutableList.Builder<Part> builder) {
		checkState(!flag.isEmpty(), "switch must have a flag", index);
		String endSwitch = makeToken("end", flag);
		limit = findString(index, limit, endSwitch) - 1;
		checkState(limit > 0, "no [end] of switch found after", index);

		String c = new StringBuilder().append(openFlag).append("case ").append(flag).append(" ").toString();
		ImmutableMap.Builder<String, List<Part>> mapBuilder = ImmutableMap.builder();
		String key = null;
		while (index < limit) {
			int caseInx = findString(index, limit, c);
			if (caseInx > 0) {
				if (key != null) {
					String str = template.substring(index, caseInx);
					mapBuilder.put(key, new Parser(str, openFlag, closeFlag).parse());
				}
				int caseEnd = findString(caseInx + c.length(), limit, closeFlag);
				key = template.substring(caseInx + c.length(), caseEnd);
				index = caseEnd + closeFlag.length();
			} else {
				checkState(key != null, "no case found", index);
				String str = template.substring(index, limit + 1);
				mapBuilder.put(key, new Parser(str, openFlag, closeFlag).parse());
				index = limit;
			}
		}
		builder.add(new SwitchPart(flag, mapBuilder.build()));
		return limit + endSwitch.length() + 1;
	}

	private int doIfThings(int index, int limit, String flag, ImmutableList.Builder<Part> builder) {
		checkState(!flag.isEmpty(), "if must have a flag", index);
		String endIf = makeToken("end", flag);
		int end = findString(index, limit, endIf);
		checkState(end > 0, "no [end] of if found after", index);

		IfPart ifPart = new IfPart(flag);
		String c = makeToken("else", flag);

		int elseInx = findString(index, end, c);
		if (elseInx > 0) {
			String str = template.substring(index, elseInx);
			ifPart.setTrueParts(new Parser(str, openFlag, closeFlag).parse());
			str = template.substring(elseInx + c.length(), end);
			ifPart.setFalseParts(new Parser(str, openFlag, closeFlag).parse());
		} else {
			String str = template.substring(index, end);
			ifPart.setTrueParts(new Parser(str, openFlag, closeFlag).parse());
		}
		builder.add(ifPart);
		return end + endIf.length();
	}

	private String makeToken(String token, String... args) {
		StringBuilder sb = new StringBuilder().append(openFlag).append(token);
		for (String arg : args) {
			sb.append(" ").append(arg);
		}
		sb.append(closeFlag);
		return sb.toString();
	}

	private boolean match(int index, CharSequence flag) {
		boolean match = true;
		for (int i = 1; i < flag.length(); i++) {
			if ((index + i >= template.length()) || template.charAt(index + i) != flag.charAt(i)) {
				match = false;
				break;
			}
		}
		return match;
	}

	private int findString(int index, int limit, CharSequence input) {
		while (index < limit) {
			if (template.charAt(index) == input.charAt(0)) {
				if (match(index, input)) {
					return index;
				}
			}
			index++;
		}
		return -1;
	}

	/**
	 * 返回下一个token信息，没有token了就返回空
	 * 
	 * @param index
	 * @param t
	 * @return
	 */
	private TokenPos findToken(int index, int limit) {
		int next = findString(index, limit, openFlag);
		if (next == -1)
			return null;
		TokenPos result = new TokenPos();
		result.start = next;
		index = next + openFlag.length();

		next = findString(index, limit, closeFlag);
		checkState(next > 0, "no closeFlag after", index);
		checkState(next > index, "null token found", index);

		String str = template.substring(index, next);
		index = str.indexOf(" ");
		if (index == -1) {
			result.token = str;
			result.flag = Utils.NULL_STR;
		} else {
			result.token = str.substring(0, index);
			result.flag = str.substring(index + 1);
		}
		result.end = next + closeFlag.length();
		return result;
	}

	private class TokenPos {
		int start;
		int end;
		String token;
		String flag;
	}
}

package rainbow.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.UUID;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.BaseEncoding;

import rainbow.core.model.exception.AppException;

/**
 * 通用工具，弥补guava没有提供的常用工具类
 * 
 * @author lijinghui
 * 
 */
public abstract class Utils {

	public static final char[] HEXBYTES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static final String[] NUMBER_CN = { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" };

	/**
	 * 空数组常量
	 */
	public static final Object[] NULL_ARRAY = new Object[0];

	/**
	 * 空字符串常量
	 */
	public static final String NULL_STR = "";

	/**
	 * 检查输入的容器对象是不是为 null或者为空
	 * 
	 * @param c
	 *            检查用的容器对象
	 * @return {@code true} 如果==null or isEmpty()
	 */
	public static boolean isNullOrEmpty(Collection<?> c) {
		return c == null ? true : c.isEmpty();
	}

	/**
	 * 检查一个Map是不是为null或者为空
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isNullOrEmpty(Map<?, ?> map) {
		return map == null ? true : map.isEmpty();
	}

	/**
	 * 从一个map中获得key对应的value, map为空时返回null
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	public static <K, V> V safeGet(Map<K, V> map, K key) {
		return (isNullOrEmpty(map)) ? null : map.get(key);
	}

	/**
	 * 去除字符串中的空白
	 * 
	 * @param string
	 * @return
	 */
	public static String trimBlank(String string) {
		return trimString(string, ' ', '\r', '\t', '\n');
	}

	/**
	 * 去除字符串中的指定字符
	 * 
	 * @param string
	 * @param chars
	 * @return
	 */
	public static String trimString(String string, char... chars) {
		if (Strings.isNullOrEmpty(string) || chars.length == 0)
			return NULL_STR;
		StringBuilder sb = new StringBuilder(string.length());
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			boolean ok = true;
			for (char cc : chars) {
				if (cc == c) {
					ok = false;
					break;
				}
			}
			if (ok)
				sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 返回 {@code true} 当输入的字符串有内容时
	 * 
	 * @param string
	 *            检查用的字符串
	 * @return {@code true} 如果不为空且去掉头尾的空格和TAB后还有内容
	 */
	public static boolean hasContent(String string) {
		return string != null && !string.trim().isEmpty();
	}

	public static Predicate<String> startWith(final String prefix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.startsWith(prefix);
			}
		};
	}

	public static Predicate<String> endsWith(final String suffix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.endsWith(suffix);
			}
		};
	}

	/**
	 * 重复一个字符串n次，并用指定分隔符分开
	 * 
	 * @param sb
	 * @param str
	 * @param delimiter
	 * @param count
	 */
	public static void repeat(StringBuilder sb, String str, char delimiter, int count) {
		if (count < 1)
			return;
		if (count > 1) {
			for (int i = 1; i < count; i++) {
				sb.append(str);
				sb.append(delimiter);
			}
		}
		sb.append(str);
	}

	/**
	 * 把一个byte数组转为16进制表示
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHex(byte[] b) {
		int len = b.length;
		char[] s = new char[len * 2];
		for (int i = 0, j = 0; i < len; i++) {
			int c = (b[i]) & 0xff;
			s[j++] = HEXBYTES[c >> 4 & 0xf];
			s[j++] = HEXBYTES[c & 0xf];
		}
		return new String(s);
	}

	/**
	 * 用一个字符来分割字符串，返回一个分割好的字符串数组
	 * 
	 * @param str
	 *            待处理字符串
	 * @param delimiter
	 *            分隔符
	 * @return 分割好的字符串数组
	 */
	public static String[] split(String str, char delimiter) {
		if (Strings.isNullOrEmpty(str))
			return new String[0];
		char[] buf = str.toCharArray();
		int count = 1;
		for (char c : buf) {
			if (c == delimiter)
				count++;
		}
		if (count == 1)
			return new String[] { str };
		String[] result = new String[count];
		count = 0;
		int index = 0;
		int point = 0;
		for (char c : buf) {
			if (c == delimiter) {
				if (point == index) {
					result[count++] = NULL_STR;
				} else {
					result[count++] = new String(buf, index, point - index);
				}
				point++;
				index = point;
			} else
				point++;
		}
		if (point > index) {
			result[count] = new String(buf, index, point - index);
		} else
			result[count] = NULL_STR;
		return result;
	}

	public static String[] splitTrim(String str, char delimiter) {
		String[] result = split(str, delimiter);
		for (int i = 0; i < result.length; i++)
			result[i] = result[i].trim();
		return result;
	}

	public static long toLong(String value) {
		return Long.parseLong(value, 36);
	}

	public static String toString(long value) {
		return Long.toString(value, 36).toUpperCase();
	}

	/**
	 * 设定全局的toJson函数
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.UseSingleQuotes, SerializerFeature.SkipTransientField,
				SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField,
				SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
	}

	/**
	 * 返回一个Base64编码的新生成UUID的字符串,长度为22个字符
	 * 
	 * @return
	 */
	public static String randomUUID64() {
		return BaseEncoding.base64().omitPadding().encode(UUID2Byte(UUID.randomUUID()));
	}
	
	/**
	 * 把一个UUID转为字节数组
	 * 
	 * @param uuid
	 * @return
	 */
	public static byte[] UUID2Byte(UUID uuid) {
		byte[] data = new byte[16];
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		for (int i = 0; i < 8; i++) {
			data[7 - i] = (byte) msb;
			msb = msb >> 8;
			data[15 - i] = (byte) lsb;
			lsb = lsb >> 8;
		}
		return data;
	}

	/**
	 * 把一个异常转为字符串
	 * 
	 * @param e
	 * @return
	 */
	public static String toString(Throwable e) {
		return toString(e, 0);
	}

	/**
	 * 把一个异常转为字符串,不能超过指定长度
	 * 
	 * @param e
	 * @param length
	 * @return
	 */
	public static String toString(Throwable e, int length) {
		String result;
		if (e instanceof AppException)
			result = e.getMessage();
		else
			result = Throwables.getStackTraceAsString(e);
		if (length > 0 && result.length() > length)
			return result.substring(0, length);
		return result;
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the substring before the first occurrence of a separator. The
	 * separator is not returned.
	 * </p>
	 * 
	 * <p>
	 * A {@code null} string input will return {@code null}. An empty ("")
	 * string input will return the empty string. A {@code null} separator will
	 * return the input string.
	 * </p>
	 * 
	 * <p>
	 * If nothing is found, the string input is returned.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.substringBefore(null, *)      = null
	 * StringUtils.substringBefore("", *)        = ""
	 * StringUtils.substringBefore("abc", "a")   = ""
	 * StringUtils.substringBefore("abcba", "b") = "a"
	 * StringUtils.substringBefore("abc", "c")   = "ab"
	 * StringUtils.substringBefore("abc", "d")   = "abc"
	 * StringUtils.substringBefore("abc", "")    = ""
	 * StringUtils.substringBefore("abc", null)  = "abc"
	 * </pre>
	 * 
	 * @param str
	 *            the String to get a substring from, may be null
	 * @param separator
	 *            the String to search for, may be null
	 * @return the substring before the first occurrence of the separator,
	 *         {@code null} if null String input
	 * @since 2.0
	 */
	public static String substringBefore(final String str, final String separator) {
		if (Strings.isNullOrEmpty(str) || separator == null) {
			return str;
		}
		if (separator.isEmpty()) {
			return NULL_STR;
		}
		final int pos = str.indexOf(separator);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * <p>
	 * Gets the substring after the first occurrence of a separator. The
	 * separator is not returned.
	 * </p>
	 * 
	 * <p>
	 * A {@code null} string input will return {@code null}. An empty ("")
	 * string input will return the empty string. A {@code null} separator will
	 * return the empty string if the input string is not {@code null}.
	 * </p>
	 * 
	 * <p>
	 * If nothing is found, the empty string is returned.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.substringAfter(null, *)      = null
	 * StringUtils.substringAfter("", *)        = ""
	 * StringUtils.substringAfter(*, null)      = ""
	 * StringUtils.substringAfter("abc", "a")   = "bc"
	 * StringUtils.substringAfter("abcba", "b") = "cba"
	 * StringUtils.substringAfter("abc", "c")   = ""
	 * StringUtils.substringAfter("abc", "d")   = ""
	 * StringUtils.substringAfter("abc", "")    = "abc"
	 * </pre>
	 * 
	 * @param str
	 *            the String to get a substring from, may be null
	 * @param separator
	 *            the String to search for, may be null
	 * @return the substring after the first occurrence of the separator,
	 *         {@code null} if null String input
	 * @since 2.0
	 */
	public static String substringAfter(final String str, final String separator) {
		if (Strings.isNullOrEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return NULL_STR;
		}
		final int pos = str.indexOf(separator);
		if (pos == -1) {
			return NULL_STR;
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * <p>
	 * Gets the String that is nested in between two instances of the same
	 * String.
	 * </p>
	 * 
	 * <p>
	 * A {@code null} input String returns {@code null}. A {@code null} tag
	 * returns {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.substringBetween(null, *)            = null
	 * StringUtils.substringBetween("", "")             = ""
	 * StringUtils.substringBetween("", "tag")          = null
	 * StringUtils.substringBetween("tagabctag", null)  = null
	 * StringUtils.substringBetween("tagabctag", "")    = ""
	 * StringUtils.substringBetween("tagabctag", "tag") = "abc"
	 * </pre>
	 * 
	 * @param str
	 *            the String containing the substring, may be null
	 * @param tag
	 *            the String before and after the substring, may be null
	 * @return the substring, {@code null} if no match
	 * @since 2.0
	 */
	public static String substringBetween(final String str, final String tag) {
		return substringBetween(str, tag, tag);
	}

	/**
	 * <p>
	 * Gets the String that is nested in between two Strings. Only the first
	 * match is returned.
	 * </p>
	 * 
	 * <p>
	 * A {@code null} input String returns {@code null}. A {@code null}
	 * open/close returns {@code null} (no match). An empty ("") open and close
	 * returns an empty string.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
	 * StringUtils.substringBetween(null, *, *)          = null
	 * StringUtils.substringBetween(*, null, *)          = null
	 * StringUtils.substringBetween(*, *, null)          = null
	 * StringUtils.substringBetween("", "", "")          = ""
	 * StringUtils.substringBetween("", "", "]")         = null
	 * StringUtils.substringBetween("", "[", "]")        = null
	 * StringUtils.substringBetween("yabcz", "", "")     = ""
	 * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
	 * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
	 * </pre>
	 * 
	 * @param str
	 *            the String containing the substring, may be null
	 * @param open
	 *            the String before the substring, may be null
	 * @param close
	 *            the String after the substring, may be null
	 * @return the substring, {@code null} if no match
	 * @since 2.0
	 */
	public static String substringBetween(final String str, final String open, final String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		final int start = str.indexOf(open);
		if (start != -1) {
			final int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	/**
	 * 根据函数转换一组列表，替代Guava中的Lists.transform
	 * 
	 * @param fromList
	 * @param function
	 * @return
	 */
	public static <F, T> List<T> transform(List<F> fromList, Function<? super F, ? extends T> function) {
		List<T> result = (fromList instanceof RandomAccess) ? new ArrayList<T>(fromList.size()) : new LinkedList<T>();
		for (F f : fromList) {
			T t = function.apply(f);
			if (t != null)
				result.add(t);
		}
		return result;
	}

	/**
	 * 根据函数转换一数组列表
	 * 
	 * @param fromList
	 * @param function
	 * @return
	 */
	public static <F, T> List<T> transform(F[] fromList, Function<? super F, ? extends T> function) {
		List<T> result = new ArrayList<T>(fromList.length);
		for (F f : fromList) {
			T t = function.apply(f);
			if (t != null)
				result.add(t);
		}
		return result;
	}

	/**
	 * 处理一些比较复杂的字符串连接,分隔符为逗号
	 * 
	 * @param sb
	 * @param list
	 * @param consumer
	 */
	public static <T> void join(Appendable sb, Collection<T> list, Consumer<T> consumer) {
		join(",", sb, list, consumer);
	}

	/**
	 * 处理一些比较复杂的字符串连接
	 * 
	 * @param sep
	 *            分隔符
	 * @param sb
	 * @param list
	 * @param consumer
	 */
	public static <T> void join(CharSequence sep, Appendable sb, Collection<T> list, Consumer<T> consumer) {
		boolean first = true;
		for (T t : list) {
			if (consumer.accept(t)) {
				if (first)
					first = false;
				else
					try {
						sb.append(sep);
					} catch (IOException neverHappen) {
					}
				consumer.consume(t);
			}
		}
	}

}

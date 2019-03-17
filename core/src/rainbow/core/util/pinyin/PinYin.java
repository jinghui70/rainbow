package rainbow.core.util.pinyin;

import static rainbow.core.util.Preconditions.*;

import java.io.IOException;
import java.util.Properties;

import rainbow.core.util.Utils;

public final class PinYin {

	/**
	 * A hash table contains <Unicode, HanyuPinyin> pairs
	 */
	private static Properties pinyinTable = null;

	static {
		pinyinTable = new Properties();
		try {
			pinyinTable.load(PinYin.class.getResourceAsStream("pinyin.properties"));
		} catch (IOException e) {
		}
	}

	private PinYin() {
	}

	/**
	 * 返回一个汉字对应的拼音首字母
	 * 
	 * @param ch
	 * @return
	 */
	public static char getFirstPinYin(char ch) {
		if (ch > 128) {
			String pinyin = getPinYinFromChar(ch);
			checkNotNull(pinyin, "unknow hanzi {}", ch);
			return Character.toUpperCase(pinyin.charAt(0));
		} else
			return ch;
	}

	/**
	 * 返回一个汉字对应的所有拼音
	 * 
	 * @param ch
	 * @return
	 */
	public static String[] getPinYin(char ch) {
		int codePointOfChar = ch;
		String codepointHexStr = Integer.toHexString(codePointOfChar).toUpperCase();
		String foundRecord = pinyinTable.getProperty(codepointHexStr);
		if (foundRecord == null || foundRecord.equals("null"))
			return null;
		return Utils.split(foundRecord, ',');
	}

	/**
	 * 返回一个字符串的拼音首字母串,大写的
	 * 
	 * @param str
	 * @return
	 */
	public static String getFirstPinYin(String str) {
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			try {
				sb.append(getFirstPinYin(ch));
			} catch (NullPointerException e) {
			}
		}
		return sb.toString();
	}

	/**
	 * 返回一个字符串的全拼，小写的
	 * 
	 * @param str
	 * @return
	 */
	public static String getFullPinYin(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch > 128) {
				String pinyin = getPinYinFromChar(ch);
				if (pinyin != null)
					sb.append(pinyin);
			} else
				sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 返回一个汉字对应的拼音
	 * 
	 * @param ch
	 *            Unicode中文字符
	 * @return corresponding Hanyu Pinyin Record in Properties file; null if no
	 *         record found
	 */
	private static String getPinYinFromChar(char ch) {
		int codePointOfChar = ch;
		String codepointHexStr = Integer.toHexString(codePointOfChar).toUpperCase();
		String foundRecord = pinyinTable.getProperty(codepointHexStr);
		if (foundRecord == null || foundRecord.equals("null"))
			return null;
		int index = foundRecord.indexOf(',');
		if (index == -1)
			return foundRecord;
		return foundRecord.substring(0, index);
	}
}

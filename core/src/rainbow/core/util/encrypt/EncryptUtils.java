package rainbow.core.util.encrypt;

import static rainbow.core.util.Preconditions.checkNotNull;

import rainbow.core.extension.ExtensionRegistry;

public class EncryptUtils {

	public static Cipher getCipher(String type) {
		Cipher cipher = ExtensionRegistry.getExtensionObject(Cipher.class, type).orElse(null);
		return checkNotNull(cipher, "cipher [{}] not found", type);
	}

	/**
	 * 加密
	 * 
	 * @param type   加密器名称
	 * @param source 源内容
	 * @return 加密后内容
	 */
	public static String encrypt(String type, String source) {
		return getCipher(type).encode(source);
	}

	/**
	 * 解密
	 * 
	 * @param type   加密器名称
	 * @param source 源内容
	 * @return 解密后内容
	 */
	public static String decrypt(String type, String source) {
		return getCipher(type).decode(source);
	}

	/**
	 * 用缺省方法加密
	 * 
	 * @param source 源内容
	 * @return 解密后内容
	 */
	public static String encrypt(String source) {
		return encrypt("default", source);
	}

	/**
	 * 缺省方法解密
	 * 
	 * @param source 源内容
	 * @return 解密后内容
	 */
	public static String decrypt(String source) {
		return decrypt("default", source);
	}
}

package rainbow.core.util.encrypt;

import static com.google.common.base.Preconditions.*;
import rainbow.core.extension.ExtensionRegistry;

public class EncryptUtils {

	/**
	 * 加密，
	 * 
	 * @param type
	 * @param source
	 * @return
	 */
	public static String encrypt(String type, String source) {
		Encryption cipher = ExtensionRegistry.getExtensionObject(Encryption.class, type);
		checkNotNull(cipher, "no encrypt cipher defined for [%s]", type);
		return cipher.encode(source);
	}

	public static String decrypt(String type, String source) {
		if (ExtensionRegistry.hasExtensionPoint(Encryption.class)) {
			Encryption cipher = ExtensionRegistry.getExtensionObject(Encryption.class, type);
			if (cipher == null)
				return source;
			return cipher.decode(source);
		}
		return source;
	}
}

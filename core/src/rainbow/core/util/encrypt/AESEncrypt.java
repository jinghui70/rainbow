package rainbow.core.util.encrypt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;

/*
 * AES对称加密和解密
 */
public class AESEncrypt {

	public static String encode(String seeds, String content) {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(seeds.getBytes());
            keygen.init(128, secureRandom);
			SecretKey key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] byte_AES = cipher.doFinal(content.getBytes("UTF-8"));
			return BaseEncoding.base64().encode(byte_AES);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String seeds, String content) {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(seeds.getBytes());
            keygen.init(128, secureRandom);
			SecretKey key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] byte_decode = cipher.doFinal(BaseEncoding.base64().decode(content));
			return new String(byte_decode, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
package rainbow.core.util.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DefaultCipher implements Cipher {

	private static byte[] KEY = "rainbow platform".getBytes(StandardCharsets.UTF_8);
	private static byte[] IV = "ABCDEF1234123412".getBytes(StandardCharsets.UTF_8);

	@Override
	public String encode(String source) {
		if (source == null)
			return null;
		try {
			Key secretKeySpec = new SecretKeySpec(KEY, "AES");
			AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(IV);
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] byte_AES = cipher.doFinal(source.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(byte_AES);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String decode(String source) {
		if (source == null)
			return null;
		try {
			Key secretKeySpec = new SecretKeySpec(KEY, "AES");
			AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(IV);
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] byte_decode = cipher.doFinal(Base64.getDecoder().decode(source));
			return new String(byte_decode, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return "default";
	}

}

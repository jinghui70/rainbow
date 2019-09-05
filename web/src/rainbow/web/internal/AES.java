package rainbow.web.internal;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import rainbow.core.platform.Platform;

public class AES {

	private static byte[] KEY = "rainbow platform".getBytes(StandardCharsets.UTF_8);
	private static byte[] IV = "ABCDEF1234123412".getBytes(StandardCharsets.UTF_8);

	public static String encode(HttpServletRequest request, String content) {
		if (AES.skipEncrypt(request)) return content;
		try {
			Key secretKeySpec = new SecretKeySpec(KEY, "AES");
			AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(IV);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] byte_AES = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(byte_AES);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(HttpServletRequest request, String content) {
		if (AES.skipEncrypt(request)) return content;
		try {
			Key secretKeySpec = new SecretKeySpec(KEY, "AES");
			AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(IV);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] byte_decode = cipher.doFinal(Base64.getDecoder().decode(content));
			return new String(byte_decode, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean skipEncrypt(HttpServletRequest request) {
		return (Platform.isDev() && "postman".equals(request.getHeader("REQUESTFROM")));
	}

}

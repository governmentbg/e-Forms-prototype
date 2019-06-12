package bg.bulsi.modules.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DecryptUtil {

	public static String decryptEgn(String egn, String password, String salt) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 1000, 256);
		SecretKey tmp = factory.generateSecret(spec);

		SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");

		byte[] data = Base64.getDecoder().decode(egn);
		// skip first 4 bytes (the length of IV) and get IV byte array
		byte[] iv = Arrays.copyOfRange(data, 4, 20);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		// skip IV length (4 bytes) and IV (16 bytes)
		cipher.update(data, 20, data.length - 20);
		String plaintext = new String(cipher.doFinal(), "UTF-8");

		return plaintext;

	}

}

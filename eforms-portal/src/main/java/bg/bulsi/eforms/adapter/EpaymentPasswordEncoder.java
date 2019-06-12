package bg.bulsi.eforms.adapter;

import org.springframework.security.crypto.password.PasswordEncoder;

import bg.bulsi.eforms.util.EpaymentUtil;
import bg.bulsi.modules.util.CryptoUtils;

public class EpaymentPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return CryptoUtils.getHash(rawPassword.toString(), CryptoUtils.getSalt());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String[] passDetails = encodedPassword.split("\\" + EpaymentUtil.PASS_SEPARATOR);
		if (passDetails.length == 2) {
			return CryptoUtils.verify(passDetails[0], rawPassword.toString(), passDetails[1]);
		}
		return false;
	}

}

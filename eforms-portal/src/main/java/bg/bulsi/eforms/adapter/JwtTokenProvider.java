package bg.bulsi.eforms.adapter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import bg.bulsi.eforms.exception.CustomException;
import bg.bulsi.eforms.model.auth.EauthUserDetails;
import bg.bulsi.eforms.model.auth.USER_TYPE;
import bg.bulsi.modules.util.DecryptUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenProvider {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Value("${edelivery.jwt.token.secret-key}")
	private String secretKey;

	@Value("${edelivery.egn.decrypt.secret-key}")
	private String egnDecryptSecretKey;

	@Value("${edelivery.egn.decrypt.salt}")
	private String egnDecryptSalt;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		String email = (String) claims.get("email");
		String nameBase64 = (String) claims.get("name");
		String name = new String(Base64.getDecoder().decode(nameBase64));

		String identity = claims.getSubject();
		String egnEncrypted = identity.substring(identity.lastIndexOf(":") + 1);
		String userIdentity = "";

		try {
			userIdentity = DecryptUtil.decryptEgn(egnEncrypted, egnDecryptSecretKey, egnDecryptSalt);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException
				| BadPaddingException | InvalidKeySpecException e) {
			e.printStackTrace();
		}

		if (StringUtils.isNotBlank(userIdentity)) {
			USER_TYPE userType = extractUserType(identity);

			UserDetails userDetails = new EauthUserDetails(name, email, userIdentity, userType);
			return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
		}
		return null;
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		String token = req.getParameter("token");
		return token;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.error("JWT validation error", e);
			throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private USER_TYPE extractUserType(String data) {
		String type = data.substring(data.indexOf(":") + 1, data.lastIndexOf(":"));
		if (type.equals("EIK")) {
			return USER_TYPE.LEGAL;
		}
		return USER_TYPE.INDIVIDUAL;
	}

}

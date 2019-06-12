package bg.bulsi.eforms.model.auth;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class EauthUserDetails extends User {

	private static final long serialVersionUID = 1L;

	private String email;
	private String identity;
	private USER_TYPE userType;

	public EauthUserDetails(String username, String email, String identity, USER_TYPE userType) {
		super(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList(ROLES.ROLE_EAUTH.name()));
		this.email = email;
		this.identity = identity;
		this.userType = userType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public USER_TYPE getUserType() {
		return userType;
	}

	public void setUserType(USER_TYPE userType) {
		this.userType = userType;
	}

	public String getFirstName() {
		if (userType == USER_TYPE.INDIVIDUAL) {
			String[] names = getUsername().split(" ");
			if (names.length > 1) {
				return names[0];
			}
		}
		return getUsername();
	}

	public String getLastName() {
		if (userType == USER_TYPE.INDIVIDUAL) {
			String[] names = getUsername().split(" ");
			if (names.length > 1) {
				return names[names.length - 1];
			}
		}
		return getUsername();
	}
}

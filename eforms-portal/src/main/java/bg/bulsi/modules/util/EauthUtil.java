package bg.bulsi.modules.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import bg.bulsi.eforms.model.auth.ROLES;

public class EauthUtil {

	public static boolean hasToSetAuthentication(SecurityContext context) {

		if (context == null || containsAuthority(context.getAuthentication(), ROLES.ROLE_ADMIN.name())) {
			return true;
		}

		return false;
	}

	private static boolean containsAuthority(Authentication authentication, String authority) {

		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			for (GrantedAuthority aut : authorities) {
				if (aut.getAuthority().equals(authority)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
}

package bg.bulsi.eforms.adapter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import bg.bulsi.eforms.model.auth.EauthUserDetails;
import bg.bulsi.modules.util.EauthUtil;

public class EauthTokenFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		EauthUserDetails user = (EauthUserDetails) request.getSession().getAttribute("EAUTH_USER");

		if (user != null && EauthUtil.hasToSetAuthentication(SecurityContextHolder.getContext())) {

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
					user.getAuthorities());
			auth.setDetails(user);
			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(auth);

		}

		filterChain.doFilter(request, response);
	}

}

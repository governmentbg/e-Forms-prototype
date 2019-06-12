package bg.bulsi.eforms.adapter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class EpdaeuServiceFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String epdaeuQueryString = request.getQueryString();
		if (StringUtils.isNotBlank(epdaeuQueryString)) {
			request.getSession().setAttribute("epdaeuQueryString", epdaeuQueryString);
		}

		// eliminate warning which clutters the log
		HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response);
		wrapper.setBufferSize(10000000);
	    
		filterChain.doFilter(request, wrapper);
	}

}

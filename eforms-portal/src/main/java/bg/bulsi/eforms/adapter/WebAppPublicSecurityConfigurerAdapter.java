package bg.bulsi.eforms.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import bg.bulsi.eforms.model.auth.ROLES;

@Configuration
@Order(1)
public class WebAppPublicSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.antMatcher("/app/**").authorizeRequests().anyRequest().hasRole(ROLES.EAUTH.name());

		http.formLogin() //
				.loginPage("/index.xhtml") //
				//.loginProcessingUrl("/eauth/eauthRedirector.xhtml") //
				//.defaultSuccessUrl("/app/service.xhtml") //
				.failureUrl("/index.xhtml").permitAll(); //

		http.logout() //
				.logoutUrl("/app/logout") //
				.logoutSuccessUrl("/index.xhtml");

		http.addFilterAt(eauthTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterAfter(jwtTokenFilterBean(), EauthTokenFilter.class);
		http.addFilterAfter(new EpdaeuServiceFilter(), EauthTokenFilter.class);

		http.anonymous().disable();
		http.csrf().disable();
	}

	@Bean
	public JwtTokenFilter jwtTokenFilterBean() {
		return new JwtTokenFilter();
	}

	@Bean
	public EauthTokenFilter eauthTokenFilterBean() {
		return new EauthTokenFilter();
	}
}
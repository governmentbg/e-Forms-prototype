package bg.bulsi.eforms.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import bg.bulsi.eforms.model.auth.ROLES;
import bg.bulsi.eforms.service.epayment.EpaymentDetailsService;

@Configuration
@Order(2)
public class WebAppAdminSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@Autowired
	private EpaymentDetailsService epaymentDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.antMatcher("/admin/**").authorizeRequests().anyRequest().hasRole(ROLES.ADMIN.name());

		http.formLogin() //
				.loginPage("/admin/login.xhtml") //
				.loginProcessingUrl("/admin/login.xhtml")//
				.defaultSuccessUrl("/admin/payment_registration.xhtml") //
				.failureUrl("/admin/login.xhtml?source=loginError").permitAll();//

		http.logout() //
				.logoutUrl("/admin/logout") //
				.logoutSuccessUrl("/admin/login.xhtml"); //

		// http.anonymous().disable();
		http.csrf().disable();

		/*
		 * http.antMatcher("/protected/**").authorizeRequests().anyRequest().
		 * hasRole(ROLES.ADMIN.name());
		 * 
		 * http.formLogin() // .loginPage("/protected/login.xhtml") //
		 * .loginProcessingUrl("/protected/login.xhtml")//
		 * .defaultSuccessUrl("/protected/index.xhtml") //
		 * .failureUrl("/protected/login.xhtml").permitAll();//
		 * 
		 * http.logout() // .logoutUrl("/protected/logout") //
		 * .logoutSuccessUrl("/protected/login.xhtml"); //
		 * 
		 * // http.anonymous().disable(); http.csrf().disable();
		 */
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(epaymentDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder encoder() {
		return new EpaymentPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		/*
		 * auth.jdbcAuthentication().dataSource(dataSource)
		 * .usersByUsernameQuery(
		 * "select username, password, isactive as enabledfrom eserviceadminusers where username=?"
		 * ) .authoritiesByUsernameQuery(
		 * "select username, 'ADMIN' as authority from eserviceadminusers where username=?"
		 * )
		 */
		auth.userDetailsService(epaymentDetailsService).passwordEncoder(encoder());
	}

	/*
	 * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth)
	 * throws Exception { PasswordEncoder encoder =
	 * PasswordEncoderFactories.createDelegatingPasswordEncoder();
	 * auth.inMemoryAuthentication() // .withUser("admin") //
	 * .password(encoder.encode("123456")) // .roles("ADMIN"); }
	 */

}
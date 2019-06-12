package bg.bulsi.eforms.conf.context;

import java.security.Security;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.h2.server.web.WebServlet;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.soahouse.adminfaces.template.config.AdminfacesConfig;

import bg.bulsi.eforms.adapter.WebAppAdminSecurityConfigurerAdapter;
import bg.bulsi.eforms.adapter.WebAppPublicSecurityConfigurerAdapter;
import bg.bulsi.eforms.adapter.WebApplicationConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = { "bg.bulsi.eforms" })
@Import(value = { AdminfacesConfig.class })
@EnableScheduling

@PropertySources({ @PropertySource(value = "classpath:confEforms.properties"),
		@PropertySource(value = "file:${app.config.path}/confEforms.properties", ignoreResourceNotFound = true) })
@Order(1)
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		ServletRegistration.Dynamic servlet = servletContext.addServlet("h2-console", new WebServlet());
		servlet.setLoadOnStartup(2);
		servlet.addMapping("/console/*");
		
		//BouncyCastleProvider
		Security.addProvider(new BouncyCastleProvider());
	}
	
	

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { WebApplicationInitializer.class };
	}

	/**
	 * {@link WebApplicationConfigurerAdapter} and
	 * {@link WebAppPublicSecurityConfigurerAdapter} already being scanned.
	 * {@link WebAppAdminSecurityConfigurerAdapter} already being scanned.
	 */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] {};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/rest/*" };
	}

}
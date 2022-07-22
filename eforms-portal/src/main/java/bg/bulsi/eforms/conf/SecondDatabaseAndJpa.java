package bg.bulsi.eforms.conf;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "bg.bulsi.eforms.model.autofill",
	entityManagerFactoryRef = "secondEntityManagerFactory",
	transactionManagerRef = "secondTransactionManager")
@EnableTransactionManagement
public class SecondDatabaseAndJpa {

	private static final Log log = LogFactory.getLog(SecondDatabaseAndJpa.class);

	@Bean
	public DataSource autoFillDataSource() throws Exception {
		log.info("Loading Second DataSource.");
		Resource resource = new FileSystemResource(System.getProperty("app.config.path") + "/datasource_autofill.properties");
		if (!resource.exists()) {
			resource = new ClassPathResource("datasource_autofill.properties");
		}
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		return BasicDataSourceFactory.createDataSource(props);
	}

	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(

			@Qualifier("autoFillDataSource") DataSource dataSource, // Qualifier
			JpaVendorAdapter jpaVendorAdapter,
			@Value("#{packagesToScan}") String[] packagesToScan,
			@Value("#{sharedCacheMode}") SharedCacheMode sharedCacheMode,
			@Value("#{jpaPropertiesMap}") Map<String, ?> jpaPropertiesMap) {

		log.info("Loading Second JPA EntityManagerFactory.");
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setJpaVendorAdapter(jpaVendorAdapter);
		bean.setPackagesToScan("bg.bulsi.eforms.model.autofill"); // override: packagesToScan(def: "bg.bulsi.eforms")
		bean.setSharedCacheMode(sharedCacheMode);
		bean.setJpaPropertyMap(jpaPropertiesMap);
		bean.afterPropertiesSet();
		return bean;
	}


	/**
	 * {@link JpaTransactionManager} also supports direct DataSource access
	 * within a transaction (i.e. plain JDBC code working with the same
	 * DataSource). This allows for mixing services which access JPA and
	 * services which use plain JDBC (without being aware of JPA)! Application
	 * code needs to stick to the same simple Connection lookup pattern as with
	 * DataSourceTransactionManager (i.e.
	 * DataSourceUtils.getConnection(javax.sql.DataSource) or going through a
	 * TransactionAwareDataSourceProxy). Note that this requires a
	 * vendor-specific JpaDialect to be configured
	 *
	 * @param entityManagerFactory
	 * @return
	 */

	@Bean
	@Autowired
	public PlatformTransactionManager secondTransactionManager(
			@Qualifier("secondEntityManagerFactory") EntityManagerFactory secondEntityManagerFactory) { // Qualifier
		log.info("Loading Second JPA Transaction Management.");
		return new JpaTransactionManager(secondEntityManagerFactory);
	}

}

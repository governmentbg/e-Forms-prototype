package bg.bulsi.eforms.conf;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import bg.bulsi.eforms.conf.context.WebApplicationInitializer;

@Configuration
@EnableJpaRepositories(basePackages = "bg.bulsi.eforms.model.epayment",
	entityManagerFactoryRef = "entityManagerFactory",
	transactionManagerRef = "transactionManager")
public class Jpa {

	private static final Log log = LogFactory.getLog(Jpa.class);

	@Value("${epayments.db.type}")
	private String dbType;


	@Primary
	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter,
			@Value("#{packagesToScan}") String[] packagesToScan,
			@Value("#{sharedCacheMode}") SharedCacheMode sharedCacheMode,
			@Value("#{jpaPropertiesMap}") Map<String, ?> jpaPropertiesMap) {

		log.info("Loading Main JPA EntityManagerFactory.");
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setJpaVendorAdapter(jpaVendorAdapter);
		bean.setPackagesToScan("bg.bulsi.eforms.model.epayment"); // override: packagesToScan(def: "bg.bulsi.eforms")
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

	@Primary
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		log.info("Loading Main JPA Transaction Management.");
		return new JpaTransactionManager(entityManagerFactory);
	}


	/*
	 * DEFINITIONS
	 */

	@Bean
	public String[] packagesToScan() {
		return WebApplicationInitializer.class.getAnnotation(ComponentScan.class).basePackages();
	}


	@Bean
	public Database jpaVendorDatabase() {
		// return Database.POSTGRESQL;
		if (dbType.equals("H2")) {
			return Database.H2;
		}
		return Database.SQL_SERVER;
	}


	@Bean
	public Boolean generateDdl() {
		return Boolean.TRUE;
	}


	@Bean
	public Boolean showSql() {
		return Boolean.TRUE;
	}


	@Bean
	public SharedCacheMode sharedCacheMode() {
		return SharedCacheMode.ENABLE_SELECTIVE;
	}


	/*
	 * HIBERNATE
	 */

	public enum Hbm2Ddl {
		NONE("none"),
		VALIDATE("validate"),
		UPDATE("update"),
		CREATE("create"),
		CREATE_AND_DROP("create-drop");

		private String value;


		private Hbm2Ddl(String value) {
			this.value = value;
		}


		public String getValue() {
			return value;
		}
	}


	@Bean
	@Autowired
	public JpaVendorAdapter jpaVendorAdapter( //
			@Value("#{jpaVendorDatabase}") Database jpaVendorDatabase, //
			@Value("#{jpaVendorDialect}") String jpaVendorDialect, //
			@Value("#{generateDdl}") Boolean generateDdl, //
			@Value("#{showSql}") Boolean showSql) {

		log.info("Loading Hibernate as JPA vendor.");
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(jpaVendorDatabase);
		adapter.setDatabasePlatform(jpaVendorDialect);
		adapter.setGenerateDdl(generateDdl);
		adapter.setShowSql(showSql);
		return adapter;
	}


	@Bean
	public String jpaVendorDialect() {
		// return PostgreSQL94Dialect.class.getName();
		if (dbType.equals("H2")) {
			return H2Dialect.class.getName();
		}
		return SQLServer2012Dialect.class.getName();
	}


	@Bean
	public Map<String, ?> jpaPropertiesMap() {
		Map<String, Object> bean = new HashMap<String, Object>();
		if (dbType.equals("H2")) {
			bean.put("hibernate.hbm2ddl.auto", Hbm2Ddl.CREATE.getValue());
		} else {
			bean.put("hibernate.hbm2ddl.auto", Hbm2Ddl.NONE.getValue());
		}
		bean.put("hibernate.format_sql", true);
		// Prevents the throwing of LazyInitializationException.
		bean.put("hibernate.enable_lazy_load_no_trans", true);
		return bean;
	}

}

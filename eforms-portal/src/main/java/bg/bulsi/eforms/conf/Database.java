package bg.bulsi.eforms.conf;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class Database {

	private static final Logger log = LoggerFactory.getLogger(Database.class);


	@Primary
	@Bean
	public DataSource dataSource() throws Exception {
		log.info("Loading Main DataSource.");
		Resource resource = new FileSystemResource(System.getProperty("app.config.path") + "/datasource.properties");
		if (!resource.exists()) {
			resource = new ClassPathResource("datasource.properties");
		}
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		return BasicDataSourceFactory.createDataSource(props);
	}


	@Primary
	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
		log.info("Loading PersistenceExceptionTranslationPostProcessor.");
		return new PersistenceExceptionTranslationPostProcessor();
	}


	@Primary
	@Bean
	public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
		log.info("Loading PersistenceAnnotationBeanPostProcessor.");
		return new PersistenceAnnotationBeanPostProcessor();
	}

}

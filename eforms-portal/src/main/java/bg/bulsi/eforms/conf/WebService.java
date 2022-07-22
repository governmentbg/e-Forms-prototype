package bg.bulsi.eforms.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tempuri.IEDeliveryIntegrationService;

import bg.bulsi.edelivery.client.EdeliveryClient;
import bg.egov.edelivery.services.integration.exception.ServiceDataException;

@Configuration
public class WebService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());


	@Bean
	public IEDeliveryIntegrationService getWSClient() {
		IEDeliveryIntegrationService ws = null;
		try {
			ws = EdeliveryClient.initWs();
		} catch (ServiceDataException e) {
			log.error(e.getMessage());
		}
		return ws;
	}
}

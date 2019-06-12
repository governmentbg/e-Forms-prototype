package bg.bulsi.eforms.app;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InstitutionsServicesScheduler implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private InstitutionsServicesLoader institutionsServicesLoader;

	@Scheduled(initialDelay = 1000, fixedRateString = "${epdaeu.client.schedule.milliseconds}")
	public void loadInstitutionsAndServices() {
		institutionsServicesLoader.loadInstitutionsAndServices();
	}
}

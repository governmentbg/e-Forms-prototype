package bg.bulsi.eforms.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import bg.bulsi.eforms.model.epdaeu.InstitutionServices;
import bg.bulsi.epdaeu.model.ResponseModel;
import bg.bulsi.epdaeu.model.Status;
import bg.bulsi.epdaeu.resteasy.model.xjc.institution.Institutions;
import bg.bulsi.epdaeu.resteasy.model.xjc.institution.Institutions.Institution;
import bg.bulsi.epdaeu.resteasy.model.xjc.services.Services;
import bg.bulsi.epdaeu.resteasy.model.xjc.services.Services.Service;
import bg.bulsi.epdaeu.resteasy.resquest.EpdaeuClient;

@org.springframework.stereotype.Service("institutionsServicesLoader")
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class InstitutionsServicesLoader implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private EpdaeuClient wsClient;
	private List<InstitutionServices> institutionsServices;
	private List<InstitutionServices> institutionsServicesTmp;

	public void loadInstitutionsAndServices() {
		setWsClient(new EpdaeuClient());
		loadInstitutions(getWsClient());
		loadServices(getWsClient());
		removeInstitutionsWithoutService();
		copyTmpToRealList();
		orderServices();
		log.info("loading services list from epdaeu finished.");
	}

	private void loadInstitutions(EpdaeuClient rq) {
		institutionsServicesTmp = new ArrayList<>();
		ResponseModel<Institutions> rs = rq.getInstitutionsResult();
		if (rs.getStatusCode() == Status.STATUS_OK) {
			Institutions institutions = rs.getContent();
			for (Institution institution : institutions.getInstitution()) {
				InstitutionServices institutionServices = new InstitutionServices();
				institutionServices.setInstitution(institution);
				institutionsServicesTmp.add(institutionServices);
			}
		}
	}

	private void loadServices(EpdaeuClient rq) {

		for (InstitutionServices institution : institutionsServicesTmp) {
			institution.getServices().clear();
			Institution inst = institution.getInstitution();
			ResponseModel<Services> rs = rq.getServicesResult(inst.getInstitutionOID(), null);
			if (rs.getStatusCode() == Status.STATUS_OK) {
				Services services = rs.getContent();
				institution.getServices().addAll(services.getService());
			}
		}
	}

	private void removeInstitutionsWithoutService() {
		for (Iterator<InstitutionServices> iterator = institutionsServicesTmp.iterator(); iterator.hasNext();) {
			InstitutionServices institution = iterator.next();
			if (institution.getServices().isEmpty()) {
				iterator.remove();
			}
		}
	}

	public InstitutionServices findInstitutionFromList(String id) {
		for (InstitutionServices inst : institutionsServices) {
			if (inst.getInstitution().getId().equals(id)) {
				return inst;
			}
		}
		return null;
	}

	public Service findServiceFromList(String id) {
		for (InstitutionServices inst : institutionsServices) {
			for (Service serv : inst.getServices()) {
				if (serv.getArId().equals(id)) {
					return serv;
				}
			}
		}
		return null;
	}

	private void copyTmpToRealList() {
		institutionsServices = new ArrayList<>(institutionsServicesTmp);
		institutionsServicesTmp.clear();
	}

	private void orderServices() {
		for (InstitutionServices institutionServices : institutionsServices) {
			institutionServices.getServices().sort(new Comparator<Service>() {

				@Override
				public int compare(Service o1, Service o2) {
					Integer serv1 = extractServiceNumber(o1.getShortName());
					Integer serv2 = extractServiceNumber(o2.getShortName());
					return serv1.compareTo(serv2);
				}
			});
		}
	}

	private Integer extractServiceNumber(String serviceName) {
		try {
			Pattern pattern = Pattern.compile("^\\d+ ");
			Matcher matcher = pattern.matcher(serviceName);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group().trim());
			}
		} catch (Exception ignored) {
		}
		return 0;
	}

	public List<InstitutionServices> getInstitutionsServices() {
		if (institutionsServices == null) {
			institutionsServices = new ArrayList<>();
		}
		return institutionsServices;
	}

	public EpdaeuClient getWsClient() {
		return wsClient;
	}

	private void setWsClient(EpdaeuClient wsClient) {
		this.wsClient = wsClient;
	}
}

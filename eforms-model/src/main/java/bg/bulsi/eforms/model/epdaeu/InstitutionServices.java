package bg.bulsi.eforms.model.epdaeu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bg.bulsi.epdaeu.resteasy.model.xjc.institution.Institutions.Institution;
import bg.bulsi.epdaeu.resteasy.model.xjc.services.Services.Service;

public class InstitutionServices implements Serializable {

	private static final long serialVersionUID = 1L;

	private Institution institution;
	private List<Service> services;

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public List<Service> getServices() {
		if (services == null) {
			services = new ArrayList<Service>();
		}
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
}

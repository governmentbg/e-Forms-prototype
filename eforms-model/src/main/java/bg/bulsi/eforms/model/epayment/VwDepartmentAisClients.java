package bg.bulsi.eforms.model.epayment;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vwDepartmentAisClients")
public class VwDepartmentAisClients implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String clientid;

	private Integer departmentid;

	private String departmentname;

	private String aisname;

	private String secretkey;

	private String serviceproviderbank;

	private String serviceproviderbic;

	private String serviceprovideriban;

	private Integer eserviceclientid;


	public VwDepartmentAisClients() {
		// empty
	}


	public Integer getDepartmentid() {
		return departmentid;
	}


	public void setDepartmentid(Integer departmentid) {
		this.departmentid = departmentid;
	}


	public String getDepartmentname() {
		return departmentname;
	}


	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}


	public String getAisname() {
		return aisname;
	}


	public void setAisname(String aisname) {
		this.aisname = aisname;
	}


	public String getClientid() {
		return clientid;
	}


	public void setClientid(String clientid) {
		this.clientid = clientid;
	}


	public String getSecretkey() {
		return secretkey;
	}


	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}


	public String getServiceproviderbank() {
		return serviceproviderbank;
	}


	public void setServiceproviderbank(String serviceproviderbank) {
		this.serviceproviderbank = serviceproviderbank;
	}


	public String getServiceproviderbic() {
		return serviceproviderbic;
	}


	public void setServiceproviderbic(String serviceproviderbic) {
		this.serviceproviderbic = serviceproviderbic;
	}


	public String getServiceprovideriban() {
		return serviceprovideriban;
	}


	public void setServiceprovideriban(String serviceprovideriban) {
		this.serviceprovideriban = serviceprovideriban;
	}


	public Integer getEserviceclientid() {
		return eserviceclientid;
	}


	public void setEserviceclientid(Integer eserviceclientid) {
		this.eserviceclientid = eserviceclientid;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((aisname == null) ? 0 : aisname.hashCode());
		result = (prime * result) + ((clientid == null) ? 0 : clientid.hashCode());
		result = (prime * result) + ((departmentid == null) ? 0 : departmentid.hashCode());
		result = (prime * result) + ((departmentname == null) ? 0 : departmentname.hashCode());
		result = (prime * result) + ((eserviceclientid == null) ? 0 : eserviceclientid.hashCode());
		result = (prime * result) + ((secretkey == null) ? 0 : secretkey.hashCode());
		result = (prime * result) + ((serviceproviderbank == null) ? 0 : serviceproviderbank.hashCode());
		result = (prime * result) + ((serviceproviderbic == null) ? 0 : serviceproviderbic.hashCode());
		result = (prime * result) + ((serviceprovideriban == null) ? 0 : serviceprovideriban.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VwDepartmentAisClients other = (VwDepartmentAisClients) obj;
		if (aisname == null) {
			if (other.aisname != null) {
				return false;
			}
		} else if (!aisname.equals(other.aisname)) {
			return false;
		}
		if (clientid == null) {
			if (other.clientid != null) {
				return false;
			}
		} else if (!clientid.equals(other.clientid)) {
			return false;
		}
		if (departmentid == null) {
			if (other.departmentid != null) {
				return false;
			}
		} else if (!departmentid.equals(other.departmentid)) {
			return false;
		}
		if (departmentname == null) {
			if (other.departmentname != null) {
				return false;
			}
		} else if (!departmentname.equals(other.departmentname)) {
			return false;
		}
		if (eserviceclientid == null) {
			if (other.eserviceclientid != null) {
				return false;
			}
		} else if (!eserviceclientid.equals(other.eserviceclientid)) {
			return false;
		}
		if (secretkey == null) {
			if (other.secretkey != null) {
				return false;
			}
		} else if (!secretkey.equals(other.secretkey)) {
			return false;
		}
		if (serviceproviderbank == null) {
			if (other.serviceproviderbank != null) {
				return false;
			}
		} else if (!serviceproviderbank.equals(other.serviceproviderbank)) {
			return false;
		}
		if (serviceproviderbic == null) {
			if (other.serviceproviderbic != null) {
				return false;
			}
		} else if (!serviceproviderbic.equals(other.serviceproviderbic)) {
			return false;
		}
		if (serviceprovideriban == null) {
			if (other.serviceprovideriban != null) {
				return false;
			}
		} else if (!serviceprovideriban.equals(other.serviceprovideriban)) {
			return false;
		}
		return true;
	}

}

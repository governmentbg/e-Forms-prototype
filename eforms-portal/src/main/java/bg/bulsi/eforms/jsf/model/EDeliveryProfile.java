package bg.bulsi.eforms.jsf.model;

import java.io.Serializable;

import org.datacontract.schemas._2004._07.edelivery_common.EProfileType;

public class EDeliveryProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private EProfileType type;
	private String name;
	private String identifier;


	public EDeliveryProfile() {
		// empty
	}


	public EDeliveryProfile(EProfileType type, String name, String identifier) {
		super();
		this.type = type;
		this.name = name;
		this.identifier = identifier;
	}


	public EProfileType getType() {
		return type;
	}


	public void setType(EProfileType type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EDeliveryProfile other = (EDeliveryProfile) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "EDeliveryProfile [type=" + type + ", name=" + name + ", identifier=" + identifier + "]";
	}

}

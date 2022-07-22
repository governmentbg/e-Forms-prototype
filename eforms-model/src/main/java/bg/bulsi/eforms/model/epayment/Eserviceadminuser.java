package bg.bulsi.eforms.model.epayment;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "EserviceAdminUsers")
@NamedQuery(name = "Eserviceadminuser.findAll", query = "SELECT e FROM Eserviceadminuser e")
public class Eserviceadminuser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer eserviceadminuserid;

	private String departmentid;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "departmentid", referencedColumnName = "departmentid")
	private List<VwDepartmentAisClients> departments;

	private Integer referringeserviceclientid;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "referringeserviceclientid", referencedColumnName = "eserviceclientid", insertable = false, updatable = false)
	private VwDepartmentAisClients department;

	private String email;

	private Boolean insufficientamountnotifications;

	private String ipaddressesforaccess;

	private Boolean isactive;

	private String name;

	private Boolean notreferencednotifications;

	private Boolean overpaidamountnotifications;

	private String passwordhash;

	private String passwordsalt;

	private Boolean referencednotifications;

	private String username;


	public Eserviceadminuser() {
		// empty
	}


	public Integer getEserviceadminuserid() {
		return this.eserviceadminuserid;
	}


	public void setEserviceadminuserid(Integer eserviceadminuserid) {
		this.eserviceadminuserid = eserviceadminuserid;
	}


	public String getDepartmentid() {
		return departmentid;
	}


	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}


	public List<VwDepartmentAisClients> getDepartments() {
		return departments;
	}


	public void setDepartments(List<VwDepartmentAisClients> departments) {
		this.departments = departments;
	}


	public String getEmail() {
		return this.email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Boolean getInsufficientamountnotifications() {
		return this.insufficientamountnotifications;
	}


	public void setInsufficientamountnotifications(Boolean insufficientamountnotifications) {
		this.insufficientamountnotifications = insufficientamountnotifications;
	}


	public String getIpaddressesforaccess() {
		return this.ipaddressesforaccess;
	}


	public void setIpaddressesforaccess(String ipaddressesforaccess) {
		this.ipaddressesforaccess = ipaddressesforaccess;
	}


	public Boolean getIsactive() {
		return this.isactive;
	}


	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}


	public String getName() {
		return this.name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Boolean getNotreferencednotifications() {
		return this.notreferencednotifications;
	}


	public void setNotreferencednotifications(Boolean notreferencednotifications) {
		this.notreferencednotifications = notreferencednotifications;
	}


	public Boolean getOverpaidamountnotifications() {
		return this.overpaidamountnotifications;
	}


	public void setOverpaidamountnotifications(Boolean overpaidamountnotifications) {
		this.overpaidamountnotifications = overpaidamountnotifications;
	}


	public String getPasswordhash() {
		return this.passwordhash;
	}


	public void setPasswordhash(String passwordhash) {
		this.passwordhash = passwordhash;
	}


	public String getPasswordsalt() {
		return this.passwordsalt;
	}


	public void setPasswordsalt(String passwordsalt) {
		this.passwordsalt = passwordsalt;
	}


	public Boolean getReferencednotifications() {
		return this.referencednotifications;
	}


	public void setReferencednotifications(Boolean referencednotifications) {
		this.referencednotifications = referencednotifications;
	}


	public String getUsername() {
		return this.username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public VwDepartmentAisClients getDepartment() {
		return department;
	}


	public void setDepartment(VwDepartmentAisClients department) {
		this.department = department;
	}


	public Integer getReferringeserviceclientid() {
		return referringeserviceclientid;
	}


	public void setReferringeserviceclientid(Integer referringeserviceclientid) {
		this.referringeserviceclientid = referringeserviceclientid;
	}

}

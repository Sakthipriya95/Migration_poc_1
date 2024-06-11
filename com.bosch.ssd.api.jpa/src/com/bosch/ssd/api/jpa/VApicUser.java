package com.bosch.ssd.api.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the V_APIC_USERS database table.
 * 
 */
@Entity
@Table(name="V_APIC_USERS")
@NamedQuery(name="VApicUser.findAll", query="SELECT v FROM VApicUser v")
public class VApicUser implements Serializable {
	private static final long serialVersionUID = 1L;

	private String costcenter;

	@Column(name="COUNTRY_CODE")
	private String countryCode;

	@Column(name="COUNTRY_DIAL")
	private String countryDial;

	@Column(name="COUNTRY_NAME")
	private String countryName;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	@Column(name="CREATED_USER")
	private String createdUser;

	private String department;

	@Column(name="DISCLMR_ACCEPTED_DATE")
	private Timestamp disclmrAcceptedDate;

	@Column(name="\"DOMAIN\"")
	private String domain;

	private String email;

	private String fax;

	private String firstname;

	@Column(name="IS_DELETED")
	private String isDeleted;

	private String lastname;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	@Column(name="MODIFIED_USER")
	private String modifiedUser;

	private String phone;

	@Column(name="USER_ID")
	private BigDecimal userId;

	private String username;

	@Column(name="\"VERSION\"")
	private BigDecimal version;

	public VApicUser() {
	}

	public String getCostcenter() {
		return this.costcenter;
	}

	public void setCostcenter(String costcenter) {
		this.costcenter = costcenter;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryDial() {
		return this.countryDial;
	}

	public void setCountryDial(String countryDial) {
		this.countryDial = countryDial;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Timestamp getDisclmrAcceptedDate() {
		return this.disclmrAcceptedDate;
	}

	public void setDisclmrAcceptedDate(Timestamp disclmrAcceptedDate) {
		this.disclmrAcceptedDate = disclmrAcceptedDate;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Timestamp getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedUser() {
		return this.modifiedUser;
	}

	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public BigDecimal getUserId() {
		return this.userId;
	}

	public void setUserId(BigDecimal userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public BigDecimal getVersion() {
		return this.version;
	}

	public void setVersion(BigDecimal version) {
		this.version = version;
	}

}
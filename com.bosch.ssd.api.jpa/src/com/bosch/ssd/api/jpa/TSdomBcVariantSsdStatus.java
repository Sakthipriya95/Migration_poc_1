package com.bosch.ssd.api.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the T_SDOM_BC_VARIANT_SSD_STATUS database table.
 */
@Entity
@Table(name="T_SDOM_BC_VARIANT_SSD_STATUS")

@NamedQueries({
@NamedQuery(name="TSdomBcVariantSsdStatus.findAll", query="SELECT t FROM TSdomBcVariantSsdStatus t"),
@NamedQuery(name="TSdomBcVariantSsdStatus.findBCSSDInfo", query="SELECT t FROM TSdomBcVariantSsdStatus t  where t.elNummer= :elNummer and t.variant= :variant")})

@XmlRootElement(name = "TSdomBcVariantSsdStatus") 
public class TSdomBcVariantSsdStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", length=30)
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(length=1)
	private String deleted;

	@Column(name="DELETED_BY", length=30)
	private String deletedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="DELETED_DATE")
	private Date deletedDate;

	@Column(name="EL_NUMMER", nullable=false)
	private BigDecimal elNummer;

	@Column(length=1)
	private String ssd;

	@Column(length=255)
	private String variant;

	public TSdomBcVariantSsdStatus() {
	}

	public long getId() {
		return this.id;
	}
	 @XmlElement 
	public void setId(long id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}
	 @XmlElement 
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return this.createdDate;
	}
	 @XmlElement 
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDeleted() {
		return this.deleted;
	}
	 @XmlElement 
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public String getDeletedBy() {
		return this.deletedBy;
	}
	 @XmlElement 
	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Date getDeletedDate() {
		return this.deletedDate;
	}
	 @XmlElement 
	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public BigDecimal getElNummer() {
		return this.elNummer;
	}
	 @XmlElement 
	public void setElNummer(BigDecimal elNummer) {
		this.elNummer = elNummer;
	}

	public String getSsd() {
		return this.ssd;
	}
	 @XmlElement 
	public void setSsd(String ssd) {
		this.ssd = ssd;
	}

	public String getVariant() {
		return this.variant;
	}
	 @XmlElement 
	public void setVariant(String variant) {
		this.variant = variant;
	}

}
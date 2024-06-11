package com.bosch.ssd.api.jpa;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the V_SDOM_BC_SSDINFO database table.
 * 
 */
@Entity
@Table(name="V_SDOM_BC_SSDINFO")
@NamedQueries({
@NamedQuery(name="VSdomBcSsdinfo.findAll", query="SELECT v FROM VSdomBcSsdinfo v"),
@NamedQuery(name="VSdomBcSsdinfo.findBCinfo", query="SELECT v FROM VSdomBcSsdinfo v where v.bcNumber = :elnummer and v.bcVariant = :variante"),
@NamedQuery(name="VSdomBcSsdinfo.findBCinfoByBCName", query="SELECT v FROM VSdomBcSsdinfo v where v.bcName = :bcName and v.bcVariant = :variante")
})
public class VSdomBcSsdinfo implements Serializable {

  
	/**
   * 
   */
  private static final long serialVersionUID = -5050753366983671721L;

  @Column(name="EL_NAME", nullable=false, length=50)
	private String bcName;

	@Column(name="EL_NUMMER", nullable=false)
	private BigDecimal bcNumber;

	@Id
	private byte[] idc;

	@Column(name="NODE_NAME", insertable=false, updatable=false, length=4000)
	private String nodeName;

	@Column(name="REVISION", nullable=false)
	private BigDecimal bcRevision;

	@Column(name="SSD_STATUS", nullable=false, length=30)
	private String ssdStatus;

	@Column(name="VARIANTE", nullable=false, length=32)
	private String bcVariant;

	public VSdomBcSsdinfo() {
	}

	public String getBcName() {
		return this.bcName;
	}

	public void setBcName(String bcName) {
		this.bcName = bcName;
	}

	public BigDecimal getBcNumber() {
		return this.bcNumber;
	}

	public void setBcNumber(BigDecimal bcNumber) {
		this.bcNumber = bcNumber;
	}

	public byte[] getIdc() {
		return this.idc;
	}

	public void setIdc(byte[] idc) {
		this.idc = idc;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public BigDecimal getBcRevision() {
		return this.bcRevision;
	}

	public void setBcRevision(BigDecimal bcRevision) {
		this.bcRevision = bcRevision;
	}

	public String getSsdStatus() {
		return this.ssdStatus;
	}

	public void setSsdStatus(String ssdStatus) {
		this.ssdStatus = ssdStatus;
	}

	public String getBcVariant() {
		return this.bcVariant;
	}

	public void setBcVariant(String bcVariant) {
		this.bcVariant = bcVariant;
	}

}
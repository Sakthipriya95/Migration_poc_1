package com.bosch.ssd.api.jpa.review;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the T_LDB2_SSD_CRW_RULES database table.
 * 
 */
@Entity
@Table(name="T_LDB2_SSD_CRW_RULES")
@NamedQuery(name="TLdb2SsdCrwRule.findAll", query="SELECT t FROM TLdb2SsdCrwRule t")
public class TLdb2SsdCrwRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="T_LDB2_SSD_CRW_RULES_ID_GENERATOR", sequenceName="SEQ_SSD_CRW")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_LDB2_SSD_CRW_RULES_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", length=200)
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	@Column(name="IS_LATEST_RVW", length=1)
	private String isLatestRvw;

	@Column(name="LAB_LAB_ID")
	private BigDecimal labLabId;

	@Column(name="LAB_OBJ_ID")
	private BigDecimal labObjId;

	@Column(name="MODIFIED_BY", length=200)
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	private BigDecimal rev;

	//bi-directional many-to-one association to TLdb2SsdCrwReview
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SSD_RVW_ID", nullable=false)
	private TLdb2SsdCrwReview TLdb2SsdCrwReview;

	public TLdb2SsdCrwRule() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getIsLatestRvw() {
		return this.isLatestRvw;
	}

	public void setIsLatestRvw(String isLatestRvw) {
		this.isLatestRvw = isLatestRvw;
	}

	public BigDecimal getLabLabId() {
		return this.labLabId;
	}

	public void setLabLabId(BigDecimal labLabId) {
		this.labLabId = labLabId;
	}

	public BigDecimal getLabObjId() {
		return this.labObjId;
	}

	public void setLabObjId(BigDecimal labObjId) {
		this.labObjId = labObjId;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public BigDecimal getRev() {
		return this.rev;
	}

	public void setRev(BigDecimal rev) {
		this.rev = rev;
	}

	public TLdb2SsdCrwReview getTLdb2SsdCrwReview() {
		return this.TLdb2SsdCrwReview;
	}

	public void setTLdb2SsdCrwReview(TLdb2SsdCrwReview TLdb2SsdCrwReview) {
		this.TLdb2SsdCrwReview = TLdb2SsdCrwReview;
	}

}
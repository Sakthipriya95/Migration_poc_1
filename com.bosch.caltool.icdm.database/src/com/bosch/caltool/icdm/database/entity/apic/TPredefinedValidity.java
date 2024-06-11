package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_PREDEFINED_VALIDITY database table.
 */
@Entity
@Table(name = "T_PREDEFINED_VALIDITY")
@NamedQueries(value = {
    @NamedQuery(name = "TPredefinedValidity.findAll", query = "SELECT t FROM TPredefinedValidity t"),
    @NamedQuery(name = TPredefinedValidity.GET_ALL, query = "SELECT t FROM TPredefinedValidity t"),
    @NamedQuery(name = TPredefinedValidity.GET_BY_VALUEID, query = "SELECT t FROM TPredefinedValidity t where t.grpAttrVal.valueId =:valId") })
public class TPredefinedValidity implements Serializable {


  /**
   * Named query to fetch values.
   */
  public static final String GET_ALL = "TPredefinedValidity.getAll";
  /**
   * Named query to fetch predefinedValidity by value id
   */
  public static final String GET_BY_VALUEID = "TPredefinedValidity.getByValueId";

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "T_PREDEFINED_VALIDITY_ID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_PREDEFINED_VALIDITY_ID_GENERATOR")
  @Column(name = "PRE_VALDTY_ID")
  private long validityId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;


  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "VALIDITY_VALUE_ID")
  private TabvAttrValue validityValue;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "VALIDITY_ATTR_ID")
  private TabvAttribute validityAttribute;


  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "GRP_ATTR_VAL_ID")
  private TabvAttrValue grpAttrVal;

  public TPredefinedValidity() {}

  public long getValidityId() {
    return this.validityId;
  }

  public void setValidityId(final long validityId) {
    this.validityId = validityId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the validityValue
   */
  public TabvAttrValue getValidityValue() {
    return this.validityValue;
  }

  /**
   * @param validityValue the validityValue to set
   */
  public void setValidityValue(final TabvAttrValue validityValue) {
    this.validityValue = validityValue;
  }


  /**
   * @return the validityAttribute
   */
  public TabvAttribute getValidityAttribute() {
    return this.validityAttribute;
  }


  /**
   * @param validityAttribute the validityAttribute to set
   */
  public void setValidityAttribute(final TabvAttribute validityAttribute) {
    this.validityAttribute = validityAttribute;
  }


  /**
   * @return the grpAttrVal
   */
  public TabvAttrValue getGrpAttrVal() {
    return this.grpAttrVal;
  }


  /**
   * @param grpAttrVal the grpAttrVal to set
   */
  public void setGrpAttrVal(final TabvAttrValue grpAttrVal) {
    this.grpAttrVal = grpAttrVal;
  }


}
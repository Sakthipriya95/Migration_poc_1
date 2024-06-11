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

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_PREDEFINED_ATTR_VALUES database table.
 */
@Entity
@Table(name = "T_PREDEFINED_ATTR_VALUES")
@NamedQueries(value = {
    @NamedQuery(name = TPredefinedAttrValue.GET_ALL, query = "SELECT t FROM TPredefinedAttrValue t"),
    @NamedQuery(name = TPredefinedAttrValue.NQ_FIND_BY_VAL_ID, query = "SELECT val FROM TPredefinedAttrValue val WHERE val.grpAttrVal.valueId = :valId"),
    @NamedQuery(name = TPredefinedAttrValue.NQ_FIND_BY_ATTR_ID, query = "SELECT val FROM TPredefinedAttrValue val WHERE val.preDefinedAttr.attrId = :attrId"),
    @NamedQuery(name = TPredefinedAttrValue.NQ_FIND_BY_VAL_IDS, query = "SELECT val FROM TPredefinedAttrValue val WHERE val.grpAttrVal.valueId IN :valIdSet") })
@OptimisticLocking(cascade = true)

public class TPredefinedAttrValue implements Serializable {

  /**
   * Named query to fetch values.
   */
  public static final String GET_ALL = "TPredefinedAttrValue.getAll";

  /**
   * Named query to find value entity using attribute value id
   */
  public static final String NQ_FIND_BY_VAL_ID = "TPredefinedAttrValue.NQ_FIND_BY_VAL_ID";
  /**
   * Named query to find value entity using attribute value id set
   */
  public static final String NQ_FIND_BY_VAL_IDS = "TPredefinedAttrValue.NQ_FIND_BY_VAL_IDS";


  private static final long serialVersionUID = 1L;

  public static final String NQ_FIND_BY_ATTR_ID = "TPredefinedAttrValue.NQ_FIND_BY_ATTR_ID";

  @Id
  @SequenceGenerator(name = "T_PRE_ATTR_VALUES_ID_GENERATOR", sequenceName = "SEQV_ATTRIBUTES")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_PRE_ATTR_VALUES_ID_GENERATOR")
  @Column(name = "PRE_ATTRVL_ID")
  private long preAttrVallId;

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

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "PREDEFINED_ATTR_ID")
  private TabvAttribute preDefinedAttr;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "GRP_ATTR_VAL_ID")
  private TabvAttrValue grpAttrVal;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "PREDEFINED_VALUE_ID", nullable = false)
  private TabvAttrValue preDefAttrVal;

  public TPredefinedAttrValue() {}

  public long getPreAttrValId() {
    return this.preAttrVallId;
  }

  public void setPreAttrValId(final long preAttrValId) {
    this.preAttrVallId = preAttrValId;
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
   * @return the preDefinedAttr
   */
  public TabvAttribute getPreDefinedAttr() {
    return this.preDefinedAttr;
  }


  /**
   * @param preDefinedAttr the preDefinedAttr to set
   */
  public void setPreDefinedAttr(final TabvAttribute preDefinedAttr) {
    this.preDefinedAttr = preDefinedAttr;
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


  /**
   * @return the preDefAttrVal
   */
  public TabvAttrValue getPreDefAttrVal() {
    return this.preDefAttrVal;
  }

  /**
   * @param preDefAttrVal the preDefAttrVal to set
   */
  public void setPreDefAttrVal(final TabvAttrValue preDefAttrVal) {
    this.preDefAttrVal = preDefAttrVal;
  }
}
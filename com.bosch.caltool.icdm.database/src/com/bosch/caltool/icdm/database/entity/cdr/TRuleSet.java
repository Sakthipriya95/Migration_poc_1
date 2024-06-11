package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;

import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;


/**
 * The persistent class for the T_RULE_SET database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RULE_SET")

@NamedQueries(value = { @NamedQuery(name = TRuleSet.FIND_ALL, query = "SELECT t FROM TRuleSet t") })
public class TRuleSet implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String FIND_ALL = "TRuleSet.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RSET_ID", unique = true, nullable = false, precision = 15)
  private long rsetId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  @Column(name = "DESC_ENG", nullable = false, length = 500)
  private String descEng;

  @Column(name = "DESC_GER", length = 500)
  private String descGer;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "RSET_NAME", nullable = false, length = 100)
  private String rsetName;

  @Column(name = "SSD_NODE_ID", nullable = false, precision = 15)
  private Long ssdNodeId;


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TRuleSetParam
  @OneToMany(mappedBy = "TRuleSet", fetch = FetchType.LAZY)
  @PrivateOwned
  private List<TRuleSetParam> TRuleSetParams;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "ATTR_VALUE_ID")
  private TabvAttrValue tabvAttrValue;

  @Column(name = "READ_ACCESS_NEEDED_IN_DRT", nullable = false, length = 1)
  private String readAccessFlag;

  public TRuleSet() {}

  public long getRsetId() {
    return this.rsetId;
  }

  public void setRsetId(final long rsetId) {
    this.rsetId = rsetId;
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

  public String getDeletedFlag() {
    return this.deletedFlag;
  }

  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
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

  public String getRsetName() {
    return this.rsetName;
  }

  public void setRsetName(final String rsetName) {
    this.rsetName = rsetName;
  }

  public Long getSsdNodeId() {
    return this.ssdNodeId;
  }

  public void setSsdNodeId(final Long ssdNodeId) {
    this.ssdNodeId = ssdNodeId;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TRuleSetParam> getTRuleSetParams() {
    return this.TRuleSetParams;
  }

  public void setTRuleSetParams(final List<TRuleSetParam> TRuleSetParams) {
    this.TRuleSetParams = TRuleSetParams;
  }


  /**
   * @return the tabvAttrValue
   */
  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }


  /**
   * @param tabvAttrValue the tabvAttrValue to set
   */
  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }


  /**
   * @return the readAccessFlag
   */
  public String getReadAccessFlag() {
    return this.readAccessFlag;
  }


  /**
   * @param readAccessFlag the readAccessFlag to set
   */
  public void setReadAccessFlag(final String readAccessFlag) {
    this.readAccessFlag = readAccessFlag;
  }

}
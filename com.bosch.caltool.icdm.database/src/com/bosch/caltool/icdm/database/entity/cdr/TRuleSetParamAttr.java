package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

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
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;


/**
 * The persistent class for the T_RULE_SET_PARAM_ATTR database table.
 */

@NamedQueries(value = {
    @NamedQuery(name = TRuleSetParamAttr.NQ_GET_RS_PARAM_ATTR_BY_PARAMLIST, query = "SELECT pattr FROM TRuleSetParamAttr pattr where pattr.TRuleSetParam.rsetParamId in :paramList"),
    @NamedQuery(name = TRuleSetParamAttr.NQ_GET_RS_PARAM_ATTR_BY_ID, query = "SELECT pattr FROM TRuleSetParamAttr pattr,GttObjectName temp where pattr.TRuleSetParam.rsetParamId = temp.id") })
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RULE_SET_PARAM_ATTR")
public class TRuleSetParamAttr implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final String NQ_GET_RS_PARAM_ATTR_BY_PARAMLIST = "getrulesetparamattrbyparamlist";
  public static final String NQ_GET_RS_PARAM_ATTR_BY_ID = "getrulsetparamattrbyid";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RSET_PAR_ATTR_ID", unique = true, nullable = false, precision = 15)
  private long rsetParAttrId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TRuleSetParam
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RSET_PARAM_ID", nullable = false)
  private TRuleSetParam TRuleSetParam;

  // // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttribute tabvAttribute;

  public TRuleSetParamAttr() {}

  public long getRsetParAttrId() {
    return this.rsetParAttrId;
  }

  public void setRsetParAttrId(final long rsetParAttrId) {
    this.rsetParAttrId = rsetParAttrId;
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

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TRuleSetParam getTRuleSetParam() {
    return this.TRuleSetParam;
  }

  public void setTRuleSetParam(final TRuleSetParam TRuleSetParam) {
    this.TRuleSetParam = TRuleSetParam;
  }

}
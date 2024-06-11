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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_RULE_SET_PARAMS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RULE_SET_PARAMS")
public class TRuleSetParam implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RSET_PARAM_ID", unique = true, nullable = false, precision = 15)
  private long rsetParamId;

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

  // bi-directional many-to-one association to TFunction
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUNC_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TFunction TFunction;

  // bi-directional many-to-one association to TParameter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TParameter TParameter;

  // bi-directional many-to-one association to TRuleSet
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RSET_ID", nullable = false)
  private TRuleSet TRuleSet;


  // bi-directional many-to-one association to TRuleSetParamAttr
  @OneToMany(mappedBy = "TRuleSetParam", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @PrivateOwned
  private List<TRuleSetParamAttr> TRuleSetParamAttrs;

//bi-directional many-to-one association to TRulesetHwComponent
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "HW_COMPONENT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRulesetHwComponent tRulesetHwComponent;

//bi-directional many-to-one association to TRulesetParamResp
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRulesetParamResp tRulesetParamResp;

//bi-directional many-to-one association to TRulesetParamType
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_TYPE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRulesetParamType tRulesetParamType;

//bi-directional many-to-one association to TRulesetSysElement
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SYS_ELEMENT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRulesetSysElement tRulesetSysElement;


  public TRuleSetParam() {}

  public long getRsetParamId() {
    return this.rsetParamId;
  }

  public void setRsetParamId(final long rsetParamId) {
    this.rsetParamId = rsetParamId;
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

  public TFunction getTFunction() {
    return this.TFunction;
  }

  public void setTFunction(final TFunction TFunction) {
    this.TFunction = TFunction;
  }

  public TParameter getTParameter() {
    return this.TParameter;
  }

  public void setTParameter(final TParameter TParameter) {
    this.TParameter = TParameter;
  }

  public TRuleSet getTRuleSet() {
    return this.TRuleSet;
  }

  public void setTRuleSet(final TRuleSet TRuleSet) {
    this.TRuleSet = TRuleSet;
  }

  public List<TRuleSetParamAttr> getTRuleSetParamAttrs() {
    return this.TRuleSetParamAttrs;
  }

  public void setTRuleSetParamAttrs(final List<TRuleSetParamAttr> TRuleSetParamAttrs) {
    this.TRuleSetParamAttrs = TRuleSetParamAttrs;
  }


  /**
   * @return the tRulesetHwComponent
   */
  public TRulesetHwComponent gettRulesetHwComponent() {
    return this.tRulesetHwComponent;
  }


  /**
   * @param tRulesetHwComponent the tRulesetHwComponent to set
   */
  public void settRulesetHwComponent(final TRulesetHwComponent tRulesetHwComponent) {
    this.tRulesetHwComponent = tRulesetHwComponent;
  }


  /**
   * @return the tRulesetParamResp
   */
  public TRulesetParamResp gettRulesetParamResp() {
    return this.tRulesetParamResp;
  }


  /**
   * @param tRulesetParamResp the tRulesetParamResp to set
   */
  public void settRulesetParamResp(final TRulesetParamResp tRulesetParamResp) {
    this.tRulesetParamResp = tRulesetParamResp;
  }


  /**
   * @return the tRulesetParamType
   */
  public TRulesetParamType gettRulesetParamType() {
    return this.tRulesetParamType;
  }


  /**
   * @param tRulesetParamType the tRulesetParamType to set
   */
  public void settRulesetParamType(final TRulesetParamType tRulesetParamType) {
    this.tRulesetParamType = tRulesetParamType;
  }


  /**
   * @return the tRulesetSysElement
   */
  public TRulesetSysElement gettRulesetSysElement() {
    return this.tRulesetSysElement;
  }


  /**
   * @param tRulesetSysElement the tRulesetSysElement to set
   */
  public void settRulesetSysElement(final TRulesetSysElement tRulesetSysElement) {
    this.tRulesetSysElement = tRulesetSysElement;
  }

}
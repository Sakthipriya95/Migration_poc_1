package com.bosch.caltool.icdm.database.entity.comppkg;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_COMP_PKG database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_COMP_PKG")
@NamedQuery(name = "TCompPkg.findAll", query = "SELECT t FROM TCompPkg t")
public class TCompPkg implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "COMP_SEQ_GENERATOR", sequenceName = "SEQV_ATTRIBUTES", allocationSize = 50)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMP_SEQ_GENERATOR")
  @Column(name = "COMP_PKG_ID", unique = true, nullable = false)
  private long compPkgId;

  @Column(name = "COMP_PKG_NAME", nullable = false, length = 200)
  private String compPkgName;

  @Column(name = "COMP_PKG_TYPE", nullable = false, length = 5)
  private String compPkgType;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DESC_ENG", nullable = false, length = 2000)
  private String descEng;

  @Column(name = "DESC_GER", length = 2000)
  private String descGer;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "SSD_NODE_ID")
  private Long ssdNodeId;

  @Column(name = "SSD_PARAM_CLASS", length = 25)
  private String ssdParamClass;

  @Column(name = "SSD_USECASE", length = 25)
  private String ssdUsecase;

  @Column(name = "SSD_VERS_NODE_ID")
  private Long ssdVersNodeId;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  // bi-directional many-to-one association to TCompPkgBc
  @OneToMany(mappedBy = "TCompPkg", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @PrivateOwned
  private List<TCompPkgBc> TCompPkgBcs;

  // bi-directional many-to-one association to TCpRuleAttr
  @OneToMany(mappedBy = "TCompPkg", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @PrivateOwned
  private List<TCpRuleAttr> TCpRuleAttrs;

  public TCompPkg() {}

  public long getCompPkgId() {
    return this.compPkgId;
  }

  public void setCompPkgId(final long compPkgId) {
    this.compPkgId = compPkgId;
  }

  public String getCompPkgName() {
    return this.compPkgName;
  }

  public void setCompPkgName(final String compPkgName) {
    this.compPkgName = compPkgName;
  }

  public String getCompPkgType() {
    return this.compPkgType;
  }

  public void setCompPkgType(final String compPkgType) {
    this.compPkgType = compPkgType;
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

  public Long getSsdNodeId() {
    return this.ssdNodeId;
  }

  public void setSsdNodeId(final Long ssdNodeId) {
    this.ssdNodeId = ssdNodeId;
  }

  public String getSsdParamClass() {
    return this.ssdParamClass;
  }

  public void setSsdParamClass(final String ssdParamClass) {
    this.ssdParamClass = ssdParamClass;
  }

  public String getSsdUsecase() {
    return this.ssdUsecase;
  }

  public void setSsdUsecase(final String ssdUsecase) {
    this.ssdUsecase = ssdUsecase;
  }

  public Long getSsdVersNodeId() {
    return this.ssdVersNodeId;
  }

  public void setSsdVersNodeId(final Long ssdVersNodeId) {
    this.ssdVersNodeId = ssdVersNodeId;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TCompPkgBc> getTCompPkgBcs() {
    return this.TCompPkgBcs;
  }

  public void setTCompPkgBcs(final List<TCompPkgBc> TCompPkgBcs) {
    this.TCompPkgBcs = TCompPkgBcs;
  }

  public List<TCpRuleAttr> getTCpRuleAttrs() {
    return this.TCpRuleAttrs;
  }

  public void setTCpRuleAttrs(final List<TCpRuleAttr> TCpRuleAttrs) {
    this.TCpRuleAttrs = TCpRuleAttrs;
  }

  public TCpRuleAttr addTCpRuleAttr(final TCpRuleAttr TCpRuleAttr) {
    getTCpRuleAttrs().add(TCpRuleAttr);
    TCpRuleAttr.setTCompPkg(this);

    return TCpRuleAttr;
  }

  public TCpRuleAttr removeTCpRuleAttr(final TCpRuleAttr TCpRuleAttr) {
    getTCpRuleAttrs().remove(TCpRuleAttr);
    TCpRuleAttr.setTCompPkg(null);

    return TCpRuleAttr;
  }


  /**
   * @return the deletedFlag
   */
  public String getDeletedFlag() {
    return this.deletedFlag;
  }


  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

}
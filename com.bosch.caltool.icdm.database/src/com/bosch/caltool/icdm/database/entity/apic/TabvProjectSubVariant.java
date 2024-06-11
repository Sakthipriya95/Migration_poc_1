package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;


/**
 * The persistent class for the TABV_PROJECT_SUB_VARIANTS database table.
 */
@Entity
@Table(name = "TABV_PROJECT_SUB_VARIANTS")
@OptimisticLocking(cascade = true)
@NamedQuery(name = "TabvProjectSubVariant.getAllProjVars", query = "SELECT t FROM TabvProjectSubVariant t")
public class TabvProjectSubVariant implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL_SUB_VARIANTS = "TabvProjectSubVariant.getAllProjVars";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "SUB_VARIANT_ID", unique = true, nullable = false, precision = 15)
  private long subVariantId;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID", nullable = false)
  private TPidcVersion tPidcVersion;

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VARIANT_ID", nullable = false)
  private TabvProjectVariant tabvProjectVariant;

  // bi-directional many-to-one association to TabvProjSubVariantsAttr
  @OneToMany(mappedBy = "tabvProjectSubVariant")
  // Icdm-230
  @PrivateOwned
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs;

  // bi-directional many-to-one association to TFocusMatrixVersionAttr
  @OneToMany(mappedBy = "tabvProjectSubVariant", fetch = FetchType.LAZY)
  private Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs;


  // bi-directional many-to-one association to TPidcRmDefinition
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_RM_ID")
  private TPidcRmDefinition TPidcRmDefinition;

  // bi-directional many-to-one association to TPidcSubVarCocWp
  @PrivateOwned
  @OneToMany(mappedBy = "tabvprojsubvar")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TPidcSubVarCocWp> tPidcSubVarCocWp;


  public TabvProjectSubVariant() {}

  public long getSubVariantId() {
    return this.subVariantId;
  }

  public void setSubVariantId(final long subVariantId) {
    this.subVariantId = subVariantId;
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

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }

  public TabvProjectVariant getTabvProjectVariant() {
    return this.tabvProjectVariant;
  }

  public void setTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    this.tabvProjectVariant = tabvProjectVariant;
  }

  public List<TabvProjSubVariantsAttr> getTabvProjSubVariantsAttrs() {
    return this.tabvProjSubVariantsAttrs;
  }

  public void setTabvProjSubVariantsAttrs(final List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs) {
    this.tabvProjSubVariantsAttrs = tabvProjSubVariantsAttrs;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Set<TFocusMatrixVersionAttr> getTFocusMatrixVersionAttrs() {
    return this.TFocusMatrixVersionAttrs;
  }

  public void setTFocusMatrixVersionAttrs(final Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs) {
    this.TFocusMatrixVersionAttrs = TFocusMatrixVersionAttrs;
  }

  public TFocusMatrixVersionAttr addTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().add(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTabvProjectSubVariant(this);

    return TFocusMatrixVersionAttr;
  }

  public TFocusMatrixVersionAttr removeTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().remove(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTabvProjectSubVariant(null);

    return TFocusMatrixVersionAttr;
  }


  public TPidcRmDefinition getTPidcRmDefinition() {
    return this.TPidcRmDefinition;
  }

  public void setTPidcRmDefinition(final TPidcRmDefinition TPidcRmDefinition) {
    this.TPidcRmDefinition = TPidcRmDefinition;
  }


  /**
   * @return the tPidcSubVarCocWp
   */
  public List<TPidcSubVarCocWp> gettPidcSubVarCocWp() {
    if (CommonUtils.isNull(this.tPidcSubVarCocWp)) {
      this.tPidcSubVarCocWp = new ArrayList<>();
    }
    return this.tPidcSubVarCocWp;
  }


  /**
   * @param tPidcSubVarCocWp the tPidcSubVarCocWp to set
   */
  public void settPidcSubVarCocWp(final List<TPidcSubVarCocWp> tPidcSubVarCocWp) {
    this.tPidcSubVarCocWp = tPidcSubVarCocWp;
  }

}
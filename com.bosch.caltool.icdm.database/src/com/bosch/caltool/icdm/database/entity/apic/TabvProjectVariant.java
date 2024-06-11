package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrPidcVariant;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelivery;

/**
 * The persistent class for the TABV_PROJECT_VARIANTS database table.
 */


@Entity
// Icdm-230
@OptimisticLocking(cascade = true)
@Table(name = "TABV_PROJECT_VARIANTS")
@NamedQueries(value = {
    @NamedQuery(name = TabvProjectVariant.NQ_GET_PIDC_VARIANT, query = "                                    " +
        "SELECT varattr.tabvProjectVariant                                                                  " +
        "FROM TabvVariantsAttr varattr, TabvProjectVariant var                                              " +
        "WHERE  var = varattr.tabvProjectVariant                                                            " +
        "   AND varattr.tabvAttrValue.valueId = :valueId                                                    " +
        "   AND varattr.tPidcVersion.pidcVersId = :pidcVersId                                               " +
        "   AND var.deletedFlag = 'N'") })
public class TabvProjectVariant implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * get the pidc a2l from a2l file id and project id.
   */
  public static final String GET_PIDC_VERSION = "TabvProjectVariant.GET_PIDC_VERSION";

  /**
   * get pidc variant for sdom pver value id, pidc version id and deleted flag - No
   */
  public static final String NQ_GET_PIDC_VARIANT = "TabvProjectVariant.getPidcVariant";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "VARIANT_ID", unique = true, nullable = false, precision = 15)
  private long variantId;

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

  // bi-directional many-to-one association to TabvProjectSubVariant
  @OneToMany(mappedBy = "tabvProjectVariant")
  // Icdm-230
  @PrivateOwned
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvProjectSubVariant> tabvProjectSubVariants;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID", nullable = false)
  private TPidcVersion tPidcVersion;

  // bi-directional many-to-one association to TabvProjSubVariantsAttr
  @OneToMany(mappedBy = "tabvProjectVariant")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs;

  // bi-directional many-to-one association to TCDFxDelivery
  @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCDFxDelivery> tCdfxDeliveryList;

  // bi-directional many-to-one association to TabvVariantsAttr
  @OneToMany(mappedBy = "tabvProjectVariant")
  // Icdm-230
  @PrivateOwned
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvVariantsAttr> tabvVariantsAttrs;

  // bi-directional many-to-one association to TFocusMatrixVersionAttr
  @OneToMany(mappedBy = "tabvProjectVariant", fetch = FetchType.LAZY)
  private Set<TFocusMatrixVersionAttr> tFocusMatrixVersionAttrs;

  // bi-directional many-to-one association to TPidcRmDefinition
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_RM_ID")
  private TPidcRmDefinition tPidcRmDefinition;

  // bi-directional many-to-one association to TEmrPidcVariant
  @OneToMany(mappedBy = "tabvProjectVariant")
  @PrivateOwned
  private Set<TEmrPidcVariant> tEmrPidcVariants;

  // bi-directional many-to-one association to TA2lVarGrpVarMapping
  @OneToMany(mappedBy = "tabvProjectVariant", fetch = FetchType.LAZY)
  private List<TA2lVarGrpVariantMapping> tA2lVarGrpVariantMappings;

//bi-directional many-to-one association to TRvwQnaireRespVariant
  @OneToMany(mappedBy = "tabvProjectVariant", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants;

  // bi-directional many-to-one association to TPidcVariantCocWP
  @PrivateOwned
  @OneToMany(mappedBy = "tabvprojvar")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TPidcVariantCocWp> tPidcVarCocWp;

  // bi-directional one-to-many association to TA2lWpResponsibilityStatus
  @PrivateOwned
  @OneToMany(mappedBy = "tabvProjVariant", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lWpResponsibilityStatus> tA2lWPRespStatus;

  public TabvProjectVariant() {
    // Public constructor
  }

  public long getVariantId() {
    return this.variantId;
  }

  public void setVariantId(final long variantId) {
    this.variantId = variantId;
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

  public List<TabvProjectSubVariant> getTabvProjectSubVariants() {
    return this.tabvProjectSubVariants;
  }

  public void setTabvProjectSubVariants(final List<TabvProjectSubVariant> tabvProjectSubVariants) {
    this.tabvProjectSubVariants = tabvProjectSubVariants;
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

  public List<TabvProjSubVariantsAttr> getTabvProjSubVariantsAttrs() {
    return this.tabvProjSubVariantsAttrs;
  }

  public void setTabvProjSubVariantsAttrs(final List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs) {
    this.tabvProjSubVariantsAttrs = tabvProjSubVariantsAttrs;
  }

  public List<TabvVariantsAttr> getTabvVariantsAttrs() {
    return this.tabvVariantsAttrs;
  }

  public void setTabvVariantsAttrs(final List<TabvVariantsAttr> tabvVariantsAttrs) {
    this.tabvVariantsAttrs = tabvVariantsAttrs;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }


  public Set<TFocusMatrixVersionAttr> getTFocusMatrixVersionAttrs() {
    return this.tFocusMatrixVersionAttrs;
  }

  public void setTFocusMatrixVersionAttrs(final Set<TFocusMatrixVersionAttr> tFocusMatrixVersionAttrs) {
    this.tFocusMatrixVersionAttrs = tFocusMatrixVersionAttrs;
  }

  public TFocusMatrixVersionAttr addTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr tFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().add(tFocusMatrixVersionAttr);
    tFocusMatrixVersionAttr.setTabvProjectVariant(this);

    return tFocusMatrixVersionAttr;
  }

  public TFocusMatrixVersionAttr removeTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr tFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().remove(tFocusMatrixVersionAttr);
    tFocusMatrixVersionAttr.setTabvProjectVariant(null);

    return tFocusMatrixVersionAttr;
  }


  /**
   * @return the tPidcVarCocWp
   */
  public List<TPidcVariantCocWp> gettPidcVarCocWp() {
    if (CommonUtils.isNull(this.tPidcVarCocWp)) {
      this.tPidcVarCocWp = new ArrayList<>();
    }
    return this.tPidcVarCocWp;
  }


  /**
   * @param tPidcVarCocWp the tPidcVarCocWp to set
   */
  public void settPidcVarCocWp(final List<TPidcVariantCocWp> tPidcVarCocWp) {
    this.tPidcVarCocWp = tPidcVarCocWp;
  }

  public TPidcRmDefinition getTPidcRmDefinition() {
    return this.tPidcRmDefinition;
  }

  public void setTPidcRmDefinition(final TPidcRmDefinition tPidcRmDefinition) {
    this.tPidcRmDefinition = tPidcRmDefinition;
  }

  public Set<TEmrPidcVariant> getTEmrPidcVariants() {
    return this.tEmrPidcVariants;
  }

  public void setTEmrPidcVariants(final Set<TEmrPidcVariant> temrPidcVariants) {
    this.tEmrPidcVariants = temrPidcVariants;
  }

  public TEmrPidcVariant addTEmrPidcVariant(final TEmrPidcVariant temrPidcVariant) {
    if (getTEmrPidcVariants() == null) {
      this.tEmrPidcVariants = new HashSet<>();
    }
    getTEmrPidcVariants().add(temrPidcVariant);
    temrPidcVariant.setTabvProjectVariant(this);
    return temrPidcVariant;
  }

  public TEmrPidcVariant removeTEmrPidcVariant(final TEmrPidcVariant temrPidcVariant) {
    getTEmrPidcVariants().remove(temrPidcVariant);
    temrPidcVariant.setTabvProjectVariant(null);
    return temrPidcVariant;
  }

  /**
   * @return the tA2lVarGrpVariantMappings
   */
  public List<TA2lVarGrpVariantMapping> gettA2lVarGrpVariantMappings() {
    return this.tA2lVarGrpVariantMappings;
  }


  /**
   * @param tA2lVarGrpVariantMappings the tA2lVarGrpVariantMappings to set
   */
  public void settA2lVarGrpVariantMappings(final List<TA2lVarGrpVariantMapping> tA2lVarGrpVariantMappings) {
    this.tA2lVarGrpVariantMappings = tA2lVarGrpVariantMappings;
  }


  /**
   * @return the tCdfxDeliveryList
   */
  public List<TCDFxDelivery> gettCdfxDeliveryList() {
    return this.tCdfxDeliveryList;
  }


  /**
   * @param tCdfxDeliveryList the tCdfxDeliveryList to set
   */
  public void settCdfxDeliveryList(final List<TCDFxDelivery> tCdfxDeliveryList) {
    this.tCdfxDeliveryList = tCdfxDeliveryList;
  }


  /**
   * @return the tRvwQnaireRespVariants
   */
  public Set<TRvwQnaireRespVariant> getTRvwQnaireRespVariants() {
    return this.tRvwQnaireRespVariants;
  }


  /**
   * @param tRvwQnaireRespVariants the tRvwQnaireRespVariants to set
   */
  public void setTRvwQnaireRespVariants(final Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants) {
    this.tRvwQnaireRespVariants = tRvwQnaireRespVariants;
  }

  /**
   * @param tRvwQnaireRespVariants as input
   * @return tRvwQnaireRespVariants
   */
  public TRvwQnaireRespVariant addTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariants) {
    if (getTRvwQnaireRespVariants() == null) {
      setTRvwQnaireRespVariants(new HashSet<>());
    }
    getTRvwQnaireRespVariants().add(tRvwQnaireRespVariants);
    tRvwQnaireRespVariants.setTabvProjectVariant(this);

    return tRvwQnaireRespVariants;
  }

  /**
   * @param tRvwQnaireRespVariants as input
   * @return tRvwQnaireRespVariants
   */
  public TRvwQnaireRespVariant removeTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariants) {
    getTRvwQnaireRespVariants().remove(tRvwQnaireRespVariants);
    return tRvwQnaireRespVariants;
  }


  /**
   * @return the tA2lWPRespStatus
   */
  public List<TA2lWpResponsibilityStatus> gettA2lWPRespStatus() {
    return this.tA2lWPRespStatus;
  }


  /**
   * @param tA2lWPRespStatus the tA2lWPRespStatus to set
   */
  public void settA2lWPRespStatus(final List<TA2lWpResponsibilityStatus> tA2lWPRespStatus) {
    this.tA2lWPRespStatus = tA2lWPRespStatus;
  }


}
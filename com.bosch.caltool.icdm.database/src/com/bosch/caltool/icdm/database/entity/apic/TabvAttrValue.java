package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;


/**
 * The persistent class for the TABV_ATTR_VALUES database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TabvAttrValue.NQ_FIND_BY_TEXT_VAL_ATTR_ID, query = "SELECT val FROM TabvAttrValue val WHERE val.textvalueEng = :textValEng and val.tabvAttribute.attrId = :attrID"),
    @NamedQuery(name = TabvAttrValue.NQ_FIND_BY_VAL_ID, query = "SELECT val FROM TabvAttrValue val WHERE val.valueId = :valId"),
    @NamedQuery(name = TabvAttrValue.NQ_GET_SDOM_PVER_VAL_BY_PROJ_N_A2L_ID, query = "                               " +
        "SELECT val FROM TabvAttrValue val                                                                          " +
        "WHERE val.textvalueEng =                                                                                   " +
        "    ( SELECT a2l.sdomPverName FROM TPidcA2l a2l                                                            " +
        "      WHERE a2l.TPidcVersion.pidcVersId = :pidcVersionId                                                   " +
        "         AND a2l.mvTa2lFileinfo.id = :a2lFileId)                                                           " +
        "  and val.tabvAttribute.attrId = :sdomPverAttrId                                                         ") })

@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TabvAttrValue.NNQ_FIND_ALL_SDOM_PVER_NAMES_BY_PIDC_ID, query = "                           " +
        "select textvalue_eng from tabv_attr_values                                                                     " +
        "  where value_id in (                                                                                          " +
        "    select pattr.value_id from tabv_project_attr pattr, t_pidc_version pvers                                   " +
        "      where pvers.pidc_vers_id = pattr.pidc_vers_id and pvers.project_id = ? and attr_id = ?                   " +
        "    union                                                                                                      " +
        "    select vattr.value_id from tabv_variants_attr vattr, t_pidc_version pvers, tabv_project_variants variants  " +
        "      where vattr.variant_id = variants.variant_id and pvers.pidc_vers_id = variants.pidc_vers_id and  pvers.project_id = ? and attr_id = ? and variants.deleted_flag = 'N')" +
        "  and deleted_flag = 'N'") })
@Entity
// Icdm-230
@OptimisticLocking(cascade = true)
@Table(name = "TABV_ATTR_VALUES")
public class TabvAttrValue implements Serializable {

  /**
   * Named query to find value entity using English text value and attribute id
   */
  public static final String NQ_FIND_BY_TEXT_VAL_ATTR_ID = "TabvAttrValue.NQ_FIND_BY_TEXT_VAL_ATTR_ID";

  /**
   * Named native query to find all sdom pver names by PIDC ID.
   * <p>
   * params - PIDC ID, SDOM PVER attribute ID
   */
  public static final String NNQ_FIND_ALL_SDOM_PVER_NAMES_BY_PIDC_ID =
      "TabvAttrValue.NNQ_FIND_ALL_SDOM_PVER_NAMES_BY_PIDC_ID";

  private static final long serialVersionUID = 1L;
  /**
   * Named query to find value entity using attribute value id
   */
  public static final String NQ_FIND_BY_VAL_ID = "TabvAttrValue.NQ_FIND_BY_VAL_ID";

  /**
   * Fetch SDOM PVER Attribute Value object by pidc version ID and A2L file ID
   * <p>
   *
   * @param pidcVersionId PIDC Version ID
   * @param a2lFileId A2L File Info ID
   */
  public static final String NQ_GET_SDOM_PVER_VAL_BY_PROJ_N_A2L_ID =
      "TabvAttrValue.getSdomPverAttrValByProjAndA2LFileId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "VALUE_ID", unique = true, nullable = false, precision = 15)
  private long valueId;

  @Column(length = 1)
  private String boolvalue;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  private Timestamp datevalue;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(precision = 22)
  private BigDecimal numvalue;

  @Column(length = 255)
  private String othervalue;

  @Column(name = "TEXTVALUE_ENG", length = 100)
  private String textvalueEng;

  @Column(name = "TEXTVALUE_GER", length = 100)
  private String textvalueGer;

  @Column(name = "VALUE_DESC_ENG", length = 255)
  private String valueDescEng;

  @Column(name = "VALUE_DESC_GER", length = 255)
  private String valueDescGer;

  @Column(name = "CLEARING_STATUS", length = 1)
  private String clearingStatus;


  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = true)
  private TabvApicUser tabvApicUser;


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;
  // bi-directional many-to-one association to TWorkpackageDivision
  @OneToMany(mappedBy = "tabvAttrValue", fetch = FetchType.LAZY)
  private List<TWorkpackageDivision> TWorkpackageDivisions;
  // bi-directional many-to-one association to TabvAttrDependency
  @OneToMany(mappedBy = "tabvAttrValue")
  @BatchFetch(value = BatchFetchType.JOIN)
  @PrivateOwned
  // Icdm-230
  private List<TabvAttrDependency> tabvAttrDependencies;

  // bi-directional many-to-one association to TabvAttrDependency
  // icdm-516
  @OneToMany(mappedBy = "tabvAttrValueD")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvAttrDependency> tabvAttrDependenciesD;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvProjectidcard
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TabvProjectidcard> tabvProjectidcards;

  // bi-directional many-to-one association to TabvProjectAttr
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TabvProjectAttr> tabvProjectAttrs;

  // bi-directional many-to-one association to TabvProjectSubVariant
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TabvProjectSubVariant> tabvProjectSubVariants;

  // bi-directional many-to-one association to TabvProjectVariant
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TabvProjectVariant> tabvProjectVariants;

  // bi-directional many-to-one association to TabvProjSubVariantsAttr
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs;

  // bi-directional many-to-one association to TabvVariantsAttr
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TabvVariantsAttr> tabvVariantsAttrs;

  // bi-directional many-to-one association to TCharacteristicValue
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "CHAR_VAL_ID")
  private TCharacteristicValue tCharacteristicValue;

  @Column(name = "CHANGE_COMMENT", length = 4000)
  private String changeComment;

  // bi-directional many-to-one association to TMandatoryAttr
  @OneToMany(mappedBy = "tabvAttrValue", fetch = FetchType.LAZY)
  private List<TMandatoryAttr> tMandatoryAttrs;

  // bi-directional many-to-one association to TAliasDetail
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TAliasDetail> tAliasDetails;

  // bi-directional one-to-many association to TGroupAttrValue
  @OneToMany(mappedBy = "grpAttrVal", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TPredefinedAttrValue> tGroupAttrValue;

  // bi-directional one-to-many association to TGroupAttrValue
  @OneToMany(mappedBy = "grpAttrVal", fetch = FetchType.LAZY)
  private List<TPredefinedValidity> tGroupAttrValidity;

  // bi-directional many-to-one association to TFocusMatrixVersionAttr
  @OneToMany(mappedBy = "tabvAttrValue", fetch = FetchType.LAZY)
  private Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs;

  // bi-directional many-to-one association to TA2lGroup
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TA2lGroup> tA2lGroups;

  // bi-directional many-to-one association to TRuleSet
  @OneToMany(mappedBy = "tabvAttrValue")
  private Set<TRuleSet> TRuleSets;

  // bi-directional many-to-one association to TLink
  @OneToMany(mappedBy = "tabvAttrValue", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @PrivateOwned
  private List<TLink> tLinks;

  // bi-directional many-to-one association to TQuestionDepenAttrValue
  @OneToMany(mappedBy = "tabvAttrValue", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues;

  // bi-directional many-to-one association to TFc2wpDefinition
  @OneToMany(mappedBy = "tabvAttrValueDiv", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TFc2wpDefinition> tFc2wpDefinitions;

  // bi-directional many-to-one association to TSsdValue
  @OneToMany(mappedBy = "tabvAttrValue")
  private List<TSsdValue> TSsdValues;

  public TabvAttrValue() {}

  /**
   * @return the changeComment
   */
  public String getChangeComment() {
    return this.changeComment;
  }


  /**
   * @param changeComment the changeComment to set
   */
  public void setChangeComment(final String changeComment) {
    this.changeComment = changeComment;
  }

  public long getValueId() {
    return this.valueId;
  }

  public void setValueId(final long valueId) {
    this.valueId = valueId;
  }

  public String getBoolvalue() {
    return this.boolvalue;
  }

  public void setBoolvalue(final String boolvalue) {
    this.boolvalue = boolvalue;
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

  public Timestamp getDatevalue() {
    return this.datevalue;
  }

  public void setDatevalue(final Timestamp datevalue) {
    this.datevalue = datevalue;
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

  public BigDecimal getNumvalue() {
    return this.numvalue;
  }

  public void setNumvalue(final BigDecimal numvalue) {
    this.numvalue = numvalue;
  }

  public String getOthervalue() {
    return this.othervalue;
  }

  public void setOthervalue(final String othervalue) {
    this.othervalue = othervalue;
  }

  public String getTextvalueEng() {
    return this.textvalueEng;
  }

  public void setTextvalueEng(final String textvalueEng) {
    this.textvalueEng = textvalueEng;
  }

  public String getTextvalueGer() {
    return this.textvalueGer;
  }

  public void setTextvalueGer(final String textvalueGer) {
    this.textvalueGer = textvalueGer;
  }

  public String getValueDescEng() {
    return this.valueDescEng;
  }

  public void setValueDescEng(final String valueDescEng) {
    this.valueDescEng = valueDescEng;
  }


  /**
   * @return the tRuleSets
   */
  public Set<TRuleSet> getTRuleSets() {
    return this.TRuleSets;
  }


  /**
   * @param tRuleSets the tRuleSets to set
   */
  public void setTRuleSets(final Set<TRuleSet> tRuleSets) {
    this.TRuleSets = tRuleSets;
  }

  public String getValueDescGer() {
    return this.valueDescGer;
  }

  public void setValueDescGer(final String valueDescGer) {
    this.valueDescGer = valueDescGer;
  }

  public List<TabvAttrDependency> getTabvAttrDependencies() {
    return this.tabvAttrDependencies;
  }

  public void setTabvAttrDependencies(final List<TabvAttrDependency> tabvAttrDependencies) {
    this.tabvAttrDependencies = tabvAttrDependencies;
  }

  public List<TabvAttrDependency> getTabvAttrDependenciesD() {
    return this.tabvAttrDependenciesD;
  }

  public void setTabvAttrDependenciesD(final List<TabvAttrDependency> tabvAttrDependenciesD) {
    this.tabvAttrDependenciesD = tabvAttrDependenciesD;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public List<TabvProjectidcard> getTabvProjectidcards() {
    return this.tabvProjectidcards;
  }

  public void setTabvProjectidcards(final List<TabvProjectidcard> tabvProjectidcards) {
    this.tabvProjectidcards = tabvProjectidcards;
  }

  public List<TabvProjectAttr> getTabvProjectAttrs() {
    return this.tabvProjectAttrs;
  }

  public void setTabvProjectAttrs(final List<TabvProjectAttr> tabvProjectAttrs) {
    this.tabvProjectAttrs = tabvProjectAttrs;
  }

  public List<TabvProjectSubVariant> getTabvProjectSubVariants() {
    return this.tabvProjectSubVariants;
  }

  public void setTabvProjectSubVariants(final List<TabvProjectSubVariant> tabvProjectSubVariants) {
    this.tabvProjectSubVariants = tabvProjectSubVariants;
  }

  public List<TabvProjectVariant> getTabvProjectVariants() {
    return this.tabvProjectVariants;
  }

  public void setTabvProjectVariants(final List<TabvProjectVariant> tabvProjectVariants) {
    this.tabvProjectVariants = tabvProjectVariants;
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

  public String getClearingStatus() {
    return this.clearingStatus;
  }

  public void setClearingStatus(final String clearingStatus) {
    this.clearingStatus = clearingStatus;
  }

  /**
   * @return the tCharacteristicValue
   */
  public TCharacteristicValue gettCharacteristicValue() {
    return this.tCharacteristicValue;
  }


  /**
   * @param tCharacteristicValue the tCharacteristicValue to set
   */
  public void settCharacteristicValue(final TCharacteristicValue tCharacteristicValue) {
    this.tCharacteristicValue = tCharacteristicValue;
  }

  public List<TMandatoryAttr> getTMandatoryAttrs() {
    return this.tMandatoryAttrs;
  }

  public void setTMandatoryAttrs(final List<TMandatoryAttr> TMandatoryAttrs) {
    this.tMandatoryAttrs = TMandatoryAttrs;
  }

  public List<TAliasDetail> getTAliasDetails() {
    return this.tAliasDetails;
  }

  public void setTAliasDetails(final List<TAliasDetail> tAliasDetails) {
    this.tAliasDetails = tAliasDetails;
  }

  public void addTAliasDetail(final TAliasDetail tAliasDetail) {
    List<TAliasDetail> tAliasDetailList = getTAliasDetails();
    if (tAliasDetailList == null) {
      tAliasDetailList = new ArrayList<>();
      setTAliasDetails(tAliasDetailList);
    }
    tAliasDetailList.add(tAliasDetail);

    tAliasDetail.setTabvAttrValue(this);
  }

  public void removeTAliasDetail(final TAliasDetail tAliasDetail) {
    List<TAliasDetail> tAliasDetailList = getTAliasDetails();
    if (tAliasDetailList != null) {
      tAliasDetailList.remove(tAliasDetail);
    }
  }

  public List<TWorkpackageDivision> getTWorkpackageDivisions() {
    return this.TWorkpackageDivisions;
  }

  public void setTWorkpackageDivisions(final List<TWorkpackageDivision> TWorkpackageDivisions) {
    this.TWorkpackageDivisions = TWorkpackageDivisions;
  }


  /**
   * @return the tGroupAttrValue
   */
  public List<TPredefinedAttrValue> gettGroupAttrValue() {
    return this.tGroupAttrValue;
  }


  /**
   * @param tGroupAttrValue the tGroupAttrValue to set
   */
  public void settGroupAttrValue(final List<TPredefinedAttrValue> tGroupAttrValue) {
    this.tGroupAttrValue = tGroupAttrValue;
  }


  /**
   * @return the tGroupAttrValidity
   */
  public List<TPredefinedValidity> gettGroupAttrValidity() {
    return this.tGroupAttrValidity;
  }


  /**
   * @param tGroupAttrValidity the tGroupAttrValidity to set
   */
  public void settGroupAttrValidity(final List<TPredefinedValidity> tGroupAttrValidity) {
    this.tGroupAttrValidity = tGroupAttrValidity;
  }

  public Set<TFocusMatrixVersionAttr> getTFocusMatrixVersionAttrs() {
    return this.TFocusMatrixVersionAttrs;
  }

  public void setTFocusMatrixVersionAttrs(final Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs) {
    this.TFocusMatrixVersionAttrs = TFocusMatrixVersionAttrs;
  }

  public TFocusMatrixVersionAttr addTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().add(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTabvAttrValue(this);

    return TFocusMatrixVersionAttr;
  }

  public TFocusMatrixVersionAttr removeTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().remove(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTabvAttrValue(null);

    return TFocusMatrixVersionAttr;
  }

  public List<TA2lGroup> getTA2lGroups() {
    return this.tA2lGroups;
  }

  public void setTA2lGroups(final List<TA2lGroup> TA2lGroups) {
    this.tA2lGroups = TA2lGroups;
  }


  /**
   * @return the tLinks
   */
  public List<TLink> gettLinks() {
    return this.tLinks;
  }


  /**
   * @param tLinks the tLinks to set
   */
  public void settLinks(final List<TLink> tLinks) {
    this.tLinks = tLinks;
  }

  public TLink removeTLink(final TLink TLink) {
    gettLinks().remove(TLink);
    TLink.setTabvAttrValue(null);
    return TLink;
  }

  /**
   * Gets the t question depen attr values.
   *
   * @return the t question depen attr values
   */
  public Set<TQuestionDepenAttrValue> getTQuestionDepenAttrValues() {
    return this.tQuestionDepenAttrValues;
  }

  /**
   * Sets the t question depen attr values.
   *
   * @param tQuestionDepenAttrValues the new t question depen attr values
   */
  public void setTQuestionDepenAttrValues(final Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues) {
    this.tQuestionDepenAttrValues = tQuestionDepenAttrValues;
  }

  /**
   * Adds the T question depen attr values.
   *
   * @param tQuestionDepenAttrValue the t question depen attr value
   * @return the t question depen attr value
   */
  public TQuestionDepenAttrValue addTQuestionDepenAttrValues(final TQuestionDepenAttrValue tQuestionDepenAttrValue) {
    if (getTQuestionDepenAttrValues() == null) {
      this.tQuestionDepenAttrValues = new HashSet<>();
    }
    getTQuestionDepenAttrValues().add(tQuestionDepenAttrValue);
    tQuestionDepenAttrValue.setTabvAttrValue(this);

    return tQuestionDepenAttrValue;
  }

  /**
   * Removes the T question depen attr values.
   *
   * @param tQuestionDepenAttrValue the t question depen attr value
   * @return the t question depen attr value
   */
  public TQuestionDepenAttrValue removeTQuestionDepenAttrValues(final TQuestionDepenAttrValue tQuestionDepenAttrValue) {
    if (getTQuestionDepenAttrValues() != null) {
      getTQuestionDepenAttrValues().remove(tQuestionDepenAttrValue);
    }
    return tQuestionDepenAttrValue;
  }

  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }

  /**
   * Gets the t fc 2 wp definitions.
   *
   * @return the tFc2wpDefinitions
   */
  public Set<TFc2wpDefinition> gettFc2wpDefinitions() {
    return this.tFc2wpDefinitions;
  }


  /**
   * Sets the t fc 2 wp definitions.
   *
   * @param tFc2wpDefinitions the tFc2wpDefinitions to set
   */
  public void settFc2wpDefinitions(final Set<TFc2wpDefinition> tFc2wpDefinitions) {
    this.tFc2wpDefinitions = tFc2wpDefinitions;
  }

  /**
   * Adds the T fc 2 wp definitions.
   *
   * @param tFc2wpDefinition the t fc 2 wp definition
   * @return the t fc 2 wp definition
   */
  public TFc2wpDefinition addTFc2wpDefinitions(final TFc2wpDefinition tFc2wpDefinition) {
    if (gettFc2wpDefinitions() == null) {
      this.tFc2wpDefinitions = new HashSet<>();
    }
    gettFc2wpDefinitions().add(tFc2wpDefinition);
    tFc2wpDefinition.setTabvAttrValueDiv(this);

    return tFc2wpDefinition;
  }

  /**
   * Removes the T fc 2 wp definitions.
   *
   * @param tFc2wpDefinition the t fc 2 wp definition
   * @return the t fc 2 wp definition
   */
  public TFc2wpDefinition removeTFc2wpDefinitions(final TFc2wpDefinition tFc2wpDefinition) {
    if (gettFc2wpDefinitions() != null) {
      gettFc2wpDefinitions().remove(tFc2wpDefinition);
    }
    return tFc2wpDefinition;
  }


  /**
   * @return the tSsdValues
   */
  public List<TSsdValue> getTSsdValues() {
    return this.TSsdValues;
  }


  /**
   * @param tSsdValues the tSsdValues to set
   */
  public void setTSsdValues(final List<TSsdValue> tSsdValues) {
    this.TSsdValues = tSsdValues;
  }
}
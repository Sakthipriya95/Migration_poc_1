package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
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


/**
 * The persistent class for the TABV_ATTRIBUTES database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TabvAttribute.NQ_GET_ALL, query = "select attr from TabvAttribute attr"),
    @NamedQuery(name = TabvAttribute.NQ_GET_PROJ_STRUCT_ATTRS, query = "SELECT attr FROM TabvAttribute attr where attr.attrLevel > 0 "),
    @NamedQuery(name = TabvAttribute.CHECK_EADM_NAME, query = "SELECT attr FROM TabvAttribute attr WHERE attr.eadmName= :eadmName and attr.attrId!= :attrID"),
    @NamedQuery(name = TabvAttribute.GET_UNCLEARED, query = "select distinct attr.attrId from TabvAttribute attr , TabvAttrValue vals where attr=vals.tabvAttribute and vals.clearingStatus not in ('Y','D','R') ") })

@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TabvAttribute.GET_QUO_REL_ATTR_BY_MCR_IDS, query = "                                                            " +
        " select attr.*                                                                                                                      " +
        " from                                                                                                                               " +
        "                 tabv_ucp_attrs uc_attr                                                                                             " +
        "           join  tabv_attributes attr                                                                                               " +
        "           on    (uc_attr.attr_id = attr.attr_id)                                                                                   " +
        " where            uc_attr.mapping_flags = 1                                                                                         " +
        "           and    section_id                                                                                                        " +
        "           in     (select section_id                                                                                                " +
        "                        from   tabv_use_case_sections                                                                               " +
        "                        where  wp_masterlist_id                                                                                     " +
        "                        in     (select id                                                                                           " +
        "                                    from    t_wpml_wp_masterlist                                                                    " +
        "                                    where   mcr_id = ?))                                                                            ", resultClass = TabvAttribute.class) })

@Entity
// Icdm-230
@OptimisticLocking(cascade = true)
@Table(name = "TABV_ATTRIBUTES")
public class TabvAttribute implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to fetch attributes.
   */
  public static final String NQ_GET_ALL = "TabvAttribute.getAll";

  /**
   * Named query to fetch project structure attributes. i.e, attributes with level &gt; 0
   */
  public static final String NQ_GET_PROJ_STRUCT_ATTRS = "TabvAttribute.getProjectStructAttrs";

  /**
   * Named query to check EADM name already exists.
   */
  public static final String CHECK_EADM_NAME = "TabvAttribute.checkEADMName";

  /**
   * Native query to find the atttributes having uncleared values
   */
  public static final String GET_UNCLEARED = "TabvAttribute.unCleared";

  /**
   * Native query to find the Quotation relevant atttributes mapped to usecase section by mcr id
   */
  public static final String GET_QUO_REL_ATTR_BY_MCR_IDS = "TabvAttribute.GET_QUO_REL_ATTR_BY_MCR_IDS";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "ATTR_ID", unique = true, nullable = false, precision = 15)
  private long attrId;

  @Column(name = "ATTR_DESC_ENG", nullable = false, length = 4000)
  private String attrDescEng;

  @Column(name = "ATTR_DESC_GER", length = 4000)
  private String attrDescGer;

  @Column(name = "ATTR_LEVEL", precision = 2)
  private BigDecimal attrLevel;

  @Column(name = "ATTR_NAME_ENG", nullable = false, length = 100)
  private String attrNameEng;

  @Column(name = "ATTR_NAME_GER", length = 100)
  private String attrNameGer;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;
  // Icdm-480 attribute security
  @Column(name = "attr_security", nullable = false, length = 1)
  private String attrSecurity;
  // Icdm-480 attribute value security
  @Column(name = "value_security", nullable = false, length = 1)
  private String valueSecurity;

  @Column(length = 100)
  private String format;

  @Column(nullable = false, length = 1)
  private String mandatory;

  @Column(name = "ADD_VALUES_BY_USERS_FLAG", nullable = false, length = 1)
  private String addValuesByUsersFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "NORMALIZED_FLAG", length = 1)
  private String normalizedFlag;

  @Column(nullable = false, length = 30)
  private String units;

  @Column(name = "MOVE_DOWN_YN", length = 1)
  private String moveDownYN;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "PART_NUMBER_FLAG", length = 1)
  private String partNumberFlag;

  @Column(name = "SPEC_LINK_FLAG", length = 1)
  private String specLinkFlag;

  // bi-directional many-to-one association to TabvAttrGroup
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "GROUP_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrGroup tabvAttrGroup;

  // bi-directional many-to-one association to TabvAttrValueType
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_TYPE_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValueType tabvAttrValueType;

  // bi-directional many-to-one association to TabvAttrDependency
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-230
  @PrivateOwned
  private List<TabvAttrDependency> tabvAttrDependencies;

  // bi-directional many-to-one association to TabvAttrDependency icdm-516
  @OneToMany(mappedBy = "tabvAttributeD")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvAttrDependency> tabvAttrDependenciesD;

  // bi-directional many-to-one association to TabvAttrValue
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-230
  @PrivateOwned
  private List<TabvAttrValue> tabvAttrValues;

  // bi-directional many-to-one association to TabvProjectAttr
  @OneToMany(mappedBy = "tabvAttribute")
  private List<TabvProjectAttr> tabvProjectAttrs;

  // bi-directional many-to-one association to TabvProjSubVariantsAttr
  @OneToMany(mappedBy = "tabvAttribute")
  private List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs;

  // bi-directional many-to-one association to TabvVariantsAttr
  @OneToMany(mappedBy = "tabvAttribute")
  private List<TabvVariantsAttr> tabvVariantsAttrs;

  // bi-directional many-to-one association to TabvUcpAttr
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvUcpAttr> tabvUcpAttrs;


  // bi-directional many-to-one association to TabvPidcDetStructure
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvPidcDetStructure> tabvPidcDetStructures;

  // bi-directional many-to-one association to TCharacteristic
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "CHAR_ID")
  private TCharacteristic tCharacteristic;

  @Column(name = "CHANGE_COMMENT", length = 4000)
  private String changeComment;

  // ICDM-1560
  @Column(name = "EADM_NAME", length = 100, unique = true)
  private String eadmName;

  // bi-directional many-to-one association to TMandatoryAttr
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  private List<TMandatoryAttr> tMandatoryAttrs;

  // bi-directional many-to-one association to TAliasDetail
  @OneToMany(mappedBy = "tabvAttribute")
  private List<TAliasDetail> tAliasDetails;

  @Column(name = "GROUP_ATTR_FLAG", length = 1)
  private String groupFlag;

  // bi-directional many-to-one association to TFocusMatrixVersionAttr
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  private Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs;

  // bi-directional many-to-one association to TQuestionDepenAttribute
  @OneToMany(mappedBy = "tabvAttribute", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TQuestionDepenAttribute> tQuestionDepenAttributes;

  // bi-directional many-to-one association to TSsdFeature
  @OneToMany(mappedBy = "tabvAttribute")
  private List<TSsdFeature> tSsdFeatures;

  /**
   * @return the groupFlag
   */
  public String getGroupFlag() {
    return this.groupFlag;
  }


  /**
   * @param groupFlag the groupFlag to set
   */
  public void setGroupFlag(final String groupFlag) {
    this.groupFlag = groupFlag;
  }


  /**
   * @return the eadmName
   */
  public String getEadmName() {
    return this.eadmName;
  }


  /**
   * @param eadmName the eadmName to set
   */
  public void setEadmName(final String eadmName) {
    this.eadmName = eadmName;
  }


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

  public TabvAttribute() {}

  public long getAttrId() {
    return this.attrId;
  }

  public void setAttrId(final long attrId) {
    this.attrId = attrId;
  }

  public String getAttrDescEng() {
    return this.attrDescEng;
  }

  public void setAttrDescEng(final String attrDescEng) {
    this.attrDescEng = attrDescEng;
  }

  public String getAttrDescGer() {
    return this.attrDescGer;
  }

  public void setAttrDescGer(final String attrDescGer) {
    this.attrDescGer = attrDescGer;
  }

  public BigDecimal getAttrLevel() {
    return this.attrLevel;
  }

  public void setAttrLevel(final BigDecimal attrLevel) {
    this.attrLevel = attrLevel;
  }

  public String getAttrNameEng() {
    return this.attrNameEng;
  }

  public void setAttrNameEng(final String attrNameEng) {
    this.attrNameEng = attrNameEng;
  }

  public String getAttrNameGer() {
    return this.attrNameGer;
  }

  public void setAttrNameGer(final String attrNameGer) {
    this.attrNameGer = attrNameGer;
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

  public String getFormat() {
    return this.format;
  }

  public void setFormat(final String format) {
    this.format = format;
  }

  public String getMandatory() {
    return this.mandatory;
  }

  public void setMandatory(final String mandatory) {
    this.mandatory = mandatory;
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

  public String getNormalizedFlag() {
    return this.normalizedFlag;
  }

  public void setNormalizedFlag(final String normalizedFlag) {
    this.normalizedFlag = normalizedFlag;
  }

  public String getUnits() {
    return this.units;
  }

  public void setUnits(final String units) {
    this.units = units;
  }

  public TabvAttrGroup getTabvAttrGroup() {
    return this.tabvAttrGroup;
  }

  public void setTabvAttrGroup(final TabvAttrGroup tabvAttrGroup) {
    this.tabvAttrGroup = tabvAttrGroup;
  }

  public TabvAttrValueType getTabvAttrValueType() {
    return this.tabvAttrValueType;
  }

  public void setTabvAttrValueType(final TabvAttrValueType tabvAttrValueType) {
    this.tabvAttrValueType = tabvAttrValueType;
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

  public List<TabvAttrValue> getTabvAttrValues() {
    return this.tabvAttrValues;
  }

  public void setTabvAttrValues(final List<TabvAttrValue> tabvAttrValues) {
    this.tabvAttrValues = tabvAttrValues;
  }

  public List<TabvProjectAttr> getTabvProjectAttrs() {
    return this.tabvProjectAttrs;
  }

  public void setTabvProjectAttrs(final List<TabvProjectAttr> tabvProjectAttrs) {
    this.tabvProjectAttrs = tabvProjectAttrs;
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

  public List<TabvUcpAttr> getTabvUcpAttrs() {
    return this.tabvUcpAttrs;
  }

  public void setTabvUcpAttrs(final List<TabvUcpAttr> tabvUcpAttrs) {
    this.tabvUcpAttrs = tabvUcpAttrs;
  }


  public String getPartNumberFlag() {
    return this.partNumberFlag;
  }


  public void setPartNumberFlag(final String partNumberFlag) {
    this.partNumberFlag = partNumberFlag;
  }


  public String getSpecLinkFlag() {
    return this.specLinkFlag;
  }

  public void setSpecLinkFlag(final String specLinkFlag) {
    this.specLinkFlag = specLinkFlag;
  }

  public List<TabvPidcDetStructure> getTabvPidcDetStructures() {
    return this.tabvPidcDetStructures;
  }

  public void setTabvPidcDetStructures(final List<TabvPidcDetStructure> tabvPidcDetStructures) {
    this.tabvPidcDetStructures = tabvPidcDetStructures;
  }

  /**
   * @return the attrSecurity
   */
  public String getAttrSecurity() {
    return this.attrSecurity;
  }

  /**
   * @param attrSecurity the attrSecurity to set
   */
  public void setAttrSecurity(final String attrSecurity) {
    this.attrSecurity = attrSecurity;
  }

  /**
   * @return the valueSecurity
   */
  public String getValueSecurity() {
    return this.valueSecurity;
  }

  /**
   * @param valueSecurity the valueSecurity to set
   */
  public void setValueSecurity(final String valueSecurity) {
    this.valueSecurity = valueSecurity;
  }


  /**
   * @return the tCharacteristic
   */
  public TCharacteristic gettCharacteristic() {
    return this.tCharacteristic;
  }


  /**
   * @param tCharacteristic the tCharacteristic to set
   */
  public void settCharacteristic(final TCharacteristic tCharacteristic) {
    this.tCharacteristic = tCharacteristic;
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

  public void setTAliasDetails(final List<TAliasDetail> TAliasDetails) {
    this.tAliasDetails = TAliasDetails;
  }

  public void addTAliasDetail(final TAliasDetail tAliasDetail) {
    List<TAliasDetail> tAliasDetailList = getTAliasDetails();
    if (tAliasDetailList == null) {
      tAliasDetailList = new ArrayList<>();
      setTAliasDetails(tAliasDetailList);
    }
    tAliasDetailList.add(tAliasDetail);

    tAliasDetail.setTabvAttribute(this);
  }

  public void removeTAliasDetail(final TAliasDetail tAliasDetail) {
    List<TAliasDetail> tAliasDetailList = getTAliasDetails();
    if (tAliasDetailList != null) {
      tAliasDetailList.remove(tAliasDetail);
    }
  }

  /**
   * @return the moveDownYN
   */
  public String getMoveDownYN() {
    return this.moveDownYN;
  }


  /**
   * @param moveDownYN the moveDownYN to set
   */
  public void setMoveDownYN(final String moveDownYN) {
    this.moveDownYN = moveDownYN;
  }

  public Set<TFocusMatrixVersionAttr> getTFocusMatrixVersionAttrs() {
    return this.TFocusMatrixVersionAttrs;
  }

  public void setTFocusMatrixVersionAttrs(final Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs) {
    this.TFocusMatrixVersionAttrs = TFocusMatrixVersionAttrs;
  }

  public TFocusMatrixVersionAttr addTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().add(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTabvAttribute(this);

    return TFocusMatrixVersionAttr;
  }

  public TFocusMatrixVersionAttr removeTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().remove(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTabvAttribute(null);

    return TFocusMatrixVersionAttr;
  }

  /**
   * Gets the t question depen attributes.
   *
   * @return the t question depen attributes
   */
  public Set<TQuestionDepenAttribute> getTQuestionDepenAttributes() {
    return this.tQuestionDepenAttributes;
  }

  public void setTQuestionDepenAttributes(final Set<TQuestionDepenAttribute> tQuestionDepenAttributes) {
    this.tQuestionDepenAttributes = tQuestionDepenAttributes;
  }

  public TQuestionDepenAttribute addTQuestionDepenAttributes(final TQuestionDepenAttribute tQuestionDepenAttribute) {
    getTQuestionDepenAttributes().add(tQuestionDepenAttribute);
    tQuestionDepenAttribute.setTabvAttribute(this);

    return tQuestionDepenAttribute;
  }

  public TQuestionDepenAttribute removeTQuestionDepenAttributes(final TQuestionDepenAttribute tQuestionDepenAttribute) {
    getTQuestionDepenAttributes().remove(tQuestionDepenAttribute);
    return tQuestionDepenAttribute;
  }


  /**
   * @return the tSsdFeatures
   */
  public List<TSsdFeature> getTSsdFeatures() {
    return this.tSsdFeatures;
  }


  /**
   * @param tSsdFeatures the tSsdFeatures to set
   */
  public void setTSsdFeatures(final List<TSsdFeature> tSsdFeatures) {
    this.tSsdFeatures = tSsdFeatures;
  }


  /**
   * @return the addValuesByUsersFlag
   */
  public String getAddValuesByUsersFlag() {
    return this.addValuesByUsersFlag;
  }


  /**
   * @param addValuesByUsersFlag the addValuesByUsersFlag to set
   */
  public void setAddValuesByUsersFlag(final String addValuesByUsersFlag) {
    this.addValuesByUsersFlag = addValuesByUsersFlag;
  }

}
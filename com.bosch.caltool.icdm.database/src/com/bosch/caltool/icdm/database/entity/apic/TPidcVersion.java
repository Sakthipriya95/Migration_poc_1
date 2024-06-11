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

import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVersCocWp;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;


/**
 * The persistent class for the T_PIDC_VERSION database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TPidcVersion.NQ_GET_ALL_PIDC_VERSIONS, query = "SELECT pidcVers FROM TPidcVersion pidcVers"),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_PIDC_NAME, query = "            " +
        "select pidcvers from TPidcVersion pidcvers, GttObjectName temp                     " +
        "where                                                                              " +
        "       pidcvers.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'                  " +
        "   and pidcvers.tabvProjectidcard.proRevId = pidcvers.proRevId                     " +
        "   and pidcvers.tabvProjectidcard.tabvAttrValue.valueId = temp.id"),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_VAR_NAME, query = "             " +
        "select var.tPidcVersion from TabvProjectVariant var , GttObjectName temp           " +
        "where                                                                              " +
        "       var.deletedFlag = 'N'                                                       " +
        "   and var.tPidcVersion.tabvProjectidcard.proRevId = var.tPidcVersion.proRevId     " +
        "   and var.tPidcVersion.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'          " +
        "   and var.tabvAttrValue.valueId = temp.id"),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_SUBVAR_NAME, query = "          " +
        "select svar.tPidcVersion from TabvProjectSubVariant svar , GttObjectName temp      " +
        "where                                                                              " +
        "       svar.deletedFlag = 'N'                                                      " +
        "   and svar.tPidcVersion.tabvProjectidcard.proRevId = svar.tPidcVersion.proRevId   " +
        "   and svar.tPidcVersion.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'         " +
        "   and svar.tabvAttrValue.valueId = temp.id"),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_WITH_VARIANTS, query = "           " +
        "select pidcvers from TPidcVersion pidcvers                                         " +
        "where                                                                              " +
        "       pidcvers.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'                  " +
        "   and pidcvers.tabvProjectidcard.proRevId = pidcvers.proRevId                     " +
        "   and pidcvers in                                                                 " +
        "           (select var.tPidcVersion from TabvProjectVariant var                    " +
        "            where var.deletedFlag = 'N')                                           "),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_WITHOUT_VARIANTS, query = "        " +
        "select pidcvers from TPidcVersion pidcvers                                         " +
        "where                                                                              " +
        "       pidcvers.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'                  " +
        "   and pidcvers.tabvProjectidcard.proRevId = pidcvers.proRevId                     " +
        "   and pidcvers not in                                                             " +
        "           (select var.tPidcVersion from TabvProjectVariant var                    " +
        "            where var.deletedFlag = 'N')                                           "),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_WITH_SUBVARIANTS, query = "        " +
        "select pidcvers from TPidcVersion pidcvers                                         " +
        "where                                                                              " +
        "       pidcVers.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'                  " +
        "   and pidcvers.tabvProjectidcard.proRevId = pidcvers.proRevId                     " +
        "   and pidcvers in                                                                 " +
        "           (select svar.tPidcVersion from TabvProjectSubVariant svar               " +
        "            where svar.deletedFlag = 'N'                                           " +
        "               and svar.tabvProjectVariant.deletedFlag = 'N')                      "),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_WITHOUT_SUBVARIANTS, query = "     " +
        "select pidcvers from TPidcVersion pidcvers                                         " +
        "where                                                                              " +
        "       pidcVers.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'                  " +
        "   and pidcvers.tabvProjectidcard.proRevId = pidcvers.proRevId                     " +
        "   and pidcvers not in                                                             " +
        "           (select svar.tPidcVersion from TabvProjectSubVariant svar               " +
        "            where svar.deletedFlag = 'N'                                           " +
        "               and svar.tabvProjectVariant.deletedFlag = 'N')                      "),
    @NamedQuery(name = TPidcVersion.NQ_GET_PIDC_VERS_BY_APRJ_N_A2L, query = "               " +
        "select pa2l.TPidcVersion.pidcVersId                                                " +
        "from TPidcA2l pa2l, TabvProjectAttr pattr                                          " +
        "where pa2l.TPidcVersion = pattr.tPidcVersion                                       " +
        "  and pa2l.mvTa2lFileinfo.id in :a2lFileId                                         " +
        "  and pattr.used = 'Y'                                                             " +
        "  and pattr.tabvAttrValue.valueId = :aprjValId                                     "),
    @NamedQuery(name = TPidcVersion.NQ_GET_ACTIVE_NON_DELETED_VERSIONS, query = "           " +
        "SELECT pidcVers FROM TPidcVersion pidcVers                                         " +
        "WHERE                                                                              " +
        "       pidcVers.tabvProjectidcard.tabvAttrValue.deletedFlag = 'N'                  " +
        "   and pidcVers.proRevId = pidcVers.tabvProjectidcard.proRevId                     ") })
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TPidcVersion.NNQ_PIDC_VERS_FOR_ATTR_VALUE, query = "           " +
        "select au.username,vrsn.project_id,vrsn.vers_name,vrsn.pidc_vers_id                " +
        "FROM tabv_apic_node_access na , tabv_apic_users au ,                               " +
        " (select distinct(pa.pidc_vers_id) from tabv_project_attr pa                       " +
        "where ( pa.value_id =  ?  ) " + " union " +
        " select distinct(va.pidc_vers_id) from tabv_variants_attr va                       " +
        " where ( va.value_id = ? ) " + " union " +
        " select distinct(sa.pidc_vers_id) from tabv_proj_sub_variants_attr sa              " +
        "where (sa.value_id = ?  )) pidcVrsn, t_pidc_version vrsn" +
        " where vrsn.pidc_vers_id =  pidcVrsn.pidc_vers_id  " +
        " and na.node_id = vrsn.project_id  and na.owner = 'Y' and na.user_id = au.user_id  "),
    @NamedNativeQuery(name = TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_VALUE_IDS, query = "      " +
        "select pvers.PIDC_VERS_ID from t_pidc_version pvers, tabv_projectidcard pidc,      " +
        " TABV_ATTR_VALUES val                                                              " +
        "where pidc.pro_rev_id = pvers.pro_rev_id and pvers.project_id  = pidc.project_id   " +
        "  and pidc.value_id = val.value_id and val.DELETED_FLAG = 'N'                      " +
        "  and pvers.pidc_vers_id in                                                        " +
        "  (                                                                                " +
        "    select PIDC_VERS_ID from tabv_project_attr, GTT_OBJECT_NAMES temp              " +
        "    where value_id = temp.id                                                       " +
        "    union                                                                          " +
        "    select PIDC_VERS_ID from tabv_variants_attr, GTT_OBJECT_NAMES temp             " +
        "    where value_id = temp.id                                                       " +
        "    union                                                                          " +
        "    select PIDC_VERS_ID from TABV_PROJ_SUB_VARIANTS_ATTR, GTT_OBJECT_NAMES temp    " +
        "    where value_id = temp.id                                                       " +
        "  )                                                                                ") })
@Entity
@Table(name = "T_PIDC_VERSION")
@OptimisticLocking(cascade = true)
public class TPidcVersion implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all PIDC Versions
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_ALL_PIDC_VERSIONS = "TPidcVersion.getAllPidcVersions";

  /**
   * Named query to get active pidc versions by pidc name
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_BY_PIDC_NAME = "TPidcVersion.getPidcVersionByPidcName";

  /**
   * Named query to get active pidc versions by value ids
   *
   * @return list of TPidcVersion
   */

  public static final String NQ_GET_PIDC_VERSIONS_BY_VALUE_IDS = "TPidcVersion.getPidcVersionByValueIds";
  /**
   * Named query to get active pidc versions by variant name
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_BY_VAR_NAME = "TPidcVersion.getPidcVersionByVarName";

  /**
   * Named query to get active pidc versions by sub variant name
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_BY_SUBVAR_NAME = "TPidcVersion.getPidcVersionBySubVarName";

  /**
   * Named query to get active pidc versions with variants
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_WITH_VARIANTS = "TPidcVersion.getPidcVersionWithVariants";

  /**
   * Named query to get active pidc versions without variants
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_WITHOUT_VARIANTS = "TPidcVersion.getPidcVersionWithoutVariants";

  /**
   * Named query to get active pidc versions with sub variants
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_WITH_SUBVARIANTS = "TPidcVersion.getPidcVersionWithSubVariants";

  /**
   * Named query to get active pidc versions without sub variants
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_PIDC_VERSIONS_WITHOUT_SUBVARIANTS = "TPidcVersion.getPidcVersionWithoutSubVariants";

  /**
   * Named query to get PIDC Version IDs by APRJ name value id and A2L file id
   *
   * @param aprjValId APRJ Attribute's Value ID
   * @param a2lFileID A2L File ID
   * @return list of pidc version IDs
   */
  public static final String NQ_GET_PIDC_VERS_BY_APRJ_N_A2L = "TPidcVersion.getPidcVersionByAprjAndA2l";

  /**
   * Named query to get all active PIDC Versions from non deleted PIDCs
   *
   * @return list of TPidcVersion
   */
  public static final String NQ_GET_ACTIVE_NON_DELETED_VERSIONS = "TPidcVersion.NQ_GET_ACTIVE_NON_DELETED_VERSIONS";


  /**
   * Get of PIDC names along with owners where the attr value_id is used
   */
  public static final String NNQ_PIDC_VERS_FOR_ATTR_VALUE = "TPidcVersion.getpidcversforattrvalue";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PIDC_VERS_ID", unique = true, nullable = false, precision = 15)
  private long pidcVersId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DELETED_FLAG", length = 1)
  private String deletedFlag;

  @Column(name = "LAST_CONFIRMATION_DATE")
  private Timestamp lastConfirmationDate;


  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "PID_STATUS", length = 1)
  private String pidStatus;

  @Column(name = "PRO_REV_ID", nullable = false, precision = 15)
  private Long proRevId;

  @Column(name = "VERS_DESC_ENG", length = 200)
  private String versDescEng;

  @Column(name = "VERS_DESC_GER", length = 200)
  private String versDescGer;

  @Column(name = "VERS_NAME", nullable = false, length = 100)
  private String versName;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvPidcDetStructure
  @OneToMany(mappedBy = "tPidcVersion")
  @PrivateOwned
  private List<TabvPidcDetStructure> tabvPidcDetStructures;

  // bi-directional many-to-one association to TabvProjectAttr
  @PrivateOwned
  @OneToMany(mappedBy = "tPidcVersion")
  private List<TabvProjectAttr> tabvProjectAttrs;

  // bi-directional many-to-one association to TabvProjectSubVariant
  @OneToMany(mappedBy = "tPidcVersion")
  private List<TabvProjectSubVariant> tabvProjectSubVariants;

  // bi-directional many-to-one association to TabvProjectVariant
  @PrivateOwned
  @OneToMany(mappedBy = "tPidcVersion")
  private List<TabvProjectVariant> tabvProjectVariants;

  // bi-directional many-to-one association to TabvProjSubVariantsAttr
  @OneToMany(mappedBy = "tPidcVersion")
  private List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs;

  // bi-directional many-to-one association to TabvVariantsAttr
  @OneToMany(mappedBy = "tPidcVersion")
  private List<TabvVariantsAttr> tabvVariantsAttrs;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvProjectidcard tabvProjectidcard;

  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARENT_PIDC_VERS_ID")
  // Batch fetch is not required here. Lazy fetch is added, so that parent is queried only when required.
  private TPidcVersion tPidcVers;

  // bi-directional many-to-one association to TPidcVersion
  @OneToMany(mappedBy = "tPidcVers", fetch = FetchType.LAZY)
  // Batch fetch is not required here. Lazy fetch is added, so that parent is queried only when required.
  private List<TPidcVersion> TPidcVersions;

  // bi-directional many-to-one association to TFocusMatrixVersion
  @OneToMany(mappedBy = "tPidcVersion", fetch = FetchType.LAZY)
  @PrivateOwned
  private Set<TFocusMatrixVersion> tFocusMatrixVersions;

  // ICDM-1463
  // bi-directional many-to-one association to TabvPidcA2l
  @OneToMany(mappedBy = "TPidcVersion", fetch = FetchType.LAZY)
  private List<TPidcA2l> tabvPidcA2ls;

  // bi-directional many-to-one association to TPidcRmDefinition
  @PrivateOwned
  @OneToMany(mappedBy = "TPidcVersion")
  private Set<TPidcRmDefinition> tPidcRmDefinitions;

  // bi-directional many-to-one association to TEmrFile
  @PrivateOwned
  @OneToMany(mappedBy = "tPidcVersion")
  private Set<TEmrFile> TEmrFiles;

  // bi-directional many-to-one association to TA2lWorkPackage
  @OneToMany(mappedBy = "pidcVersion", fetch = FetchType.LAZY)
  private List<TA2lWorkPackage> tA2lWorkPackageList;

  // bi-directional many-to-one association to TRvwQnaireRespVariant
  @OneToMany(mappedBy = "tPidcVersion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwQnaireRespVariant> tRvwQnaireRespVariants;

  // bi-directional many-to-one association to TPidcVersCocWP
  @PrivateOwned
  @OneToMany(mappedBy = "tPidcVersion")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TPidcVersCocWp> tPidcVersCocWp;

  public TPidcVersion() {
    // Public constructor
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

  public String getPidStatus() {
    return this.pidStatus;
  }

  public void setPidStatus(final String pidStatus) {
    this.pidStatus = pidStatus;
  }

  public Long getProRevId() {
    return this.proRevId;
  }

  public void setProRevId(final Long proRevId) {
    this.proRevId = proRevId;
  }

  public String getVersDescEng() {
    return this.versDescEng;
  }

  public void setVersDescEng(final String versDescEng) {
    this.versDescEng = versDescEng;
  }

  public String getVersDescGer() {
    return this.versDescGer;
  }

  public void setVersDescGer(final String versDescGer) {
    this.versDescGer = versDescGer;
  }

  public String getVersName() {
    return this.versName;
  }

  public void setVersName(final String versName) {
    this.versName = versName;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TabvPidcDetStructure> getTabvPidcDetStructures() {
    return this.tabvPidcDetStructures;
  }

  public void setTabvPidcDetStructures(final List<TabvPidcDetStructure> tabvPidcDetStructures) {
    this.tabvPidcDetStructures = tabvPidcDetStructures;
  }

  public TabvPidcDetStructure addTabvPidcDetStructure(final TabvPidcDetStructure tabvPidcDetStructure) {
    getTabvPidcDetStructures().add(tabvPidcDetStructure);
    tabvPidcDetStructure.setTPidcVersion(this);

    return tabvPidcDetStructure;
  }

  public TabvPidcDetStructure removeTabvPidcDetStructure(final TabvPidcDetStructure tabvPidcDetStructure) {
    getTabvPidcDetStructures().remove(tabvPidcDetStructure);
    tabvPidcDetStructure.setTPidcVersion(null);

    return tabvPidcDetStructure;
  }


  /**
   * @return the tPidcRmDefinitions
   */
  public Set<TPidcRmDefinition> gettPidcRmDefinitions() {
    return this.tPidcRmDefinitions;
  }


  /**
   * @param tPidcRmDefinitions the tPidcRmDefinitions to set
   */
  public void settPidcRmDefinitions(final Set<TPidcRmDefinition> tPidcRmDefinitions) {
    this.tPidcRmDefinitions = tPidcRmDefinitions;
  }

  public List<TabvProjectAttr> getTabvProjectAttrs() {
    return this.tabvProjectAttrs;
  }

  public void setTabvProjectAttrs(final List<TabvProjectAttr> tabvProjectAttrs) {
    this.tabvProjectAttrs = tabvProjectAttrs;
  }

  public TabvProjectAttr addTabvProjectAttr(final TabvProjectAttr tabvProjectAttr) {
    getTabvProjectAttrs().add(tabvProjectAttr);
    tabvProjectAttr.setTPidcVersion(this);

    return tabvProjectAttr;
  }

  public TabvProjectAttr removeTabvProjectAttr(final TabvProjectAttr tabvProjectAttr) {
    getTabvProjectAttrs().remove(tabvProjectAttr);
    tabvProjectAttr.setTPidcVersion(null);

    return tabvProjectAttr;
  }

  public List<TabvProjectSubVariant> getTabvProjectSubVariants() {
    return this.tabvProjectSubVariants;
  }

  public void setTabvProjectSubVariants(final List<TabvProjectSubVariant> tabvProjectSubVariants) {
    this.tabvProjectSubVariants = tabvProjectSubVariants;
  }

  public TabvProjectSubVariant addTabvProjectSubVariant(final TabvProjectSubVariant tabvProjectSubVariant) {
    getTabvProjectSubVariants().add(tabvProjectSubVariant);
    tabvProjectSubVariant.setTPidcVersion(this);

    return tabvProjectSubVariant;
  }

  public TabvProjectSubVariant removeTabvProjectSubVariant(final TabvProjectSubVariant tabvProjectSubVariant) {
    getTabvProjectSubVariants().remove(tabvProjectSubVariant);
    tabvProjectSubVariant.setTPidcVersion(null);

    return tabvProjectSubVariant;
  }

  public List<TabvProjectVariant> getTabvProjectVariants() {
    return this.tabvProjectVariants;
  }

  public void setTabvProjectVariants(final List<TabvProjectVariant> tabvProjectVariants) {
    this.tabvProjectVariants = tabvProjectVariants;
  }

  public TabvProjectVariant addTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    getTabvProjectVariants().add(tabvProjectVariant);
    tabvProjectVariant.setTPidcVersion(this);

    return tabvProjectVariant;
  }

  public TabvProjectVariant removeTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    getTabvProjectVariants().remove(tabvProjectVariant);
    tabvProjectVariant.setTPidcVersion(null);

    return tabvProjectVariant;
  }

  public List<TabvProjSubVariantsAttr> getTabvProjSubVariantsAttrs() {
    return this.tabvProjSubVariantsAttrs;
  }

  public void setTabvProjSubVariantsAttrs(final List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs) {
    this.tabvProjSubVariantsAttrs = tabvProjSubVariantsAttrs;
  }

  public TabvProjSubVariantsAttr addTabvProjSubVariantsAttr(final TabvProjSubVariantsAttr tabvProjSubVariantsAttr) {
    getTabvProjSubVariantsAttrs().add(tabvProjSubVariantsAttr);
    tabvProjSubVariantsAttr.setTPidcVersion(this);

    return tabvProjSubVariantsAttr;
  }

  public TabvProjSubVariantsAttr removeTabvProjSubVariantsAttr(final TabvProjSubVariantsAttr tabvProjSubVariantsAttr) {
    getTabvProjSubVariantsAttrs().remove(tabvProjSubVariantsAttr);
    tabvProjSubVariantsAttr.setTPidcVersion(null);

    return tabvProjSubVariantsAttr;
  }

  public TabvProjectidcard getTabvProjectidcard() {
    return this.tabvProjectidcard;
  }

  public void setTabvProjectidcard(final TabvProjectidcard tabvProjectidcard) {
    this.tabvProjectidcard = tabvProjectidcard;
  }

  public TPidcVersion getTPidcVers() {
    return this.tPidcVers;
  }

  public void setTPidcVers(final TPidcVersion TPidcVersion) {
    this.tPidcVers = TPidcVersion;
  }

  public List<TPidcVersion> getTPidcVersions() {
    return this.TPidcVersions;
  }

  public void setTPidcVersions(final List<TPidcVersion> TPidcVersions) {
    this.TPidcVersions = TPidcVersions;
  }

  public TPidcVersion addTPidcVersion(final TPidcVersion TPidcVersion) {
    getTPidcVersions().add(TPidcVersion);
    TPidcVersion.setTPidcVers(this);

    return TPidcVersion;
  }

  public TPidcVersion removeTPidcVersion(final TPidcVersion TPidcVersion) {
    getTPidcVersions().remove(TPidcVersion);
    TPidcVersion.setTPidcVers(null);

    return TPidcVersion;
  }

  public List<TabvVariantsAttr> getTabvVariantsAttrs() {
    return this.tabvVariantsAttrs;
  }

  public void setTabvVariantsAttrs(final List<TabvVariantsAttr> tabvVariantsAttrs) {
    this.tabvVariantsAttrs = tabvVariantsAttrs;
  }

  public List<TPidcA2l> getTabvPidcA2ls() {
    return this.tabvPidcA2ls;
  }

  public void setTabvPidcA2ls(final List<TPidcA2l> tabvPidcA2ls) {
    this.tabvPidcA2ls = tabvPidcA2ls;
  }


  public Set<TFocusMatrixVersion> getTFocusMatrixVersions() {
    return this.tFocusMatrixVersions;
  }

  public void setTFocusMatrixVersions(final Set<TFocusMatrixVersion> tFocusMatrixVersions) {
    this.tFocusMatrixVersions = tFocusMatrixVersions;
  }

  public TFocusMatrixVersion addTFocusMatrixVersion(final TFocusMatrixVersion tFocusMatrixVersion) {
    if (this.tFocusMatrixVersions == null) {
      this.tFocusMatrixVersions = new HashSet<>();
    }
    this.tFocusMatrixVersions.add(tFocusMatrixVersion);
    tFocusMatrixVersion.setTPidcVersion(this);

    return tFocusMatrixVersion;
  }

  public TFocusMatrixVersion removeTFocusMatrixVersion(final TFocusMatrixVersion tFocusMatrixVersion) {
    if (this.tFocusMatrixVersions == null) {
      this.tFocusMatrixVersions = new HashSet<>();
    }
    this.tFocusMatrixVersions.remove(tFocusMatrixVersion);
    tFocusMatrixVersion.setTPidcVersion(null);

    return tFocusMatrixVersion;
  }

  /**
   * @param tA2lWorkPackage TA2lWorkPackage instance
   * @return modified object
   */
  public TA2lWorkPackage addTA2lWorkPackage(final TA2lWorkPackage tA2lWorkPackage) {
    if (this.tA2lWorkPackageList == null) {
      this.tA2lWorkPackageList = new ArrayList<>();
    }
    this.tA2lWorkPackageList.add(tA2lWorkPackage);
    tA2lWorkPackage.setPidcVersion(this);
    return tA2lWorkPackage;
  }

  /**
   * @param tA2lWorkPackage TA2lWorkPackage instance
   * @return removed object
   */
  public TA2lWorkPackage removeTA2lWorkPackage(final TA2lWorkPackage tA2lWorkPackage) {
    if (this.tA2lWorkPackageList != null) {
      this.tA2lWorkPackageList.remove(tA2lWorkPackage);
    }
    tA2lWorkPackage.setPidcVersion(null);
    return tA2lWorkPackage;
  }


  public Timestamp getLastConfirmationDate() {
    return this.lastConfirmationDate;
  }

  public void setLastConfirmationDate(final Timestamp lastConfirmationDate) {
    this.lastConfirmationDate = lastConfirmationDate;
  }

  public long getPidcVersId() {
    return this.pidcVersId;
  }

  public void setPidcVersId(final long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  public Set<TEmrFile> getTEmrFiles() {
    return this.TEmrFiles;
  }

  public void setTEmrFiles(final Set<TEmrFile> temrFiles) {
    this.TEmrFiles = temrFiles;
  }

  public TEmrFile addTEmrFile(final TEmrFile temrFile) {
    getTEmrFiles().add(temrFile);
    temrFile.setTPidcVersion(this);

    return temrFile;
  }

  public TEmrFile removeTEmrFile(final TEmrFile temrFile) {
    getTEmrFiles().remove(temrFile);
    temrFile.setTPidcVersion(null);

    return temrFile;
  }

  /**
   * @return the tA2lWorkPackageList
   */
  public List<TA2lWorkPackage> getTA2lWorkPackageList() {
    return this.tA2lWorkPackageList;
  }


  /**
   * @param tA2lWorkPackageList the TA2lWorkPackageList to set
   */
  public void setTA2lWorkPackageList(final List<TA2lWorkPackage> tA2lWorkPackageList) {
    this.tA2lWorkPackageList = tA2lWorkPackageList;
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
   * @param tRvwQnaireRespVariant as input
   * @return tRvwQnaireRespVariant
   */
  public TRvwQnaireRespVariant addTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariant) {
    if (getTRvwQnaireRespVariants() == null) {
      setTRvwQnaireRespVariants(new HashSet<>());
    }
    getTRvwQnaireRespVariants().add(tRvwQnaireRespVariant);
    tRvwQnaireRespVariant.setTPidcVersion(this);

    return tRvwQnaireRespVariant;
  }

  /**
   * @param tRvwQnaireRespVariant as input
   * @return tRvwQnaireRespVariant
   */
  public TRvwQnaireRespVariant removeTRvwQnaireRespVariant(final TRvwQnaireRespVariant tRvwQnaireRespVariant) {
    getTRvwQnaireRespVariants().remove(tRvwQnaireRespVariant);
    return tRvwQnaireRespVariant;
  }


  /**
   * @return the tPidcVersCocWp
   */
  public List<TPidcVersCocWp> gettPidcVersCocWp() {
    return this.tPidcVersCocWp;
  }


  /**
   * @param tPidcVersCocWp the tPidcVersCocWp to set
   */
  public void settPidcVersCocWp(final List<TPidcVersCocWp> tPidcVersCocWp) {
    this.tPidcVersCocWp = tPidcVersCocWp;
  }


}
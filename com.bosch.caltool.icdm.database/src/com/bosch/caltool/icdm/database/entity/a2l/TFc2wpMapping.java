package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
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

import com.bosch.caltool.dmframework.entity.IEntity;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;


/**
 * The persistent class for the T_FC2WP_MAPPING database table.
 */
@Entity
@Table(name = "T_FC2WP_MAPPING")
@NamedQueries(value = {
    @NamedQuery(name = TFc2wpMapping.NQ_FIND_ALL, query = "SELECT t FROM TFc2wpMapping t"),
    @NamedQuery(name = TFc2wpMapping.NQ_GET_FC2WP_MAP_BY_VERS_FOR_TEMP, query = "                            " +
        "SELECT map                                                                         " +
        "FROM TFc2wpMapping map, TFunction fun, GttObjectName temp                          " +
        "WHERE map.TFunction = fun                                                          " +
        "  and fun.upperName = temp.objName                                                 " +
        "  and map.TWorkpackageDivision != null                                             " +
        "  and map.TFc2wpDefVersion.fcwpVerId = :fcwpVerId                                  "),
    @NamedQuery(name = TFc2wpMapping.NQ_GET_DISTINCT_WP_BY_VERS_FOR_TEMP, query = "                            " +
        "SELECT distinct(map.TWorkpackageDivision.tWorkpackage.wpNameE)                     " +
        "FROM TFc2wpMapping map, TFunction fun, GttObjectName temp                          " +
        "WHERE map.TFunction = fun      " +
        "  and map.deleteFlag = 'N'                                                         " +
        "  and fun.upperName = temp.objName                                                 " +
        "  and map.TWorkpackageDivision != null                                             " +
        "  and map.TFc2wpDefVersion.fcwpVerId = :fcwpVerId                                  "),
    @NamedQuery(name = TFc2wpMapping.NQ_GET_FC2WP_MAP_FOR_VERS, query = "SELECT fcwp FROM TFc2wpMapping fcwp where fcwp.TFc2wpDefVersion.fcwpVerId=:fcwpVerId") })
public class TFc2wpMapping implements IEntity, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to fetch all
   */
  public static final String NQ_FIND_ALL = "TFc2wpMapping.findAll";

  /**
   * Query to get the FC2WP mappings from the given version for the functions in temp table
   * <p>
   * Note : uses GttObjectNames temp table
   */
  public static final String NQ_GET_FC2WP_MAP_BY_VERS_FOR_TEMP = "TFc2wpMapping.findByA2lNTemp";

  /**
   * Query to get the workpackage names from FC2WP mappings for the given version & the functions in temp table
   * <p>
   * Note : uses GttObjectNames temp table
   */
  public static final String NQ_GET_DISTINCT_WP_BY_VERS_FOR_TEMP = "TFc2wpMapping.findWpNameByA2lNTemp";


  /**
   * Query to get the FC2WP mappings for the given FC2WP Version
   */
  public static final String NQ_GET_FC2WP_MAP_FOR_VERS = "TFc2wpMapping.findForVersID";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FCWP_MAP_ID", unique = true, nullable = false)
  private long fcwpMapId;

  @Column(name = "AGREED_DATE")
  private Timestamp agreedDate;

  @Column(name = "AGREED_WITH_COC_FLAG", length = 1)
  private String agreedWithCocFlag;

  @Column(length = 4000)
  private String comments;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DELETE_FLAG", length = 1)
  private String deleteFlag;

  @Column(name = "IN_ICDM_A2L_FLAG", length = 1)
  private String inIcdmA2lFlag;

  @Column(name = "IS_FC_IN_SDOM_FLAG", length = 1)
  private String isFcInSdomFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "USE_WP_DEFAULTS_FLAG", length = 1)
  private String useWpDefaultsFlag;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  @Column(name = "FC_WITH_PARAMS_FLAG", length = 1)
  private String fcWithParams;

  @Column(name = "FCWP_INFO", length = 4000)
  private String fcWpInfo;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECOND_CONTACT_USER_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvApicUser tabvApicSecondContactUser;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESP_FOR_AGR_USER_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvApicUser tabvApicRespForAgrUser;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CONTACT_USER_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvApicUser tabvApicContactUser;

  // bi-directional many-to-one association to TBaseComponent
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "BC_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TBaseComponent TBaseComponent;

  // bi-directional many-to-one association to TFc2wpDefVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FCWP_VER_ID", nullable = false)
  private TFc2wpDefVersion TFc2wpDefVersion;

  // bi-directional many-to-one association to TFunction
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUNCTION_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TFunction TFunction;

  // bi-directional many-to-one association to TWorkpackageDivision
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_DIV_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TWorkpackageDivision TWorkpackageDivision;

  // bi-directional many-to-one association to TFcwpMapPtType
  @OneToMany(mappedBy = "TFc2wpMapping", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TFc2wpMapPtType> TFcwpMapPtTypes;

  public TFc2wpMapping() {
    // Public constructor
  }

  public long getFcwpMapId() {
    return this.fcwpMapId;
  }

  public void setFcwpMapId(final long fcwpMapId) {
    this.fcwpMapId = fcwpMapId;
  }

  public Timestamp getAgreedDate() {
    return this.agreedDate;
  }

  public void setAgreedDate(final Timestamp agreedDate) {
    this.agreedDate = agreedDate;
  }

  public String getAgreedWithCocFlag() {
    return this.agreedWithCocFlag;
  }

  public void setAgreedWithCocFlag(final String agreedWithCocFlag) {
    this.agreedWithCocFlag = agreedWithCocFlag;
  }

  public String getComments() {
    return this.comments;
  }

  public void setComments(final String comments) {
    this.comments = comments;
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

  public String getDeleteFlag() {
    return this.deleteFlag;
  }

  public void setDeleteFlag(final String deleteFlag) {
    this.deleteFlag = deleteFlag;
  }

  public String getInIcdmA2lFlag() {
    return this.inIcdmA2lFlag;
  }

  public void setInIcdmA2lFlag(final String inIcdmA2lFlag) {
    this.inIcdmA2lFlag = inIcdmA2lFlag;
  }

  /**
   * @return the isFcInSdomFlag
   */
  public String getIsFcInSdomFlag() {
    return this.isFcInSdomFlag;
  }

  /**
   * @param isFcInSdomFlag the isFcInSdomFlag to set
   */
  public void setIsFcInSdomFlag(final String isFcInSdomFlag) {
    this.isFcInSdomFlag = isFcInSdomFlag;
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

  public String getUseWpDefaultsFlag() {
    return this.useWpDefaultsFlag;
  }

  public void setUseWpDefaultsFlag(final String useWpDefaultsFlag) {
    this.useWpDefaultsFlag = useWpDefaultsFlag;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }


  /**
   * @return the fcWithParams
   */
  public String getFcWithParams() {
    return this.fcWithParams;
  }


  /**
   * @param fcWithParams the fcWithParams to set
   */
  public void setFcWithParams(final String fcWithParams) {
    this.fcWithParams = fcWithParams;
  }


  /**
   * @return the fcWpInfo
   */
  public String getFcWpInfo() {
    return this.fcWpInfo;
  }


  /**
   * @param fcWpInfo the fcWpInfo to set
   */
  public void setFcWpInfo(final String fcWpInfo) {
    this.fcWpInfo = fcWpInfo;
  }

  public TabvApicUser getTabvApicSecondContactUser() {
    return this.tabvApicSecondContactUser;
  }

  public void setTabvApicSecondContactUser(final TabvApicUser tabvApicUser) {
    this.tabvApicSecondContactUser = tabvApicUser;
  }

  public TabvApicUser getTabvApicRespForAgrUser() {
    return this.tabvApicRespForAgrUser;
  }

  public void setTabvApicRespForAgrUser(final TabvApicUser tabvApicUser) {
    this.tabvApicRespForAgrUser = tabvApicUser;
  }

  public TabvApicUser getTabvApicContactUser() {
    return this.tabvApicContactUser;
  }

  public void setTabvApicContactUser(final TabvApicUser tabvApicUser) {
    this.tabvApicContactUser = tabvApicUser;
  }

  public TBaseComponent getTBaseComponent() {
    return this.TBaseComponent;
  }

  public void setTBaseComponent(final TBaseComponent tBaseComponent) {
    this.TBaseComponent = tBaseComponent;
  }

  public TFc2wpDefVersion getTFc2wpDefVersion() {
    return this.TFc2wpDefVersion;
  }

  public void setTFc2wpDefVersion(final TFc2wpDefVersion tFc2wpDefVersion) {
    this.TFc2wpDefVersion = tFc2wpDefVersion;
  }

  public TFunction getTFunction() {
    return this.TFunction;
  }

  public void setTFunction(final TFunction tFunction) {
    this.TFunction = tFunction;
  }

  public TWorkpackageDivision getTWorkpackageDivision() {
    return this.TWorkpackageDivision;
  }

  public void setTWorkpackageDivision(final TWorkpackageDivision tWorkpackageDivision) {
    this.TWorkpackageDivision = tWorkpackageDivision;
  }

  public Set<TFc2wpMapPtType> getTFc2wpMapPtTypes() {
    return this.TFcwpMapPtTypes;
  }

  public void setTFc2wpMapPtTypes(final Set<TFc2wpMapPtType> tFcwpMapPtTypes) {
    this.TFcwpMapPtTypes = tFcwpMapPtTypes;
  }

  public TFc2wpMapPtType addTFc2wpMapPtType(final TFc2wpMapPtType tFcwpMapPtType) {
    if (this.TFcwpMapPtTypes == null) {
      this.TFcwpMapPtTypes = new HashSet<>();
    }
    getTFc2wpMapPtTypes().add(tFcwpMapPtType);
    tFcwpMapPtType.setTFc2wpMapping(this);

    return tFcwpMapPtType;
  }

  public TFc2wpMapPtType removeTFc2wpMapPtType(final TFc2wpMapPtType tFcwpMapPtType) {
    if (this.TFcwpMapPtTypes == null) {
      this.TFcwpMapPtTypes = new HashSet<>();
    }
    getTFc2wpMapPtTypes().remove(tFcwpMapPtType);
    tFcwpMapPtType.setTFc2wpMapping(null);

    return tFcwpMapPtType;
  }

}
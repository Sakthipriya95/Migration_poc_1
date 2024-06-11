package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
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

import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelivery;


/**
 * The persistent class for the TABV_PIDC_A2L database table.
 */
@Entity
@Table(name = "T_PIDC_A2L")
@OptimisticLocking(cascade = true)
@NamedQueries(value = {
    @NamedQuery(name = TPidcA2l.GET_ALL, query = "SELECT t FROM TPidcA2l t"),
    @NamedQuery(name = TPidcA2l.GET_ALL_BY_PIDCVERS_ID, query = "SELECT t FROM TPidcA2l t where t.TPidcVersion.pidcVersId = :pidcVersId and t.sdomPverName = :sdomPverName"),
    @NamedQuery(name = TPidcA2l.GET_PIDC_A2L, query = "Select t FROM TPidcA2l t where t.mvTa2lFileinfo.id =:a2lFileId and t.tabvProjectidcard.projectId=:projectId") })
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TPidcA2l.GET_RELEVANT_A2L_FILES, query = "                                                     " +
        "      SELECT                                                                                                                                   " +
        "      a2l.SDOM_PVER_NAME,                                                                                                                      " +
        "      a2lInfo.SDOM_PVER_VARIANT ,                                                                                                              " +
        "      nvl(a2l.VCDM_A2L_NAME,a2lInfo.FILENAME) as a2lFileName ,                                                                                 " +
        "      a2l.CREATED_DATE as Assigned_Date,                                                                                                       " +
        "      a2l.PIDC_VERS_ID,                                                                                                                        " +
        "      vers.VERS_NAME,                                                                                                                          " +
        "      nvl(a2l.VCDM_A2L_DATE,a2lInfo.FILEDATE) as  Created_Date,                                                                                " +
        "      a2l.A2L_FILE_ID,                                                                                                                         " +
        "      a2l.PIDC_A2L_ID                                                                                                                      " +
        "      FROM T_PIDC_A2L a2l , TA2L_FILEINFO a2lInfo , T_PIDC_VERSION vers                                                                        " +
        "      where  exists ( select 1 from T_RVW_RESULTS review where a2l.PIDC_A2L_ID = review.pidc_a2l_id) and a2lInfo.id= a2l.a2l_file_id           " +
        "      and vers.PIDC_VERS_ID=a2l.PIDC_VERS_ID and a2l.project_id = ?                                                                            "), })
public class TPidcA2l implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Named query to fetch relevant a2l files belonging to pidc which was one used in review
   *
   * @return list of TPidcA2l entities
   */
  public static final String GET_RELEVANT_A2L_FILES = "TPidcA2l.RelevantA2lFiles";

  public static final String GET_ALL = "TPidcA2l.GET_ALL";

  /**
   *
   */
  public static final String GET_ALL_BY_PIDCVERS_ID = "TPidcA2l.GetAllByPidcVerSdom";

  /**
   * get the pidc a2l from a2l file id and project id.
   */
  public static final String GET_PIDC_A2L = "TPidcA2l.GET_PIDC_A2L";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PIDC_A2L_ID", unique = true, nullable = false, precision = 15)
  private long pidcA2lId;

  // ICDM-1591
  // Note : This entity relationship is added for performance improvement, to support batch fetch of a2l files
  // Relationship does not exist in database, since MvTa2lFileinfo is a view.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "A2L_FILE_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private MvTa2lFileinfo mvTa2lFileinfo;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private TabvProjectidcard tabvProjectidcard;

  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne
  @JoinColumn(name = "PIDC_VERS_ID", nullable = true)
  private TPidcVersion TPidcVersion;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @Column(name = "SDOM_PVER_NAME", nullable = false, length = 50)
  private String sdomPverName;

  @Column(name = "SSD_SOFTWARE_VERSION")
  private String ssdSoftwareVersion;

  @Column(name = "SSD_SOFTWARE_VERSION_ID")
  private Long ssdSoftwareVersionID;

  @Column(name = "SSD_SOFTWARE_PROJ_ID")
  private Long ssdSoftwareProjID;


  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "ASSIGNED_DATE")
  private Timestamp assignedDate;

  @Column(name = "ASSIGNED_USER", length = 30)
  private String assignedUser;

  @Column(name = "VCDM_A2L_DATE")
  private Timestamp vcdmA2lDate;


  /**
   * @return the assignedDate
   */
  public Timestamp getAssignedDate() {
    return this.assignedDate;
  }


  /**
   * @param assignedDate the assignedDate to set
   */
  public void setAssignedDate(final Timestamp assignedDate) {
    this.assignedDate = assignedDate;
  }


  /**
   * @return the assignedUser
   */
  public String getAssignedUser() {
    return this.assignedUser;
  }


  /**
   * @param assignedUser the assignedUser to set
   */
  public void setAssignedUser(final String assignedUser) {
    this.assignedUser = assignedUser;
  }


  @Column(name = "VCDM_A2L_NAME")
  private String vcdmA2lName;

  // bi-directional many-to-one association to TA2lResp
  @OneToMany(mappedBy = "TPidcA2l")
  private List<TA2lResp> tA2lResps;

  // bi-directional many-to-one association to TRvwVariant
  @OneToMany(mappedBy = "TPidcA2l", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwResult> TRvwResults;

  // bi-directional many-to-one association to TA2lWpDefinitionVersion
  @OneToMany(mappedBy = "tPidcA2l", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lWpDefnVersion> tA2lWpDefnVersions;


  // bi-directional many-to-one association to TCDFxDelivery
  @OneToMany(mappedBy = "pidcA2l", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCDFxDelivery> tCdfxDeliveryList;

  @Column(name = "IS_ACTIVE")
  private String isActive;

  @Column(name = "WP_PARAM_PRESENT_FLAG")
  private String wpParamPresentFlag;

  @Column(name = "ACTIVE_WP_PARAM_PRESENT_FLAG")
  private String activeWpParamPresentFlag;

  @Column(name = "IS_WORKING_SET_MODIFIED", nullable = false, length = 1)
  private String isWorkingSetModifiedFlag;


  /**
   * @return the isWorkingSetModifiedFlag
   */
  public String getIsWorkingSetModifiedFlag() {
    return this.isWorkingSetModifiedFlag;
  }


  /**
   * @param isWorkingSetModifiedFlag the isWorkingSetModifiedFlag to set
   */
  public void setIsWorkingSetModifiedFlag(final String isWorkingSetModifiedFlag) {
    this.isWorkingSetModifiedFlag = isWorkingSetModifiedFlag;
  }

  public TPidcA2l() {}

  public long getPidcA2lId() {
    return this.pidcA2lId;
  }

  public void setPidcA2lId(final long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  public MvTa2lFileinfo getMvTa2lFileinfo() {
    return this.mvTa2lFileinfo;
  }

  public void setMvTa2lFileinfo(final MvTa2lFileinfo mvTa2lFileinfo) {
    this.mvTa2lFileinfo = mvTa2lFileinfo;
  }

  public TabvProjectidcard getTabvProjectidcard() {
    return this.tabvProjectidcard;
  }

  public void setTabvProjectidcard(final TabvProjectidcard tabvProjectidcard) {
    this.tabvProjectidcard = tabvProjectidcard;
  }

  public TPidcVersion getTPidcVersion() {
    return this.TPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion TPidcVersion) {
    this.TPidcVersion = TPidcVersion;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getSdomPverName() {
    return this.sdomPverName;
  }

  public void setSdomPverName(final String sdomPverName) {
    this.sdomPverName = sdomPverName;
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

  public Timestamp getVcdmA2lDate() {
    return this.vcdmA2lDate;
  }

  public void setVcdmA2lDate(final Timestamp vcdmA2lDate) {
    this.vcdmA2lDate = vcdmA2lDate;
  }

  public String getVcdmA2lName() {
    return this.vcdmA2lName;
  }

  public void setVcdmA2lName(final String vcdmA2lName) {
    this.vcdmA2lName = vcdmA2lName;
  }

  public List<TA2lResp> getTA2lResps() {
    return this.tA2lResps;
  }

  public void setTA2lResps(final List<TA2lResp> TA2lResps) {
    this.tA2lResps = TA2lResps;
  }


  /**
   * @return the ssdSoftwareVersion
   */
  public String getSsdSoftwareVersion() {
    return this.ssdSoftwareVersion;
  }


  /**
   * @param ssdSoftwareVersion the ssdSoftwareVersion to set
   */
  public void setSsdSoftwareVersion(final String ssdSoftwareVersion) {
    this.ssdSoftwareVersion = ssdSoftwareVersion;
  }


  /**
   * @return the ssdSoftwareVersionID
   */
  public Long getSsdSoftwareVersionID() {
    return this.ssdSoftwareVersionID;
  }


  /**
   * @param ssdSoftwareVersionID the ssdSoftwareVersionID to set
   */
  public void setSsdSoftwareVersionID(final Long ssdSoftwareVersionID) {
    this.ssdSoftwareVersionID = ssdSoftwareVersionID;
  }


  /**
   * @return the ssdSoftwareProjID
   */
  public Long getSsdSoftwareProjID() {
    return this.ssdSoftwareProjID;
  }


  /**
   * @param ssdSoftwareProjID the ssdSoftwareProjID to set
   */
  public void setSsdSoftwareProjID(final Long ssdSoftwareProjID) {
    this.ssdSoftwareProjID = ssdSoftwareProjID;
  }

  /**
   * @return the tRvwResults
   */
  public Set<TRvwResult> getTRvwResults() {
    return this.TRvwResults;
  }

  /**
   * @param tRvwResults the tRvwResults to set
   */
  public void setTRvwResults(final Set<TRvwResult> tRvwResults) {
    this.TRvwResults = tRvwResults;
  }


  /**
   * @return the tA2lWpDefnVersions
   */
  public List<TA2lWpDefnVersion> gettA2lWpDefnVersions() {
    return this.tA2lWpDefnVersions;
  }


  /**
   * @param tA2lWpDefnVersions the tA2lWpDefnVersions to set
   */
  public void settA2lWpDefnVersions(final List<TA2lWpDefnVersion> tA2lWpDefnVersions) {
    this.tA2lWpDefnVersions = tA2lWpDefnVersions;
  }


  /**
   * @return the isActive
   */
  public String getIsActive() {
    return this.isActive;
  }


  /**
   * @param isActive the isActive to set
   */
  public void setIsActive(final String isActive) {
    this.isActive = isActive;
  }


  /**
   * @return the wpParamPresentFlag
   */
  public String getWpParamPresentFlag() {
    return this.wpParamPresentFlag;
  }


  /**
   * @param wpParamPresentFlag the wpParamPresentFlag to set
   */
  public void setWpParamPresentFlag(final String wpParamPresentFlag) {
    this.wpParamPresentFlag = wpParamPresentFlag;
  }


  /**
   * @return the activeWpParamPresentFlag
   */
  public String getActiveWpParamPresentFlag() {
    return this.activeWpParamPresentFlag;
  }


  /**
   * @param activeWpParamPresentFlag the activeWpParamPresentFlag to set
   */
  public void setActiveWpParamPresentFlag(final String activeWpParamPresentFlag) {
    this.activeWpParamPresentFlag = activeWpParamPresentFlag;
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


}
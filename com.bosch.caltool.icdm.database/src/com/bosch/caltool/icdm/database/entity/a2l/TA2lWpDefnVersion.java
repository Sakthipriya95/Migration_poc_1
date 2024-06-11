package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelivery;


/**
 * The persistent class for the T_A2L_WP_DEFN_VERSIONS database table.
 */
@Entity
@Table(name = "T_A2L_WP_DEFN_VERSIONS")

@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TA2lWpDefnVersion.NNS_INSERT_A2L_WPDETAILS, query = "               " +
        " {call PK_A2L_WP_DETAILS_COPY.InsertA2lWpDetails (?1, ?2, ?3) } "),
    @NamedNativeQuery(name = TA2lWpDefnVersion.PRC_INSERT_WP_FINISHED_STATUS, query = "               " +
        " {call PK_STORE_WP_FIN_STATUS.PRC_INSERT_WP_FIN_STATUS (?1, ?2) } "),
    @NamedNativeQuery(name = TA2lWpDefnVersion.PRC_UPDATE_A2L_FINISHED, query = "call PRC_UPDATE_A2L_FINISHED (?1, ?2)") })

public class TA2lWpDefnVersion implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {


  private static final long serialVersionUID = 1L;

  public static final String NNS_INSERT_A2L_WPDETAILS = "NNS_InsertA2lWpDetails";

  /**
   * Procedure call to update finished status of WP -Resp of new WP Def Active version from old Active version
   */
  public static final String PRC_UPDATE_A2L_FINISHED = "updateA2lWpDefStatus";

  /**
   * Procedure call to insert finished status of WP -Resp of new WP Def Active version from old Active version
   */
  public static final String PRC_INSERT_WP_FINISHED_STATUS = "insertA2lWpRespStatus";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_DEFN_VERS_ID")
  private long wpDefnVersId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "IS_ACTIVE")
  private String isActive;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PARAM_LVL_CHG_ALLOWD_FLAG")
  private String paramLevelChgAllowedFlag;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  @Column(name = "VERSION_DESC")
  private String versionDesc;

  @Column(name = "VERSION_NAME")
  private String versionName;

  @Column(name = "VERSION_NUMBER")
  private Long versionNumber;

  @Column(name = "ANYTIME_ACTIVE_FLAG")
  private String isAnytimeActiveFlag;

  @Column(name = "VCDM_PVD_ID")
  private Long vcdmPvdId;

  @Column(name = "VCDM_PRD_ID")
  private Long vcdmPrdId;

  // bi-directional many-to-one association to TCDFxDelivery
  @OneToMany(mappedBy = "wpDefnVersion", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCDFxDelivery> tCdfxDeliveryList;

  // bi-directional many-to-one association to TA2lVariantGroup
  @OneToMany(mappedBy = "tA2lWpDefnVersion", fetch = FetchType.LAZY)
  private Set<TA2lVariantGroup> tA2lVariantGroups;

  // bi-directional many-to-one association to TPidcA2l
  @ManyToOne
  @JoinColumn(name = "PIDC_A2L_ID")
  private TPidcA2l tPidcA2l;

  // bi-directional many-to-one association to TA2lWpResponsibility
  @OneToMany(mappedBy = "tA2lWpDefnVersion", fetch = FetchType.LAZY)
  private List<TA2lWpResponsibility> tA2lWpResponsibility;

  // bi-directional many-to-one association to TRvwResult
  @OneToMany(mappedBy = "tA2lWpDefnVersion", fetch = FetchType.LAZY)
  private List<TRvwResult> tRvwResults;

  public TA2lWpDefnVersion() {
    // cmt
  }

  public long getWpDefnVersId() {
    return this.wpDefnVersId;
  }

  public void setWpDefnVersId(final long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
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

  public String getIsActive() {
    return this.isActive;
  }

  public void setIsActive(final String isActive) {
    this.isActive = isActive;
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


  /**
   * @return the paramLevelChgAllowedFlag
   */
  public String getParamLevelChgAllowedFlag() {
    return this.paramLevelChgAllowedFlag;
  }


  /**
   * @param paramLevelChgAllowedFlag the paramLevelChgAllowedFlag to set
   */
  public void setParamLevelChgAllowedFlag(final String paramLevelChgAllowedFlag) {
    this.paramLevelChgAllowedFlag = paramLevelChgAllowedFlag;
  }

  public String getVersionDesc() {
    return this.versionDesc;
  }

  public void setVersionDesc(final String versionDesc) {
    this.versionDesc = versionDesc;
  }

  public String getVersionName() {
    return this.versionName;
  }

  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }


  /**
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }


  /**
   * @return the versionNumber
   */
  public Long getVersionNumber() {
    return this.versionNumber;
  }


  /**
   * @param versionNumber the versionNumber to set
   */
  public void setVersionNumber(final Long versionNumber) {
    this.versionNumber = versionNumber;
  }

  public Set<TA2lVariantGroup> getTA2lVariantGroups() {
    return this.tA2lVariantGroups;
  }

  public void setTA2lVariantGroups(final Set<TA2lVariantGroup> tA2lVariantGroups) {
    this.tA2lVariantGroups = tA2lVariantGroups;
  }

  public TA2lVariantGroup addTA2lVariantGroup(final TA2lVariantGroup tA2lVariantGroup) {
    if (getTA2lVariantGroups() == null) {
      setTA2lVariantGroups(new HashSet<TA2lVariantGroup>());
    }
    getTA2lVariantGroups().add(tA2lVariantGroup);
    tA2lVariantGroup.settA2lWpDefnVersion(this);

    return tA2lVariantGroup;
  }

  public TA2lVariantGroup removeTA2lVariantGroup(final TA2lVariantGroup tA2lVariantGroup) {
    if (getTA2lVariantGroups() != null) {
      getTA2lVariantGroups().remove(tA2lVariantGroup);
    }


    return tA2lVariantGroup;
  }

  public TPidcA2l getTPidcA2l() {
    return this.tPidcA2l;
  }

  public void setTPidcA2l(final TPidcA2l tPidcA2l) {
    this.tPidcA2l = tPidcA2l;
  }

  public List<TA2lWpResponsibility> getTA2lWpResponsibility() {
    return this.tA2lWpResponsibility;
  }

  public void setTA2lWpResponsibility(final List<TA2lWpResponsibility> tA2lWpResponsibility) {
    this.tA2lWpResponsibility = tA2lWpResponsibility;
  }

  public TA2lWpResponsibility addTA2lWpResponsibility(final TA2lWpResponsibility tA2lWpResponsibility) {
    getTA2lWpResponsibility().add(tA2lWpResponsibility);
    tA2lWpResponsibility.settA2lWpDefnVersion(this);

    return tA2lWpResponsibility;
  }

  public TA2lWpResponsibility removeTA2lWpResponsibility(final TA2lWpResponsibility tA2lWpResponsibility) {
    getTA2lWpResponsibility().remove(tA2lWpResponsibility);
    tA2lWpResponsibility.settA2lWpDefnVersion(null);

    return tA2lWpResponsibility;
  }

  public List<TRvwResult> getTRvwResults() {
    return this.tRvwResults;
  }

  public void setTRvwResults(final List<TRvwResult> tRvwResults) {
    this.tRvwResults = tRvwResults;
  }

  public TRvwResult addTRvwResult(final TRvwResult tRvwResult) {
    getTRvwResults().add(tRvwResult);
    tRvwResult.settA2lWpDefnVersion(this);

    return tRvwResult;
  }

  public TRvwResult removeTRvwResult(final TRvwResult tRvwResult) {
    getTRvwResults().remove(tRvwResult);
    tRvwResult.settA2lWpDefnVersion(null);

    return tRvwResult;
  }

  /**
   * @return the isAnytimeActiveFlag
   */
  public String getIsAnytimeActiveFlag() {
    return this.isAnytimeActiveFlag;
  }

  /**
   * @param isAnytimeActiveFlag the isAnytimeActiveFlag to set
   */
  public void setIsAnytimeActiveFlag(final String isAnytimeActiveFlag) {
    this.isAnytimeActiveFlag = isAnytimeActiveFlag;
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
   * @return the vcdmPvdId
   */
  public Long getVcdmPvdId() {
    return this.vcdmPvdId;
  }


  /**
   * @param vcdmPvdId the vcdmPvdId to set
   */
  public void setVcdmPvdId(final Long vcdmPvdId) {
    this.vcdmPvdId = vcdmPvdId;
  }


  /**
   * @return the vcdmPrdId
   */
  public Long getVcdmPrdId() {
    return this.vcdmPrdId;
  }


  /**
   * @param vcdmPrdId the vcdmPrdId to set
   */
  public void setVcdmPrdId(final Long vcdmPrdId) {
    this.vcdmPrdId = vcdmPrdId;
  }

}
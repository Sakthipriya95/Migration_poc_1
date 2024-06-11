package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;


/**
 * The persistent class for the T_A2L_WP_RESPONSIBILITY_STATUS database table.
 */
@Entity
@Table(name = "T_A2L_WP_RESPONSIBILITY_STATUS")
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS_WITH_VARID_WPRESPID, query = " SELECT A2L_WP_RESP_STATUS_ID FROM T_A2L_WP_RESPONSIBILITY_STATUS                                  " +
        "                                                                                                           WHERE                                                                                    " +
        "                                                                                                                   WP_RESP_ID = ?                                                                   " +
        "                                                                                                               AND VARIANT_ID = ?                                                                   " +
        "                                                                                                               AND A2L_RESP_ID IS NULL                                                              "),
    @NamedNativeQuery(name = TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS_WITH_VARID_RESPID_WPRESPID, query = " SELECT A2L_WP_RESP_STATUS_ID FROM T_A2L_WP_RESPONSIBILITY_STATUS                           " +
        "                                                                                                           WHERE                                                                                    " +
        "                                                                                                                   WP_RESP_ID = ?                                                                   " +
        "                                                                                                               AND VARIANT_ID = ?                                                                   " +
        "                                                                                                               AND A2L_RESP_ID = ?                                                                  "),
    @NamedNativeQuery(name = TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS_WITH_WPRESPID_FOR_NO_VARIANT, query = " SELECT A2L_WP_RESP_STATUS_ID FROM T_A2L_WP_RESPONSIBILITY_STATUS                         " +
        "                                                                                                           WHERE                                                                                    " +
        "                                                                                                                   WP_RESP_ID = ?                                                                   " +
        "                                                                                                               AND A2L_RESP_ID = ?                                                                  " +
        "                                                                                                               AND VARIANT_ID IS NULL                                                               "),
    @NamedNativeQuery(name = TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS, query = " SELECT A2L_WP_RESP_STATUS_ID FROM T_A2L_WP_RESPONSIBILITY_STATUS                                                      " +
        "                                                                                                           WHERE                                                                                    " +
        "                                                                                                                   WP_RESP_ID = ?                                                                   " +
        "                                                                                                               AND VARIANT_ID IS NULL                                                               " +
        "                                                                                                               AND A2L_RESP_ID IS NULL                                                               ") })
public class TA2lWpResponsibilityStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Query to get A2L WP Repsonsibilty Status by Passing Variant Id and WPRespId
   */
  public static final String GET_A2LWPRESPSTATUS_WITH_VARID_WPRESPID =
      "TA2lWpResponsibilityStatus.getA2lWPRespStatuswithvarIdWpRespId";
  /**
   * Query to get A2L WP Repsonsibilty Status by Passing WPRespId for No Variant
   */
  public static final String GET_A2LWPRESPSTATUS_WITH_WPRESPID_FOR_NO_VARIANT =
      "TA2lWpResponsibilityStatus.getA2lWPRespStatusForNoVariant";

  public static final String GET_A2LWPRESPSTATUS = "TA2lWpResponsibilityStatus.getA2lWPRespStatus";

  public static final String GET_A2LWPRESPSTATUS_WITH_VARID_RESPID_WPRESPID =
      "TA2lWpResponsibilityStatus.getA2lWPRespStatusForVarIdRespIdWpRespId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_WP_RESP_STATUS_ID")
  private long a2lWpRespStatusId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne
  @JoinColumn(name = "VARIANT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvProjectVariant tabvProjVariant;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @Column(name = "WP_RESP_FIN_STATUS")
  private String wpRespFinStatus;

  // bi-directional many-to-one association to TA2lWpResponsibility
  @ManyToOne
  @JoinColumn(name = "WP_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lWpResponsibility tA2lWPResp;

  // bi-directional many-to-one association to TA2lResponsibility
  @ManyToOne
  @JoinColumn(name = "A2L_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lResponsibility tA2lResp;

  public TA2lWpResponsibilityStatus() {
    // NA
  }

  public long getA2lWpRespStatusId() {
    return this.a2lWpRespStatusId;
  }

  public void setA2lWpRespStatusId(final long a2lWpRespStatusId) {
    this.a2lWpRespStatusId = a2lWpRespStatusId;
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

  public TabvProjectVariant getTabvProjVar() {
    return this.tabvProjVariant;
  }

  public void setTabvProjVar(final TabvProjectVariant tabvProjVariant) {
    this.tabvProjVariant = tabvProjVariant;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getWpRespFinStatus() {
    return this.wpRespFinStatus;
  }

  public void setWpRespFinStatus(final String wpRespFinStatus) {
    this.wpRespFinStatus = wpRespFinStatus;
  }

  public TA2lWpResponsibility getTA2lWpResp() {
    return this.tA2lWPResp;
  }

  public void setTA2lWpResp(final TA2lWpResponsibility tA2lWpResp) {
    this.tA2lWPResp = tA2lWpResp;
  }


  /**
   * @return the tA2lResp
   */
  public TA2lResponsibility gettA2lResp() {
    return this.tA2lResp;
  }


  /**
   * @param tA2lResp the tA2lResp to set
   */
  public void settA2lResp(final TA2lResponsibility tA2lResp) {
    this.tA2lResp = tA2lResp;
  }

}

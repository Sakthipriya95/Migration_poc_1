package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;

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
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.cdr.TParameter;


/**
 * The persistent class for the T_A2L_WP_PARAM_MAPPING database table.
 */
@Entity
@Table(name = "T_A2L_WP_PARAM_MAPPING")

@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TA2lWpParamMapping.NNS_INS_TABLE_A2L_WP_DEFAULT_MAPPING, query = "               " +
        "INSERT into T_A2L_WP_PARAM_MAPPING (PARAM_ID, WP_RESP_ID, CREATED_USER)                              " +
        "    SELECT                                                                                           " +
        "        param.id as PARAM_ID,                                                                        " +
        "        ? as WP_RESP_ID,                                                                             " +
        "        ? as CREATED_USER                                                                            " +
        "    FROM                                                                                             " +
        "        T_PARAMETER param,                                                                           " +
        "        TA2L_FILEINFO a2lfile,                                                                       " +
        "        TA2L_MODULES a2lmodule,                                                                      " +
        "        TA2L_CHARACTERISTICS a2lparam                                                                " +
        "    WHERE                                                                                            " +
        "            a2lfile.id          = a2lmodule.FILE_ID                                                  " +
        "        AND a2lmodule.MODULE_ID = a2lparam.MODULE_ID                                                 " +
        "        AND a2lparam.name       = param.name                                                         " +
        "        AND a2lparam.DTYPE      = param.PTYPE                                                        " +
        "        AND a2lfile.id = ?                                                                           "), })
@NamedQueries(value = {
    @NamedQuery(name = TA2lWpParamMapping.GET_WP_PARAM_MAPPING_FOR_WP_DEFN_VERS, query = "select t from TA2lWpParamMapping t where t.tA2lWpResponsibility.tA2lWpDefnVersion.wpDefnVersId = :wpDefnVersId"),
    @NamedQuery(name = TA2lWpParamMapping.GET_WP_PARAM_MAPPING_FOR_WP_DEFN_VERS_INHERIT_FLAG, query = "select t from TA2lWpParamMapping t where t.tA2lWpResponsibility.tA2lWpDefnVersion.wpDefnVersId = :wpDefnVersId and t.wpRespInheritedFlag = :wpRespInheritedFlag") })
public class TA2lWpParamMapping implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Named Native statement to insert parameters of an default wp mapping for A2L parameters
   */
  public static final String NNS_INS_TABLE_A2L_WP_DEFAULT_MAPPING =
      "TA2lWpParamMapping.InsertDefaultWpA2LParamsMapping";

  /**
   * Named query to get wp param mapping for a2lWpRespId
   */
  public static final String GET_WP_PARAM_MAPPING_FOR_WP_DEFN_VERS =
      "TA2lWpParamMapping.GetWpParamMappingForWpDefnVers";

  /**
   * Named query to get wp param mapping for a2lWpRespId
   */
  public static final String GET_WP_PARAM_MAPPING_FOR_WP_DEFN_VERS_INHERIT_FLAG =
      "TA2lWpParamMapping.GetWpParamMappingForWpDefnVersInheritFlag";

  /** The wp param mapping id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_PARAM_MAP_ID")
  private long wpParamMappingId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;


  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The wp name cust. */
  @Column(name = "WP_NAME_CUST")
  private String wpNameCust;

  /** The wp resp inherited flag. */
  @Column(name = "WP_RESP_INHERIT_FLAG")
  private String wpRespInheritedFlag;

  /** The wp name inherited flag. */
  @Column(name = "WP_NAME_CUST_INHERIT_FLAG")
  private String wpNameCustInheritedFlag;


  /** The version. */
  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  // bi-directional many-to-one association to TA2lWpResponsibility
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_RESP_ID")
  private TA2lWpResponsibility tA2lWpResponsibility;

  // bi-directional many-to-one association to TParameter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TParameter tParameter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PAR_A2L_RESP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lResponsibility tA2lResponsibility;

  /**
   * Gets the wp param mapping id.
   *
   * @return the wp param mapping id
   */
  public long getWpParamMappingId() {
    return this.wpParamMappingId;
  }

  /**
   * Sets the wp param mapping id.
   *
   * @param wpParamMappingId the new wp param mapping id
   */
  public void setWpParamMappingId(final long wpParamMappingId) {
    this.wpParamMappingId = wpParamMappingId;
  }

  /**
   * {@inheritDoc}
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * Sets the created date.
   *
   * @param createdDate the new created date
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * Sets the created user.
   *
   * @param createdUser the new created user
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * {@inheritDoc}
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * Sets the modified date.
   *
   * @param modifiedDate the new modified date
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * Sets the modified user.
   *
   * @param modifiedUser the new modified user
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * Gets the version.
   *
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }

  /**
   * Sets the version.
   *
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }

  /**
   * Gets the wp name cust.
   *
   * @return the wpNameCust
   */
  public String getWpNameCust() {
    return this.wpNameCust;
  }


  /**
   * Sets the wp name cust.
   *
   * @param wpNameCust the wpNameCust to set
   */
  public void setWpNameCust(final String wpNameCust) {
    this.wpNameCust = wpNameCust;
  }

  /**
   * @return the tA2lWpResponsibility
   */
  public TA2lWpResponsibility getTA2lWpResponsibility() {
    return this.tA2lWpResponsibility;
  }


  /**
   * @param tA2lWpResponsibility the tA2lWpResponsibility to set
   */
  public void setTA2lWpResponsibility(final TA2lWpResponsibility tA2lWpResponsibility) {
    this.tA2lWpResponsibility = tA2lWpResponsibility;
  }

  /**
   * Gets the t parameter.
   *
   * @return the t parameter
   */
  public TParameter getTParameter() {
    return this.tParameter;
  }

  /**
   * Sets the t parameter.
   *
   * @param tParameter the new t parameter
   */
  public void setTParameter(final TParameter tParameter) {
    this.tParameter = tParameter;
  }

  /**
   * @return the wpRespInheritedFlag
   */
  public String getWpRespInheritedFlag() {
    return this.wpRespInheritedFlag;
  }

  /**
   * @param wpRespInheritedFlag the wpRespInheritedFlag to set
   */
  public void setWpRespInheritedFlag(final String wpRespInheritedFlag) {
    this.wpRespInheritedFlag = wpRespInheritedFlag;
  }


  /**
   * @return the wpNameCustInheritedFlag
   */
  public String getWpNameCustInheritedFlag() {
    return this.wpNameCustInheritedFlag;
  }


  /**
   * @param wpNameCustInheritedFlag the wpNameCustInheritedFlag to set
   */
  public void setWpNameCustInheritedFlag(final String wpNameCustInheritedFlag) {
    this.wpNameCustInheritedFlag = wpNameCustInheritedFlag;
  }


  /**
   * @return the tA2lResponsibility
   */
  public TA2lResponsibility getTA2lResponsibility() {
    return this.tA2lResponsibility;
  }


  /**
   * @param tA2lResponsibility the tA2lResponsibility to set
   */
  public void setTA2lResponsibility(final TA2lResponsibility tA2lResponsibility) {
    this.tA2lResponsibility = tA2lResponsibility;
  }


}
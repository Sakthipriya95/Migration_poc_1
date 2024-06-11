package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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


/**
 * The persistent class for the T_A2L_WP_RESPONSIBILITY database table.
 */
@Entity
@Table(name = "T_A2L_WP_RESPONSIBILITY")
@NamedQueries(value = {
    @NamedQuery(name = TA2lWpResponsibility.GET_MAPPINGS_FROM_A2L, query = "select t from TA2lWpResponsibility t where t.tA2lWpDefnVersion.wpDefnVersId = :wpdefid"),
    @NamedQuery(name = TA2lWpResponsibility.GET_BY_A2L_WP_RESP_ID, query = "SELECT t FROM TA2lWpResponsibility t where t.a2lResponsibility.a2lRespId = :a2lRespId and t.a2lWp.a2lWpId = :a2lWpId and t.tA2lWpDefnVersion.wpDefnVersId = :wpDefnVersId"),
    @NamedQuery(name = TA2lWpResponsibility.GET_BY_A2L_RESP_ID, query = "SELECT t FROM TA2lWpResponsibility t where t.a2lResponsibility.a2lRespId = :a2lRespId and t.tA2lWpDefnVersion.wpDefnVersId = :wpDefnVersId") })
public class TA2lWpResponsibility implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  private static final long serialVersionUID = 1L;

  /**
   * GET all query
   */
  public static final String GET_ALL = "TA2lWpResponsibility.getAll";

  /**
   *
   */
  public static final String GET_MAPPINGS_FROM_A2L = "TA2lWpResponsibility.importMappingsFromA2lGrps";

  /**
  *
  */
  public static final String GET_BY_A2L_WP_RESP_ID = "TA2lWpResponsibility.findByA2LWPAndRespId";
  /**
  *
  */
  public static final String GET_BY_A2L_RESP_ID = "TA2lWpResponsibility.findByA2LRespId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_RESP_ID")
  private long wpRespId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "A2L_VAR_GRP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TA2lVariantGroup variantGroup;

  @ManyToOne
  @JoinColumn(name = "A2L_RESP_ID")
  private TA2lResponsibility a2lResponsibility;

  @ManyToOne
  @JoinColumn(name = "A2L_WP_ID")
  private TA2lWorkPackage a2lWp;


  // bi-directional many-to-one association to TA2lWpParamMapping
  @OneToMany(mappedBy = "tA2lWpResponsibility", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lWpParamMapping> tA2lWpParamMappings;

  // bi-directional many-to-one association to TA2lWpDefinitionVersion
  @ManyToOne
  @JoinColumn(name = "WP_DEFN_VERS_ID")
  private TA2lWpDefnVersion tA2lWpDefnVersion;

  // bi-directional one-to-many association to TA2lWpResponsibilityStatus
  @OneToMany(mappedBy = "tA2lWPResp", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lWpResponsibilityStatus> tA2lWPRespStatus;


  public TA2lWpResponsibility() {

    // cmt
  }

  public long getWpRespId() {
    return this.wpRespId;
  }

  public void setWpRespId(final long wpRespId) {
    this.wpRespId = wpRespId;
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

  public List<TA2lWpParamMapping> getTA2lWpParamMappings() {
    return this.tA2lWpParamMappings;
  }

  public void setTA2lWpParamMappings(final List<TA2lWpParamMapping> tA2lWpParamMappings) {
    this.tA2lWpParamMappings = tA2lWpParamMappings;
  }

  /**
   * Adds the TA 2 l wp param mapping.
   *
   * @param tA2lWpParamMapping the t A 2 l wp param mapping
   * @return the TA 2 l wp param mapping
   */
  public TA2lWpParamMapping addTA2lWpParamMapping(final TA2lWpParamMapping tA2lWpParamMapping) {
    if (getTA2lWpParamMappings() == null) {
      this.tA2lWpParamMappings = new ArrayList<>();
    }
    getTA2lWpParamMappings().add(tA2lWpParamMapping);
    tA2lWpParamMapping.setTA2lWpResponsibility(this);

    return tA2lWpParamMapping;
  }

  /**
   * Removes the TA 2 l wp param mapping.
   *
   * @param tA2lWpParamMapping the t A 2 l wp param mapping
   * @return the TA 2 l wp param mapping
   */
  public TA2lWpParamMapping removeTA2lWpParamMapping(final TA2lWpParamMapping tA2lWpParamMapping) {
    if ((getTA2lWpParamMappings() != null) && (tA2lWpParamMapping != null)) {
      getTA2lWpParamMappings().remove(tA2lWpParamMapping);
    }
    return tA2lWpParamMapping;
  }


  /**
   * @return the tA2lWpDefnVersion
   */
  public TA2lWpDefnVersion gettA2lWpDefnVersion() {
    return this.tA2lWpDefnVersion;
  }


  /**
   * @param tA2lWpDefnVersion the tA2lWpDefnVersion to set
   */
  public void settA2lWpDefnVersion(final TA2lWpDefnVersion tA2lWpDefnVersion) {
    this.tA2lWpDefnVersion = tA2lWpDefnVersion;
  }

  /**
   * @return the variantGroup
   */
  public TA2lVariantGroup getVariantGroup() {
    return this.variantGroup;
  }


  /**
   * @param variantGroup the variantGroup to set
   */
  public void setVariantGroup(final TA2lVariantGroup variantGroup) {
    this.variantGroup = variantGroup;
  }


  /**
   * @return the a2lResponsibility
   */
  public TA2lResponsibility getA2lResponsibility() {
    return this.a2lResponsibility;
  }


  /**
   * @param a2lResponsibility the a2lResponsibility to set
   */
  public void setA2lResponsibility(final TA2lResponsibility a2lResponsibility) {
    this.a2lResponsibility = a2lResponsibility;
  }


  /**
   * @return the a2lWp
   */
  public TA2lWorkPackage getA2lWp() {
    return this.a2lWp;
  }


  /**
   * @param a2lWp the a2lWp to set
   */
  public void setA2lWp(final TA2lWorkPackage a2lWp) {
    this.a2lWp = a2lWp;
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
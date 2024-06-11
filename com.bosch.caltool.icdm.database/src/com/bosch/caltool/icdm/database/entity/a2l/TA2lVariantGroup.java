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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * The persistent class for the T_A2L_VARIANT_GROUP database table.
 */
@Entity
@Table(name = "T_A2L_VARIANT_GROUPS")


public class TA2lVariantGroup implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  private static final long serialVersionUID = 1L;


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_VAR_GRP_ID")
  private long a2lVarGrpId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "GROUP_DESC")
  private String groupDesc;

  @Column(name = "GROUP_NAME")
  private String groupName;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  // bi-directional many-to-one association to TA2lWpDefnVersion
  @ManyToOne
  @JoinColumn(name = "WP_DEFN_VERS_ID")
  private TA2lWpDefnVersion tA2lWpDefnVersion;

  // bi-directional many-to-one association to TA2lVarGrpVarMapping
  @OneToMany(mappedBy = "tA2lVariantGroup", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lVarGrpVariantMapping> tA2lVarGrpVariantMappings;

  // bi-directional many-to-one association to TA2lWpParamMapping
  @OneToMany(mappedBy = "variantGroup", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lWpResponsibility> tA2lWpRespList;

  public TA2lVariantGroup() {
    //
  }


  /**
   * @return the a2lVarGrpId
   */
  public long getA2lVarGrpId() {
    return this.a2lVarGrpId;
  }


  /**
   * @param a2lVarGrpId the a2lVarGrpId to set
   */
  public void setA2lVarGrpId(final long a2lVarGrpId) {
    this.a2lVarGrpId = a2lVarGrpId;
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
   * @return the groupDesc
   */
  public String getGroupDesc() {
    return this.groupDesc;
  }


  /**
   * @param groupDesc the groupDesc to set
   */
  public void setGroupDesc(final String groupDesc) {
    this.groupDesc = groupDesc;
  }


  /**
   * @return the groupName
   */
  public String getGroupName() {
    return this.groupName;
  }


  /**
   * @param groupName the groupName to set
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
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
   * @return the tA2lVarGrpVariantMappings
   */
  public List<TA2lVarGrpVariantMapping> getTA2lVarGrpVariantMappings() {
    return this.tA2lVarGrpVariantMappings;
  }


  /**
   * @param tA2lVarGrpVariantMappings the tA2lVarGrpVariantMappings to set
   */
  public void setTA2lVarGrpVariantMappings(final List<TA2lVarGrpVariantMapping> tA2lVarGrpVariantMappings) {
    this.tA2lVarGrpVariantMappings = tA2lVarGrpVariantMappings;
  }


  /**
   * @param tA2lVarGrpVariantMapping the tA2lVarGrpVariantMapping to add
   * @return tA2lVarGrpVariantMapping
   */
  public TA2lVarGrpVariantMapping addTA2lVarGrpVarMapping(final TA2lVarGrpVariantMapping tA2lVarGrpVariantMapping) {
    if (getTA2lVarGrpVariantMappings() == null) {
      setTA2lVarGrpVariantMappings(new ArrayList<>());
    }
    getTA2lVarGrpVariantMappings().add(tA2lVarGrpVariantMapping);
    tA2lVarGrpVariantMapping.setTA2lVariantGroup(this);

    return tA2lVarGrpVariantMapping;
  }

  /**
   * @param tA2lVarGrpVariantMapping the tA2lVarGrpVariantMapping to remove
   * @return tA2lVarGrpVariantMapping
   */
  public TA2lVarGrpVariantMapping removeTA2lVarGrpVarMapping(final TA2lVarGrpVariantMapping tA2lVarGrpVariantMapping) {
    if (getTA2lVarGrpVariantMappings() != null) {
      getTA2lVarGrpVariantMappings().remove(tA2lVarGrpVariantMapping);
    }

    return tA2lVarGrpVariantMapping;
  }


  /**
   * @return the tA2lWpRespList
   */
  public List<TA2lWpResponsibility> gettA2lWpRespList() {
    return this.tA2lWpRespList;
  }


  /**
   * @param tA2lWpRespList the tA2lWpRespList to set
   */
  public void settA2lWpRespList(final List<TA2lWpResponsibility> tA2lWpRespList) {
    this.tA2lWpRespList = tA2lWpRespList;
  }


}
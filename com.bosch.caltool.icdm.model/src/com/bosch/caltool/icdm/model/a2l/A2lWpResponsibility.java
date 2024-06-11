package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2lWpResponsibility Model class
 *
 * @author pdh2cob
 */
public class A2lWpResponsibility implements Cloneable, Comparable<A2lWpResponsibility>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 219215373493284L;
  /**
   * Wp Resp Id
   */
  private Long id;
  /**
   * Wp Defn Vers Id
   */
  private Long wpDefnVersId;

  /**
   * ID of A2lWp info object
   */
  private Long a2lWpId;

  /**
   * Wp Name
   */
  private String name;
  /**
   * Wp Desc
   */
  private String description;

  /**
   * WP Name at customer
   */
  private String wpNameCust;

  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;
  /**
   * Variant Group Id
   */
  private Long variantGrpId;
  /**
   * Resp id from current pidc
   */
  private Long a2lRespId;

  /**
   * mappedWpRespName used in filtering via outline page
   */
  private String mappedWpRespName;
  /**
   * used to identify the matching wp resp based on variant group
   */
  private String variantGrpName;


  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }


  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return wpDefnVersId
   */
  public Long getWpDefnVersId() {
    return this.wpDefnVersId;
  }

  /**
   * @param wpDefnVersId set wpDefnVersId
   */
  public void setWpDefnVersId(final Long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }


  /**
   * @return the wpNameCust
   */
  public String getWpNameCust() {
    return this.wpNameCust;
  }


  /**
   * @param wpNameCust the wpNameCust to set
   */
  public void setWpNameCust(final String wpNameCust) {
    this.wpNameCust = wpNameCust;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A2lWpResponsibility clone() {
    A2lWpResponsibility wpResp = null;
    try {
      wpResp = (A2lWpResponsibility) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return wpResp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWpResponsibility object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWpResponsibility) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;

  }


  /**
   * @return the variantGrpId
   */
  public Long getVariantGrpId() {
    return this.variantGrpId;
  }


  /**
   * @param variantGrpId the variantGrpId to set
   */
  public void setVariantGrpId(final Long variantGrpId) {
    this.variantGrpId = variantGrpId;
  }


  /**
   * @return the mappedWpRespName
   */
  public String getMappedWpRespName() {
    return this.mappedWpRespName;
  }


  /**
   * @param mappedWpRespName the mappedWpRespName to set
   */
  public void setMappedWpRespName(final String mappedWpRespName) {
    this.mappedWpRespName = mappedWpRespName;
  }


  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }


  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  /**
   * @return the variantGrpName
   */
  public String getVariantGrpName() {
    return this.variantGrpName;
  }


  /**
   * @param variantGrpName the variantGrpName to set
   */
  public void setVariantGrpName(final String variantGrpName) {
    this.variantGrpName = variantGrpName;
  }


}

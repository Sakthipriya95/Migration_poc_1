package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2LWPResponsibilityStatus Model class
 *
 * @author UKT1COB
 */
public class A2lWpResponsibilityStatus implements Cloneable, Comparable<A2lWpResponsibilityStatus>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 441576596320176L;
  /**
   * A2l Wp Resp Status Id
   */
  private Long id;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * Wp Resp Id
   */
  private Long wpRespId;
  /**
   * Wp Resp Fin Status
   */
  private String wpRespFinStatus;
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
   * Wp Name
   */
  private String name;
  /**
   * Wp Desc
   */
  private String description;
  /**
   * mappedWpRespName used in Popup message to let user know about the status change
   */
  private String mappedWpRespName;
  /**
   * A2lWpId is needed for find the matching wpResponse
   */
  private Long a2lWpId;
  /**
   * A2lRespId is needed for find the matching wpResponse
   */
  private Long a2lRespId;
  /**
   * inheritedFlag is false if the responsible is customized responsible
   */
  private boolean inheritedFlag;

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
   * @return variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }

  /**
   * @param variantId set variantId
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }

  /**
   * @return wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }

  /**
   * @param wpRespId set wpRespId
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
  }

  /**
   * @return wpRespFinStatus
   */
  public String getWpRespFinStatus() {
    return this.wpRespFinStatus;
  }

  /**
   * @param wpRespFinStatus set wpRespFinStatus
   */
  public void setWpRespFinStatus(final String wpRespFinStatus) {
    this.wpRespFinStatus = wpRespFinStatus;
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
  public A2lWpResponsibilityStatus clone() {
    A2lWpResponsibilityStatus a2lWPResp = null;
    try {
      a2lWPResp = (A2lWpResponsibilityStatus) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return a2lWPResp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWpResponsibilityStatus object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWpResponsibilityStatus) obj).getId());
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
   * @return the mappedWpRespName
   */
  public String getMappedWpRespName() {
    return mappedWpRespName;
  }

  /**
   * @param mappedWpRespName the mappedWpRespName to set
   */
  public void setMappedWpRespName(String mappedWpRespName) {
    this.mappedWpRespName = mappedWpRespName;
  }

  
  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return a2lWpId;
  }

  
  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }

  
  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return a2lRespId;
  }

  
  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  
  /**
   * @return the inheritedFlag
   */
  public boolean isInheritedFlag() {
    return inheritedFlag;
  }

  
  /**
   * @param inheritedFlag the inheritedFlag to set
   */
  public void setInheritedFlag(boolean inheritedFlag) {
    this.inheritedFlag = inheritedFlag;
  }

}

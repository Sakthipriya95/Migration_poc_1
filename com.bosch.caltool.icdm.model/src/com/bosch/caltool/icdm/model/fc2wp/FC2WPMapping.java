/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.fc2wp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bne4cob
 */
public class FC2WPMapping implements IDataObject, Comparable<FC2WPMapping>, Cloneable {

  /**
   *
   */
  private static final long serialVersionUID = -6124510543034131483L;

  private Long id;

  private String name;
  private String description;
  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;

  private Long fcwpVerId;

  private Long functionId;
  private String functionName;
  private String functionDesc;

  private boolean agreeWithCoc;
  private Date agreeWithCocDate;
  private Long agreeWithCocRespUserId;

  private Long bcID;

  private Set<Long> ptTypeSet = new HashSet<>();

  private Long wpDivId;

  private boolean useWpDef;
  private Long contactPersonId;
  private Long contactPersonSecondId;

  private String comments;

  private boolean usedInIcdm;

  private boolean isFcInSdom;

  private boolean deleted;

  private Long version;

  private boolean fcWithParams;

  private String fc2wpInfo;


  /**
   * @return the agreeWithCocRespUserId
   */
  public Long getAgreeWithCocRespUserId() {
    return this.agreeWithCocRespUserId;
  }


  /**
   * @param agreeWithCocRespUserId the agreeWithCocRespUserId to set
   */
  public void setAgreeWithCocRespUserId(final Long agreeWithCocRespUserId) {
    this.agreeWithCocRespUserId = agreeWithCocRespUserId;
  }


  /**
   * @return the contactPersonId
   */
  public Long getContactPersonId() {
    return this.contactPersonId;
  }


  /**
   * @param contactPersonId the contactPersonId to set
   */
  public void setContactPersonId(final Long contactPersonId) {
    this.contactPersonId = contactPersonId;
  }


  /**
   * @return the contactPersonSecondId
   */
  public Long getContactPersonSecondId() {
    return this.contactPersonSecondId;
  }


  /**
   * @param contactPersonSecondId the contactPersonSecondId to set
   */
  public void setContactPersonSecondId(final Long contactPersonSecondId) {
    this.contactPersonSecondId = contactPersonSecondId;
  }

  /**
   * @return the fcwpVerId
   */
  public Long getFcwpVerId() {
    return this.fcwpVerId;
  }

  /**
   * @param fcwpVerId the fcwpVerId to set
   */
  public void setFcwpVerId(final Long fcwpVerId) {
    this.fcwpVerId = fcwpVerId;
  }

  /**
   * @return the functionId
   */
  public Long getFunctionId() {
    return this.functionId;
  }

  /**
   * @param functionId the functionId to set
   */
  public void setFunctionId(final Long functionId) {
    this.functionId = functionId;
  }

  /**
   * @return the functionName
   */
  public String getFunctionName() {
    return this.functionName;
  }

  /**
   * @return the functionDesc
   */
  public String getFunctionDesc() {
    return this.functionDesc;
  }

  /**
   * @param functionDesc the functionDesc to set
   */
  public void setFunctionDesc(final String functionDesc) {
    this.functionDesc = functionDesc;
  }

  /**
   * @param functionName the functionName to set
   */
  public void setFunctionName(final String functionName) {
    this.functionName = functionName;
  }

  /**
   * @return the agreeWithCoc
   */
  public boolean isAgreeWithCoc() {
    return this.agreeWithCoc;
  }

  /**
   * @param agreeWithCoc the agreeWithCoc to set
   */
  public void setAgreeWithCoc(final boolean agreeWithCoc) {
    this.agreeWithCoc = agreeWithCoc;
  }

  /**
   * @return the agreeWithCocDate
   */
  public Date getAgreeWithCocDate() {
    return this.agreeWithCocDate;
  }

  /**
   * @param agreeWithCocDate the agreeWithCocDate to set
   */
  public void setAgreeWithCocDate(final Date agreeWithCocDate) {
    this.agreeWithCocDate = agreeWithCocDate;
  }

  /**
   * @return the bcID
   */
  public Long getBcID() {
    return this.bcID;
  }

  /**
   * @param bcID the bcID to set
   */
  public void setBcID(final Long bcID) {
    this.bcID = bcID;
  }

  /**
   * @return the useWpDef
   */
  public boolean isUseWpDef() {
    return this.useWpDef;
  }

  /**
   * @param useWpDef the useWpDef to set
   */
  public void setUseWpDef(final boolean useWpDef) {
    this.useWpDef = useWpDef;
  }

  /**
   * @return the comments
   */
  public String getComments() {
    return this.comments;
  }

  /**
   * @param comments the comments to set
   */
  public void setComments(final String comments) {
    this.comments = comments;
  }

  /**
   * @return the usedInIcdm
   */
  public boolean isUsedInIcdm() {
    return this.usedInIcdm;
  }

  /**
   * @param usedInIcdm the usedInIcdm to set
   */
  public void setUsedInIcdm(final boolean usedInIcdm) {
    this.usedInIcdm = usedInIcdm;
  }

  /**
   * @return the isFcInSdom
   */
  public boolean isFcInSdom() {
    return this.isFcInSdom;
  }


  /**
   * @param isFcInSdom the isFcInSdom to set
   */
  public void setFcInSdom(final boolean isFcInSdom) {
    this.isFcInSdom = isFcInSdom;
  }


  /**
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }

  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * @return the wpDivId
   */
  public Long getWpDivId() {
    return this.wpDivId;
  }


  /**
   * @param wpDivId the wpDivId to set
   */
  public void setWpDivId(final Long wpDivId) {
    this.wpDivId = wpDivId;
  }

  /**
   * @return the ptTypeSet
   */
  public Set<Long> getPtTypeSet() {
    return this.ptTypeSet;
  }

  /**
   * @param ptTypeSet the ptTypeSet to set
   */
  public void setPtTypeSet(final Set<Long> ptTypeSet) {
    if (ptTypeSet != null) {
      this.ptTypeSet = new HashSet<>(ptTypeSet);
    }
  }


  /**
   * @param fcwpMapId the fcwpMapId to set
   */
  @Override
  public void setId(final Long fcwpMapId) {
    this.id = fcwpMapId;
  }

  /**
   * @return the fcwpMapId
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the fcWithParams
   */
  public boolean isFcWithParams() {
    return this.fcWithParams;
  }


  /**
   * @param fcWithParams the fcWithParams to set
   */
  public void setFcWithParams(final boolean fcWithParams) {
    this.fcWithParams = fcWithParams;
  }


  /**
   * @return the fc2wpInfo
   */
  public String getFc2wpInfo() {
    return this.fc2wpInfo;
  }


  /**
   * @param fc2wpInfo the fc2wpInfo to set
   */
  public void setFc2wpInfo(final String fc2wpInfo) {
    this.fc2wpInfo = fc2wpInfo;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "FC2WPMapping [fcwpMapId=" + this.id + ", functionName=" + this.functionName + ", functionDesc=" +
        this.functionDesc + ", agreeWithCoc=" + this.agreeWithCoc + ", agreeWithCocDate=" + this.agreeWithCocDate +
        ", agreeWithCocRespUserId=" + this.agreeWithCocRespUserId + ", bcID=" + this.bcID + ", ptTypeSet=" +
        this.ptTypeSet + ", wpDivId=" + this.wpDivId + ", useWpDef=" + this.useWpDef + ", contactPersonId=" +
        this.contactPersonId + ", contactPersonSecondId=" + this.contactPersonSecondId + ", comments=" + this.comments +
        ", usedInIcdm=" + this.usedInIcdm + ", deleted=" + this.deleted + ", version=" + this.version +
        ", fcWithParams=" + this.fcWithParams + ", fc2wpInfo=" + this.fc2wpInfo + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FC2WPMapping clone() throws CloneNotSupportedException {
    FC2WPMapping clon = (FC2WPMapping) super.clone();
    clon.setPtTypeSet(new HashSet<>(getPtTypeSet()));
    return clon;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FC2WPMapping oth) {
    int ret = ModelUtil.compare(this.name, oth.name);
    return ret == 0 ? ModelUtil.compare(this.id, oth.id) : ret;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((FC2WPMapping) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((FC2WPMapping) obj).getName());
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}

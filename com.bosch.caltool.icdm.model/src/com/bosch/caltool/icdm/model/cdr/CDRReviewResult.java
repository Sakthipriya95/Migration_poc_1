/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Review Result Model class
 *
 * @author BRU2COB
 */
public class CDRReviewResult implements Cloneable, Comparable<CDRReviewResult>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 8176681277033524714L;

  /**
   * Result Id
   */
  private Long id;
  /**
   * Grp Work Pkg
   */
  private String grpWorkPkg;
  /**
   * Rvw Status
   */
  private String rvwStatus;
  /**
   * Org Result Id
   */
  private Long orgResultId;
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
   * Description
   */
  private String description;
  /**
   * Source Type
   */
  private String sourceType;
  /**
   * Review Type
   */
  private String reviewType;
  /**
   * Rset Id
   */
  private Long rsetId;
  /**
   * Pidc A2l Id
   */
  private Long pidcA2lId;
  /**
   * Lock Status
   */
  private String lockStatus;

  /**
   * Delta Review Type
   */
  private String deltaReviewType;
  /**
   * Wp Div Id
   */
  private Long wpDivId;
  /**
   * Sdom pver variant name of a2l file
   */
  private String sdomPverVarName;
  /**
   * Pidc variant Id
   */
  private Long primaryVariantId;
  /**
   * Pidc variant Name
   */
  private String primaryVariantName;

  /**
   * Review result name
   */
  private String name;

  /**
   * Pidc version id
   */
  private Long pidcVersionId;

  /**
   * A2l Workpackage Definition Version id
   */
  private Long wpDefnVersId;

  private String comments;
  /**
   * O - only OBD Gen Qnaire, N - only Simplified Qnaire, B - Both OBD and Simplified Qnaire
   */
  private String obdFlag;
  /**
   * Y - Simplified Qnaire response - I confirm , N - Simplified Qnaire response - I do not confirm, {{null}} - No response
   */
  private String simpQuesRespValue;

  /**
   * Simplified Qnaire response remarks
   */
  private String simpQuesRemarks;


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
   * @return the wpDefnVersId
   */
  public Long getWpDefnVersId() {
    return this.wpDefnVersId;
  }


  /**
   * @param wpDefnVersId the wpDefnVersId to set
   */
  public void setWpDefnVersId(final Long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
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
   * @return grpWorkPkg
   */
  public String getGrpWorkPkg() {
    return this.grpWorkPkg;
  }

  /**
   * @param grpWorkPkg set grpWorkPkg
   */
  public void setGrpWorkPkg(final String grpWorkPkg) {
    this.grpWorkPkg = grpWorkPkg;
  }


  /**
   * @return rvwStatus
   */
  public String getRvwStatus() {
    return this.rvwStatus;
  }

  /**
   * @param rvwStatus set rvwStatus
   */
  public void setRvwStatus(final String rvwStatus) {
    this.rvwStatus = rvwStatus;
  }

  /**
   * @return orgResultId
   */
  public Long getOrgResultId() {
    return this.orgResultId;
  }

  /**
   * @param orgResultId set orgResultId
   */
  public void setOrgResultId(final Long orgResultId) {
    this.orgResultId = orgResultId;
  }

  /**
   * @return createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
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
   * @return description
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description set description
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return sourceType
   */
  public String getSourceType() {
    return this.sourceType;
  }

  /**
   * @param sourceType set sourceType
   */
  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * @return reviewType
   */
  public String getReviewType() {
    return this.reviewType;
  }

  /**
   * @param reviewType set reviewType
   */
  public void setReviewType(final String reviewType) {
    this.reviewType = reviewType;
  }

  /**
   * @return rsetId
   */
  public Long getRsetId() {
    return this.rsetId;
  }

  /**
   * @param rsetId set rsetId
   */
  public void setRsetId(final Long rsetId) {
    this.rsetId = rsetId;
  }

  /**
   * @return pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId set pidcA2lId
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return lockStatus
   */
  public String getLockStatus() {
    return this.lockStatus;
  }

  /**
   * @param lockStatus set lockStatus
   */
  public void setLockStatus(final String lockStatus) {
    this.lockStatus = lockStatus;
  }


  /**
   * @return deltaReviewType
   */
  public String getDeltaReviewType() {
    return this.deltaReviewType;
  }

  /**
   * @param deltaReviewType set deltaReviewType
   */
  public void setDeltaReviewType(final String deltaReviewType) {
    this.deltaReviewType = deltaReviewType;
  }

  /**
   * @return wpDivId
   */
  public Long getWpDivId() {
    return this.wpDivId;
  }

  /**
   * @param wpDivId set wpDivId
   */
  public void setWpDivId(final Long wpDivId) {
    this.wpDivId = wpDivId;
  }

  /**
   * @return the sdomPverVarName
   */
  public String getSdomPverVarName() {
    return this.sdomPverVarName;
  }

  /**
   * @param sdomPverVarName the sdomPverVarName to set
   */
  public void setSdomPverVarName(final String sdomPverVarName) {
    this.sdomPverVarName = sdomPverVarName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRReviewResult clone() {
    CDRReviewResult result = null;
    try {
      result = (CDRReviewResult) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRReviewResult object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((CDRReviewResult) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return the variantId
   */
  public Long getPrimaryVariantId() {
    return this.primaryVariantId;
  }

  /**
   * @param variantId the variantId to set
   */
  public void setPrimaryVariantId(final Long variantId) {
    this.primaryVariantId = variantId;
  }

  /**
   * @return the variantName
   */
  public String getPrimaryVariantName() {
    return this.primaryVariantName;
  }

  /**
   * @param variantName the variantName to set
   */
  public void setPrimaryVariantName(final String variantName) {
    this.primaryVariantName = variantName;
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
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }

  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the obdFlag
   */
  public String getObdFlag() {
    return this.obdFlag;
  }


  /**
   * @param obdFlag the obdFlag to set
   */
  public void setObdFlag(final String obdFlag) {
    this.obdFlag = obdFlag;
  }


  /**
   * @return the simpQuesRespValue
   */
  public String getSimpQuesRespValue() {
    return this.simpQuesRespValue;
  }


  /**
   * @param simpQuesRespValue the simpQuesRespValue to set
   */
  public void setSimpQuesRespValue(final String simpQuesRespValue) {
    this.simpQuesRespValue = simpQuesRespValue;
  }


  /**
   * @return the simpQuesRemarks
   */
  public String getSimpQuesRemarks() {
    return this.simpQuesRemarks;
  }


  /**
   * @param simpQuesRemarks the simpQuesRemarks to set
   */
  public void setSimpQuesRemarks(final String simpQuesRemarks) {
    this.simpQuesRemarks = simpQuesRemarks;
  }

}

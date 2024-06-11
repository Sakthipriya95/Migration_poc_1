/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * C:\temp\classes Model class
 *
 * @author BRU2COB
 */
public class CDRResultParameter implements Cloneable, Comparable<CDRResultParameter>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 1795095498231049535L;
  /**
   * Rvw Param Id
   */
  private Long id;
  /**
   * Param name
   */
  private String name;
  /**
   * Param type
   */
  private String pType;
  /**
   * Param Id
   */
  private Long paramId;
  /**
   * Result Id
   */
  private Long resultId;
  /**
   * Rvw Fun Id
   */
  private Long rvwFunId;

  /**
   * Rvw WP Resp Id
   */
  private Long rvwWpRespId;

  /**
   * Rvw Method(Ready For series)
   */
  private String rvwMethod;
  /**
   * Lower Limit
   */
  private BigDecimal lowerLimit;
  /**
   * Upper Limit
   */
  private BigDecimal upperLimit;
  /**
   * Ref Value
   */
  private byte[] refValue;
  /**
   * Ref Unit
   */
  private String refUnit;
  /**
   * Hint
   */
  private String hint;

  /**
   * Hint
   */
  private String paramHint;
  /**
   * Checked Value
   */
  private byte[] checkedValue;
  /**
   * Result
   */
  private String result;
  /**
   * Rvw Comment
   */
  private String rvwComment;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;
  /**
   * Check Unit
   */
  private String checkUnit;
  /**
   * Change Flag
   */
  private Long changeFlag;
  /**
   * Match Ref Flag(Exact Match)
   */
  private String matchRefFlag;
  /**
   * Rvw File Id
   */
  private Long rvwFileId;
  /**
   * Bitwise Limit
   */
  private String bitwiseLimit;
  /**
   * Isbitwise
   */
  private String isbitwise;
  /**
   * Parent Param Id
   */
  private Long parentParamId;
  /**
   * Review Score
   */
  private String reviewScore;
  /**
   * Compli Result
   */
  private String compliResult;
  /**
   * Qssd Result
   */
  private String qssdResult;
  /**
   * Lab Obj Id
   */
  private BigDecimal labObjId;
  /**
   * Rev Id
   */
  private BigDecimal revId;
  /**
   * Compli Lab Obj Id
   */
  private BigDecimal compliLabObjId;
  /**
   * Compli Rev Id
   */
  private BigDecimal compliRevId;
  /**
   * Qssd Lab Obj Id
   */
  private BigDecimal qssdLabObjId;
  /**
   * Qssd Rev Id
   */
  private BigDecimal qssdRevId;
  /**
   * Secondary Result
   */
  private String secondaryResult;
  /**
   * Secondary Result State
   */
  private String secondaryResultState;
  /**
   * Sr Result
   */
  private String srResult;
  /**
   * Sr Error Details
   */
  private String srErrorDetails;
  /**
   * Sr Accepted Flag
   */
  private String srAcceptedFlag;
  /**
   * Sr Accepted User
   */
  private String srAcceptedUser;
  /**
   * Sr Accepted Date
   */
  private String srAcceptedDate;
  /**
   * Func name
   */
  private String funcName;

  /**
   * Description
   */
  private String description;

  /**
   * Maturity Level
   */
  private String maturityLvl;
  /**
   * isReadOnlyParam
   */
  private boolean isReadOnlyParam;
  /**
   * arcReleasedFlag
   */
  private boolean arcReleasedFlag;

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
   * @return paramId
   */
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId set paramId
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }

  /**
   * @return resultId
   */
  public Long getResultId() {
    return this.resultId;
  }

  /**
   * @param resultId set resultId
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
  }

  /**
   * @return rvwFunId
   */
  public Long getRvwFunId() {
    return this.rvwFunId;
  }

  /**
   * @param rvwFunId set rvwFunId
   */
  public void setRvwFunId(final Long rvwFunId) {
    this.rvwFunId = rvwFunId;
  }

  /**
   * @return rvwMethod
   */
  public String getRvwMethod() {
    return this.rvwMethod;
  }

  /**
   * @param rvwMethod set rvwMethod
   */
  public void setRvwMethod(final String rvwMethod) {
    this.rvwMethod = rvwMethod;
  }

  /**
   * @return lowerLimit
   */
  public BigDecimal getLowerLimit() {
    return this.lowerLimit;
  }

  /**
   * @param lowerLimit set lowerLimit
   */
  public void setLowerLimit(final BigDecimal lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  /**
   * @return upperLimit
   */
  public BigDecimal getUpperLimit() {
    return this.upperLimit;
  }

  /**
   * @param upperLimit set upperLimit
   */
  public void setUpperLimit(final BigDecimal upperLimit) {
    this.upperLimit = upperLimit;
  }

  /**
   * @return refValue
   */
  public byte[] getRefValue() {
    return this.refValue;
  }

  /**
   * @param refValue set refValue
   */
  public void setRefValue(final byte[] refValue) {
    this.refValue = refValue;
  }

  /**
   * @return refUnit
   */
  public String getRefUnit() {
    return this.refUnit;
  }

  /**
   * @param refUnit set refUnit
   */
  public void setRefUnit(final String refUnit) {
    this.refUnit = refUnit;
  }

  /**
   * @return hint
   */
  public String getHint() {
    return this.hint;
  }

  /**
   * @param hint set hint
   */
  public void setHint(final String hint) {
    this.hint = hint;
  }


  /**
   * @return the paramHint
   */
  public String getParamHint() {
    return this.paramHint;
  }


  /**
   * @param paramHint the paramHint to set
   */
  public void setParamHint(final String paramHint) {
    this.paramHint = paramHint;
  }

  /**
   * @return checkedValue
   */
  public byte[] getCheckedValue() {
    return this.checkedValue;
  }

  /**
   * @param checkedValue set checkedValue
   */
  public void setCheckedValue(final byte[] checkedValue) {
    this.checkedValue = checkedValue;
  }

  /**
   * @return result
   */
  public String getResult() {
    return this.result;
  }

  /**
   * @param result set result
   */
  public void setResult(final String result) {
    this.result = result;
  }

  /**
   * @return rvwComment
   */
  public String getRvwComment() {
    return this.rvwComment;
  }

  /**
   * @param rvwComment set rvwComment
   */
  public void setRvwComment(final String rvwComment) {
    this.rvwComment = rvwComment;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
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
   * @return checkUnit
   */
  public String getCheckUnit() {
    return this.checkUnit;
  }

  /**
   * @param checkUnit set checkUnit
   */
  public void setCheckUnit(final String checkUnit) {
    this.checkUnit = checkUnit;
  }

  /**
   * @return changeFlag
   */
  public Long getChangeFlag() {
    return this.changeFlag;
  }

  /**
   * @param changeFlag set changeFlag
   */
  public void setChangeFlag(final Long changeFlag) {
    this.changeFlag = changeFlag;
  }

  /**
   * @return matchRefFlag
   */
  public String getMatchRefFlag() {
    return this.matchRefFlag;
  }

  /**
   * @param matchRefFlag set matchRefFlag
   */
  public void setMatchRefFlag(final String matchRefFlag) {
    this.matchRefFlag = matchRefFlag;
  }

  /**
   * @return rvwFileId
   */
  public Long getRvwFileId() {
    return this.rvwFileId;
  }

  /**
   * @param rvwFileId set rvwFileId
   */
  public void setRvwFileId(final Long rvwFileId) {
    this.rvwFileId = rvwFileId;
  }

  /**
   * @return bitwiseLimit
   */
  public String getBitwiseLimit() {
    return this.bitwiseLimit;
  }

  /**
   * @param bitwiseLimit set bitwiseLimit
   */
  public void setBitwiseLimit(final String bitwiseLimit) {
    this.bitwiseLimit = bitwiseLimit;
  }

  /**
   * @return isbitwise
   */
  public String getIsbitwise() {
    return this.isbitwise;
  }

  /**
   * @param isbitwise set isbitwise
   */
  public void setIsbitwise(final String isbitwise) {
    this.isbitwise = isbitwise;
  }

  /**
   * @return parentParamId
   */
  public Long getParentParamId() {
    return this.parentParamId;
  }

  /**
   * @param parentParamId set parentParamId
   */
  public void setParentParamId(final Long parentParamId) {
    this.parentParamId = parentParamId;
  }

  /**
   * @return reviewScore
   */
  public String getReviewScore() {
    return this.reviewScore;
  }

  /**
   * @param reviewScore set reviewScore
   */
  public void setReviewScore(final String reviewScore) {
    this.reviewScore = reviewScore;
  }


  /**
   * @return compliResult
   */
  public String getCompliResult() {
    return this.compliResult;
  }

  /**
   * @param compliResult set compliResult
   */
  public void setCompliResult(final String compliResult) {
    this.compliResult = compliResult;
  }

  /**
   * @return labObjId
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId set labObjId
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return revId
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId set revId
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return compliLabObjId
   */
  public BigDecimal getCompliLabObjId() {
    return this.compliLabObjId;
  }

  /**
   * @param compliLabObjId set compliLabObjId
   */
  public void setCompliLabObjId(final BigDecimal compliLabObjId) {
    this.compliLabObjId = compliLabObjId;
  }

  /**
   * @return compliRevId
   */
  public BigDecimal getCompliRevId() {
    return this.compliRevId;
  }

  /**
   * @param compliRevId set compliRevId
   */
  public void setCompliRevId(final BigDecimal compliRevId) {
    this.compliRevId = compliRevId;
  }

  /**
   * @return secondaryResult
   */
  public String getSecondaryResult() {
    return this.secondaryResult;
  }

  /**
   * @param secondaryResult set secondaryResult
   */
  public void setSecondaryResult(final String secondaryResult) {
    this.secondaryResult = secondaryResult;
  }

  /**
   * @return secondaryResultState
   */
  public String getSecondaryResultState() {
    return this.secondaryResultState;
  }

  /**
   * @param secondaryResultState set secondaryResultState
   */
  public void setSecondaryResultState(final String secondaryResultState) {
    this.secondaryResultState = secondaryResultState;
  }

  /**
   * @return srResult
   */
  public String getSrResult() {
    return this.srResult;
  }

  /**
   * @param srResult set srResult
   */
  public void setSrResult(final String srResult) {
    this.srResult = srResult;
  }

  /**
   * @return srErrorDetails
   */
  public String getSrErrorDetails() {
    return this.srErrorDetails;
  }

  /**
   * @param srErrorDetails set srErrorDetails
   */
  public void setSrErrorDetails(final String srErrorDetails) {
    this.srErrorDetails = srErrorDetails;
  }

  /**
   * @return srAcceptedFlag
   */
  public String getSrAcceptedFlag() {
    return this.srAcceptedFlag;
  }

  /**
   * @param srAcceptedFlag set srAcceptedFlag
   */
  public void setSrAcceptedFlag(final String srAcceptedFlag) {
    this.srAcceptedFlag = srAcceptedFlag;
  }

  /**
   * @return srAcceptedUser
   */
  public String getSrAcceptedUser() {
    return this.srAcceptedUser;
  }

  /**
   * @param srAcceptedUser set srAcceptedUser
   */
  public void setSrAcceptedUser(final String srAcceptedUser) {
    this.srAcceptedUser = srAcceptedUser;
  }

  /**
   * @return srAcceptedDate
   */
  public String getSrAcceptedDate() {
    return this.srAcceptedDate;
  }

  /**
   * @param srAcceptedDate set srAcceptedDate
   */
  public void setSrAcceptedDate(final String srAcceptedDate) {
    this.srAcceptedDate = srAcceptedDate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public CDRResultParameter clone() {

    CDRResultParameter resultParam = null;

    try {
      resultParam = (CDRResultParameter) super.clone();

    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return resultParam;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRResultParameter object) {
    int compareResult = ModelUtil.compare(getName(), object.getName());
    if (compareResult == 0) {
      compareResult = ModelUtil.compare(getId(), object.getId());
    }
    return compareResult;
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

    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((CDRResultParameter) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the pType
   */
  public String getpType() {
    return this.pType;
  }


  /**
   * @param pType the pType to set
   */
  public void setpType(final String pType) {
    this.pType = pType;
  }


  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
  }


  /**
   * @return the rvwWpRespId
   */
  public Long getRvwWpRespId() {
    return this.rvwWpRespId;
  }


  /**
   * @param rvwWpRespId the rvwWpRespId to set
   */
  public void setRvwWpRespId(final Long rvwWpRespId) {
    this.rvwWpRespId = rvwWpRespId;
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
   * @return the maturityLvl
   */
  public String getMaturityLvl() {
    return this.maturityLvl;
  }


  /**
   * @param maturityLvl the maturityLvl to set
   */
  public void setMaturityLvl(final String maturityLvl) {
    this.maturityLvl = maturityLvl;
  }


  /**
   * @return the qssdResult
   */
  public String getQssdResult() {
    return this.qssdResult;
  }


  /**
   * @param qssdResult the qssdResult to set
   */
  public void setQssdResult(final String qssdResult) {
    this.qssdResult = qssdResult;
  }


  /**
   * @return the qssdLabObjId
   */
  public BigDecimal getQssdLabObjId() {
    return this.qssdLabObjId;
  }


  /**
   * @param qssdLabObjId the qssdLabObjId to set
   */
  public void setQssdLabObjId(final BigDecimal qssdLabObjId) {
    this.qssdLabObjId = qssdLabObjId;
  }


  /**
   * @return the qssdRevId
   */
  public BigDecimal getQssdRevId() {
    return this.qssdRevId;
  }


  /**
   * @param qssdRevId the qssdRevId to set
   */
  public void setQssdRevId(final BigDecimal qssdRevId) {
    this.qssdRevId = qssdRevId;
  }


  /**
   * @return the isReadOnlyParam
   */
  public boolean isReadOnlyParam() {
    return this.isReadOnlyParam;
  }


  /**
   * @param isReadOnlyParam the isReadOnlyParam to set
   */
  public void setReadOnlyParam(final boolean isReadOnlyParam) {
    this.isReadOnlyParam = isReadOnlyParam;
  }


  /**
   * @return the arcReleasedFlag
   */
  public boolean getArcReleasedFlag() {
    return this.arcReleasedFlag;
  }


  /**
   * @param arcReleasedFlag the arcReleasedFlag to set
   */
  public void setArcReleasedFlag(final boolean arcReleasedFlag) {
    this.arcReleasedFlag = arcReleasedFlag;
  }


}

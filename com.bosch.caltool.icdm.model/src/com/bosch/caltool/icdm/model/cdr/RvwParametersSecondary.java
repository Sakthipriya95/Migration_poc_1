package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Review Parameters Secondary Model class
 *
 * @author bru2cob
 */
public class RvwParametersSecondary implements Comparable<RvwParametersSecondary>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 2881439869124917835L;
  /**
   * Sec Rvw Param Id
   */
  private Long id;
  /**
   * Rvw Param Id
   */
  private Long rvwParamId;
  /**
   * Sec Review Id
   */
  private Long secReviewId;
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
   * Result
   */
  private String result;
  /**
   * Change Flag
   */
  private int changeFlag;
  /**
   * Match Ref Flag-->Exact Match
   */
  private String matchRefFlag;
  /**
   * Bitwise Limit
   */
  private String bitwiseLimit;
  /**
   * Isbitwise
   */
  private String isbitwise;
  /**
   * Lab Obj Id
   */
  private Long labObjId;
  /**
   * Rev Id
   */
  private Long revId;
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
   * Rvw Method-->ready for series
   */
  private String rvwMethod;
  /**
   * Param name
   */
  private String name;

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
   * @return rvwParamId
   */
  public Long getRvwParamId() {
    return this.rvwParamId;
  }

  /**
   * @param rvwParamId set rvwParamId
   */
  public void setRvwParamId(final Long rvwParamId) {
    this.rvwParamId = rvwParamId;
  }

  /**
   * @return secReviewId
   */
  public Long getSecReviewId() {
    return this.secReviewId;
  }

  /**
   * @param secReviewId set secReviewId
   */
  public void setSecReviewId(final Long secReviewId) {
    this.secReviewId = secReviewId;
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
   * @return changeFlag
   */
  public int getChangeFlag() {
    return this.changeFlag;
  }

  /**
   * @param changeFlag set changeFlag
   */
  public void setChangeFlag(final int changeFlag) {
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
   * @return labObjId
   */
  public Long getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId set labObjId
   */
  public void setLabObjId(final Long labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return revId
   */
  public Long getRevId() {
    return this.revId;
  }

  /**
   * @param revId set revId
   */
  public void setRevId(final Long revId) {
    this.revId = revId;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RvwParametersSecondary object) {

    int compare = ModelUtil.compare(getName(), object.getName());
    if (compare == 0) {
      return ModelUtil.compare(getSecReviewId(), object.getSecReviewId());
    }
    return compare;
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
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getName(), ((RvwParametersSecondary) obj).getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getName());
  }

}

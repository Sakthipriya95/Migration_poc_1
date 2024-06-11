package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * DaWpResp Model class
 *
 * @author say8cob
 */
public class DaWpResp implements Cloneable, Comparable<DaWpResp>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 118107846865299L;
  /**
   * Da Wp Resp Id
   */
  private Long id;
  /**
   * Data Assessment Id
   */
  private Long dataAssessmentId;
  /**
   * A2l Wp Id
   */
  private BigDecimal a2lWpId;
  /**
   * A2l Wp Name
   */
  private String a2lWpName;
  /**
   * A2l Resp Id
   */
  private BigDecimal a2lRespId;
  /**
   * A2l Resp Alias Name
   */
  private String a2lRespAliasName;
  /**
   * A2l Resp Name
   */
  private String a2lRespName;
  /**
   * A2l Resp Type
   */
  private String a2lRespType;
  /**
   * Wp Ready For Production Flag
   */
  private String wpReadyForProductionFlag;
  /**
   * Wp Finished Flag
   */
  private String wpFinishedFlag;
  /**
   * Qnaires Answered Flag
   */
  private String qnairesAnsweredFlag;
  /**
   * Parameter Reviewed Flag
   */
  private String parameterReviewedFlag;
  /**
   * Hex Rvw Equal Flag
   */
  private String hexRvwEqualFlag;
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
   * @return dataAssessmentId
   */
  public Long getDataAssessmentId() {
    return this.dataAssessmentId;
  }

  /**
   * @param dataAssessmentId set dataAssessmentId
   */
  public void setDataAssessmentId(final Long dataAssessmentId) {
    this.dataAssessmentId = dataAssessmentId;
  }

  /**
   * @return a2lWpId
   */
  public BigDecimal getA2lWpId() {
    return this.a2lWpId;
  }

  /**
   * @param a2lWpId set a2lWpId
   */
  public void setA2lWpId(final BigDecimal a2lWpId) {
    this.a2lWpId = a2lWpId;
  }

  /**
   * @return a2lWpName
   */
  public String getA2lWpName() {
    return this.a2lWpName;
  }

  /**
   * @param a2lWpName set a2lWpName
   */
  public void setA2lWpName(final String a2lWpName) {
    this.a2lWpName = a2lWpName;
  }

  /**
   * @return a2lRespId
   */
  public BigDecimal getA2lRespId() {
    return this.a2lRespId;
  }

  /**
   * @param a2lRespId set a2lRespId
   */
  public void setA2lRespId(final BigDecimal a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  /**
   * @return a2lRespAliasName
   */
  public String getA2lRespAliasName() {
    return this.a2lRespAliasName;
  }

  /**
   * @param a2lRespAliasName set a2lRespAliasName
   */
  public void setA2lRespAliasName(final String a2lRespAliasName) {
    this.a2lRespAliasName = a2lRespAliasName;
  }

  /**
   * @return a2lRespName
   */
  public String getA2lRespName() {
    return this.a2lRespName;
  }

  /**
   * @param a2lRespName set a2lRespName
   */
  public void setA2lRespName(final String a2lRespName) {
    this.a2lRespName = a2lRespName;
  }

  /**
   * @return a2lRespType
   */
  public String getA2lRespType() {
    return this.a2lRespType;
  }

  /**
   * @param a2lRespType set a2lRespType
   */
  public void setA2lRespType(final String a2lRespType) {
    this.a2lRespType = a2lRespType;
  }

  /**
   * @return wpReadyForProductionFlag
   */
  public String getWpReadyForProductionFlag() {
    return this.wpReadyForProductionFlag;
  }

  /**
   * @param wpReadyForProductionFlag set wpReadyForProductionFlag
   */
  public void setWpReadyForProductionFlag(final String wpReadyForProductionFlag) {
    this.wpReadyForProductionFlag = wpReadyForProductionFlag;
  }

  /**
   * @return wpFinishedFlag
   */
  public String getWpFinishedFlag() {
    return this.wpFinishedFlag;
  }

  /**
   * @param wpFinishedFlag set wpFinishedFlag
   */
  public void setWpFinishedFlag(final String wpFinishedFlag) {
    this.wpFinishedFlag = wpFinishedFlag;
  }

  /**
   * @return qnairesAnsweredFlag
   */
  public String getQnairesAnsweredFlag() {
    return this.qnairesAnsweredFlag;
  }

  /**
   * @param qnairesAnsweredFlag set qnairesAnsweredFlag
   */
  public void setQnairesAnsweredFlag(final String qnairesAnsweredFlag) {
    this.qnairesAnsweredFlag = qnairesAnsweredFlag;
  }

  /**
   * @return parameterReviewedFlag
   */
  public String getParameterReviewedFlag() {
    return this.parameterReviewedFlag;
  }

  /**
   * @param parameterReviewedFlag set parameterReviewedFlag
   */
  public void setParameterReviewedFlag(final String parameterReviewedFlag) {
    this.parameterReviewedFlag = parameterReviewedFlag;
  }

  /**
   * @return hexRvwEqualFlag
   */
  public String getHexRvwEqualFlag() {
    return this.hexRvwEqualFlag;
  }

  /**
   * @param hexRvwEqualFlag set hexRvwEqualFlag
   */
  public void setHexRvwEqualFlag(final String hexRvwEqualFlag) {
    this.hexRvwEqualFlag = hexRvwEqualFlag;
  }

  /**
   * {@inheritDoc}
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
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
   * {@inheritDoc}
   */
  @Override
  public DaWpResp clone() {
    DaWpResp daWpResp = null;
    try {
      daWpResp = (DaWpResp) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return daWpResp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DaWpResp object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getA2lWpId(), ((DaWpResp) obj).getA2lWpId()) &&
        ModelUtil.isEqual(getA2lRespId(), ((DaWpResp) obj).getA2lRespId()) &&
        ModelUtil.isEqual(getId(), ((DaWpResp) obj).getId()); // To Be Checked
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}

package com.bosch.caltool.icdm.model.cdr.compli;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * CompliReviewParamResultHex Model class
 *
 * @author dmr1cob
 */
public class CompliRvwHexParam implements Comparable<CompliRvwHexParam>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 321003744351655L;
  /**
   * Hex Params Id
   */
  private Long id;
  /**
   * Compli Rvw Hex Id
   */
  private Long compliRvwHexId;
  /**
   * Param Id
   */
  private Long paramId;
  /**
   * Check Value
   */
  private byte[] checkValue;
  /**
   * Compli Result
   */
  private String compliResult;

  /**
   * QSSD Result
   */
  private String qssdResult;

  /**
   * Lab Obj Id
   */
  private Long labObjId;
  /**
   * Rev Id
   */
  private Long revId;
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
   * @return compliRvwHexId
   */
  public Long getCompliRvwHexId() {
    return this.compliRvwHexId;
  }

  /**
   * @param compliRvwHexId set compliRvwHexId
   */
  public void setCompliRvwHexId(final Long compliRvwHexId) {
    this.compliRvwHexId = compliRvwHexId;
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
   * @return checkValue
   */
  public byte[] getCheckValue() {
    return this.checkValue;
  }

  /**
   * @param checkValue set checkValue
   */
  public void setCheckValue(final byte[] checkValue) {
    this.checkValue = checkValue;
  }


  /**
   * @return the compliResult
   */
  public String getCompliResult() {
    return this.compliResult;
  }


  /**
   * @param compliResult the compliResult to set
   */
  public void setCompliResult(final String compliResult) {
    this.compliResult = compliResult;
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
   * @return created user
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser user
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return created date
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate date
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modified user
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser user
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modified date
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate date
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompliRvwHexParam object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((CompliRvwHexParam) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
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


}

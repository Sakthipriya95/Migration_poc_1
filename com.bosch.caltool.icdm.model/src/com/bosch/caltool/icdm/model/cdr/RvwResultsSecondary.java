package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Secondary Review Results Model class
 *
 * @author bru2cob
 */
public class RvwResultsSecondary implements Comparable<RvwResultsSecondary>, IModel {

  /**
   * Sec Review Id
   */
  private Long id;
  /**
   * Result Id
   */
  private Long resultId;
  /**
   * Rset Id
   */
  private Long rsetId;
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
   * Ssd Release Id
   */
  private Long ssdReleaseId;
  /**
   * Source
   */
  private String source;
  /**
   * Ssd Version Id
   */
  private Long ssdVersionId;
  /**
   * Ruleset name
   */
  private String ruleSetName;

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
   * @return ssdReleaseId
   */
  public Long getSsdReleaseId() {
    return this.ssdReleaseId;
  }

  /**
   * @param ssdReleaseId set ssdReleaseId
   */
  public void setSsdReleaseId(final Long ssdReleaseId) {
    this.ssdReleaseId = ssdReleaseId;
  }

  /**
   * @return source
   */
  public String getSource() {
    return this.source;
  }

  /**
   * @param source set source
   */
  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * @return ssdVersionId
   */
  public Long getSsdVersionId() {
    return this.ssdVersionId;
  }

  /**
   * @param ssdVersionId set ssdVersionId
   */
  public void setSsdVersionId(final Long ssdVersionId) {
    this.ssdVersionId = ssdVersionId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RvwResultsSecondary object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwResultsSecondary) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the ruleSetName
   */
  public String getRuleSetName() {
    return this.ruleSetName;
  }


  /**
   * @param ruleSetName the ruleSetName to set
   */
  public void setRuleSetName(final String ruleSetName) {
    this.ruleSetName = ruleSetName;
  }

}

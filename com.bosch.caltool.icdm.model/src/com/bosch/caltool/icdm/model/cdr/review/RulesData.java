/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bru2cob
 */
public class RulesData {

  /**
   * is common rules secondary
   */
  private boolean commonRulesSecondary;
  /**
   * is common rules primary
   */
  private boolean commonRulesPrimary;
  /**
   * ssd release id
   */
  private Long ssdReleaseId;
  /**
   * ssd rules file path
   */
  private String ssdRuleFilePath;
  /**
   * primary ruleset id
   */
  private Long primaryRuleSetId;
  /**
   * Set of secondary rule set ids
   */
  private Set<Long> secondaryRuleSetIds;

  /**
   * @return the commonRulesSecondary
   */
  public boolean isCommonRulesSecondary() {
    return this.commonRulesSecondary;
  }

  /**
   * @param commonRulesSecondary the commonRulesSecondary to set
   */
  public void setCommonRulesSecondary(final boolean commonRulesSecondary) {
    this.commonRulesSecondary = commonRulesSecondary;
  }

  /**
   * @return the commonRulesPrimary
   */
  public boolean isCommonRulesPrimary() {
    return this.commonRulesPrimary;
  }

  /**
   * @param commonRulesPrimary the commonRulesPrimary to set
   */
  public void setCommonRulesPrimary(final boolean commonRulesPrimary) {
    this.commonRulesPrimary = commonRulesPrimary;
  }

  /**
   * @return the ssdReleaseId
   */
  public Long getSsdReleaseId() {
    return this.ssdReleaseId;
  }

  /**
   * @param ssdReleaseId the ssdReleaseId to set
   */
  public void setSsdReleaseId(final Long ssdReleaseId) {
    this.ssdReleaseId = ssdReleaseId;
  }

  /**
   * @return the ssdRuleFilePath
   */
  public String getSsdRuleFilePath() {
    return this.ssdRuleFilePath;
  }

  /**
   * @param ssdRuleFilePath the ssdRuleFilePath to set
   */
  public void setSsdRuleFilePath(final String ssdRuleFilePath) {
    this.ssdRuleFilePath = ssdRuleFilePath;
  }

  /**
   * @return the primaryRuleSetId
   */
  public Long getPrimaryRuleSetId() {
    return this.primaryRuleSetId;
  }

  /**
   * @param primaryRuleSetId the primaryRuleSetId to set
   */
  public void setPrimaryRuleSetId(final Long primaryRuleSetId) {
    this.primaryRuleSetId = primaryRuleSetId;
  }

  /**
   * @return the secondaryRuleSetIds
   */
  public Set<Long> getSecondaryRuleSetIds() {
    return this.secondaryRuleSetIds;
  }

  /**
   * @param secondaryRuleSetIds the secondaryRuleSetIds to set
   */
  public void setSecondaryRuleSetIds(final Set<Long> secondaryRuleSetIds) {
    this.secondaryRuleSetIds = secondaryRuleSetIds == null ? null : new HashSet<>(secondaryRuleSetIds);
  }

}

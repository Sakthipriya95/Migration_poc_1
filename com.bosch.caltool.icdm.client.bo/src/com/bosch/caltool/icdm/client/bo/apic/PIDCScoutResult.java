/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * @author rgo7cob
 */
public class PIDCScoutResult implements Comparable<PIDCScoutResult> {

  /**
   * Pid Card
   */
  private final PidcSearchResult result;

  /**
   * Initial buffer size capacity for the formatted tool tip text
   */
  private static final int INITIAL_CAPACITY_TOOLTIP_TEXT = 54;

  // Icdm-1283 Show the Icons for PIDC's with A2l Files and Reviews
  /**
   * @param result Pidc Search Result
   */
  // ICDM-2255
  public PIDCScoutResult(final PidcSearchResult result) {
    super();
    this.result = result;
  }

  /**
   * @return the PIDC Version
   */
  public PidcVersion getPidcVersion() {
    return this.result.getPidcVersion();
  }

  /**
   * @return the hasA2lFiles
   */
  public boolean hasA2lFiles() {
    return this.result.isA2lFilesMapped();
  }


  /**
   * @return the hasReviews
   */
  public boolean hasReviews() {
    return this.result.isReviewResultsFound();
  }


  /**
   * @return the hasFocusMatrix
   */
  // ICDM-2255
  public boolean hasFocusMatrix() {
    return this.result.isFocusMatrixDefined();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCScoutResult result2) {
    // ICDM-2485
    // compare based on the pidc name
    return ApicUtil.compare(getPidcVersion(), result2.getPidcVersion());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return Objects.isNull(obj) || (getClass() != obj.getClass()) ? false
        : Objects.equals(getPidcVersion(), ((PIDCScoutResult) obj).getPidcVersion());
  }

  /**
   * returns the formatted Tool tip text
   *
   * @return tooltip text
   */
  public String getFormattedToolTipText() {

    StringBuilder toolTipTextBuilder = new StringBuilder(INITIAL_CAPACITY_TOOLTIP_TEXT);
    String pidcVersionName = getPidcVersion().getName();
    toolTipTextBuilder.append("PIDC Name = ").append(pidcVersionName).append(',');

    // if the pidc version has atleast one focus matrix
    if (hasFocusMatrix()) {
      toolTipTextBuilder.append("\n Focus Matrix = YES,");
    }

    // if the pidc version has atleast one review result
    if (hasReviews()) {
      toolTipTextBuilder.append("\n Reviews = YES,");
    }

    // if the pidc version has atleast one
    else if (hasA2lFiles()) {
      toolTipTextBuilder.append("\n A2L Files = YES,");
    }

    toolTipTextBuilder.deleteCharAt(toolTipTextBuilder.length() - 1).append('.');
    return toolTipTextBuilder.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(getPidcVersion());
  }

  /**
   * @param level level
   * @return level value
   */
  public String getLevelValueText(final Long level) {
    return this.result.getLevelAttrMap().get(level).getValue();
  }

  /**
   * @return map. key - level, value - pidc level attribute
   */
  public Map<Long, PidcVersionAttribute> getLevelAttributesMap() {
    return new HashMap<>(this.result.getLevelAttrMap());
  }

}

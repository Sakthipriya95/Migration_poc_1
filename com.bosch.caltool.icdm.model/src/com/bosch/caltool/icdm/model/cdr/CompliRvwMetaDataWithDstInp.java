/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.Map.Entry;

/**
 * @author apj4cob
 */
public class CompliRvwMetaDataWithDstInp extends CompliReviewInputMetaData {

  /**
   *
   */
  private static final long serialVersionUID = 7008872062972362499L;

  /**
   * vcdm Dst revision id
   */
  private Long vcdmDstId;


  private String aprjName;

  private Integer aprjRevision;

  private String dstName;

  private Integer dstRevision;

  private boolean isCheckedIn;


  /**
   * @return the aprjName
   */
  public String getAprjName() {
    return this.aprjName;
  }


  /**
   * @param aprjName the aprjName to set
   */
  public void setAprjName(final String aprjName) {
    this.aprjName = aprjName;
  }


  /**
   * @return the aprjRevision
   */
  public Integer getAprjRevision() {
    return this.aprjRevision;
  }


  /**
   * @param aprjRevision the aprjRevision to set
   */
  public void setAprjRevision(final Integer aprjRevision) {
    this.aprjRevision = aprjRevision;
  }


  /**
   * @return the dstName
   */
  public String getDstName() {
    return this.dstName;
  }


  /**
   * @param dstName the dstName to set
   */
  public void setDstName(final String dstName) {
    this.dstName = dstName;
  }


  /**
   * @return the dstRevision
   */
  public Integer getDstRevision() {
    return this.dstRevision;
  }


  /**
   * @param dstRevision the dstRevision to set
   */
  public void setDstRevision(final Integer dstRevision) {
    this.dstRevision = dstRevision;
  }


  /**
   * @return the isCheckedIn
   */
  public boolean isCheckedIn() {
    return this.isCheckedIn;
  }


  /**
   * @param isCheckedIn the isCheckedIn to set
   */
  public void setCheckedIn(final boolean isCheckedIn) {
    this.isCheckedIn = isCheckedIn;
  }


  /**
   * @return the vcdmDstId
   */
  public Long getVcdmDstId() {
    return this.vcdmDstId;
  }


  /**
   * @param vcdmDstId the vcdmDstId to set
   */
  public void setVcdmDstId(final Long vcdmDstId) {
    this.vcdmDstId = vcdmDstId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CompliReviewInputMetaData");
    builder.append("[");

    builder.append("pverName=").append(getPverName()).append(STR_DELIMITER_CONSTANT);
    builder.append("pverVariant=").append(getPverVariant()).append(STR_DELIMITER_CONSTANT);
    builder.append("a2lFileName=").append(getA2lFileName()).append(STR_DELIMITER_CONSTANT);

    for (Entry<Long, Long> pidcElementEntry : getHexFilePidcElement().entrySet()) {
      builder.append("pidcElement").append(pidcElementEntry.getKey()).append(STR_EQUALS_CONSTANT)
          .append(pidcElementEntry.getValue()).append(STR_DELIMITER_CONSTANT);

    }

    for (Entry<Long, String> hexfileEntry : getHexfileIdxMap().entrySet()) {
      builder.append("Hexfile").append(hexfileEntry.getKey()).append(STR_EQUALS_CONSTANT)
          .append(hexfileEntry.getValue()).append(STR_DELIMITER_CONSTANT);

    }

    for (Entry<Long, String> dstFiles : getDstMap().entrySet()) {
      builder.append("dtsfile").append(dstFiles.getKey()).append(STR_EQUALS_CONSTANT).append(dstFiles.getValue())
          .append(STR_DELIMITER_CONSTANT);

    }
    builder.append("]");
    return builder.toString();
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author rgo7cob input model for web flow Compli Review
 */
public class CompliReviewInputMetaData implements Serializable {


  /**
   *
   */
  private static final long serialVersionUID = 3352480281810312288L;

  /**
   *
   */
  protected static final String STR_DELIMITER_CONSTANT = ";";
  /**
   *
   */
  protected static final String STR_EQUALS_CONSTANT = "=";

  /**
   * web flow id
   */
  private String webflowID;
  /**
   * pver Name
   */
  private String pverName;

  private Long pidcA2L;

  /**
   * @return the pidcA2L
   */
  public Long getPidcA2L() {
    return this.pidcA2L;
  }


  /**
   * @param pidcA2L the pidcA2L to set
   */
  public void setPidcA2L(final Long pidcA2L) {
    this.pidcA2L = pidcA2L;
  }

  /**
   * pver Variant
   */
  private String pverVariant;
  /**
   * pver version
   */
  private String pverRevision;

  /**
   * a2l file name
   */
  private String a2lFileName;

  private ExcelReportTypeEnum datafileoption;

  private boolean predecessorCheck;

  /**
   * Key is the hex file index number and the value is the Varaint id/ Pidc version id
   */
  private Map<Long, Long> hexFilePidcElement = new TreeMap<>();


  /**
   * List of hex file names
   */
  private Map<Long, String> hexfileIdxMap = new TreeMap<>();

  /**
   * List of dst names Key - index value - dst name
   */
  private Map<Long, String> dstMap = new TreeMap<>();


  /**
   * @return the dstMap
   */
  public Map<Long, String> getDstMap() {
    return this.dstMap;
  }


  /**
   * @param dstMap the dstMap to set
   */
  public void setDstMap(final Map<Long, String> dstMap) {
    this.dstMap = dstMap;
  }


  /**
   * @return the webflowID
   */
  public String getWebflowID() {
    return this.webflowID;
  }


  /**
   * @param webflowID the webflowID to set
   */
  public void setWebflowID(final String webflowID) {
    this.webflowID = webflowID;
  }


  /**
   * @return the pverName
   */
  public String getPverName() {
    return this.pverName;
  }


  /**
   * @param pverName the pverName to set
   */
  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }


  /**
   * @return the pverVariant
   */
  public String getPverVariant() {
    return this.pverVariant;
  }


  /**
   * @param pverVariant the pverVariant to set
   */
  public void setPverVariant(final String pverVariant) {
    this.pverVariant = pverVariant;
  }


  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }


  /**
   * @param a2lFileName the a2lFileName to set
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }


  /**
   * @return the hexFilePidcElement
   */
  public Map<Long, Long> getHexFilePidcElement() {
    return this.hexFilePidcElement;
  }


  /**
   * @param hexFilePidcElement the hexFilePidcElement to set
   */
  public void setHexFilePidcElement(final Map<Long, Long> hexFilePidcElement) {
    this.hexFilePidcElement = hexFilePidcElement;
  }

  /**
   * @return the hexfileIdxMap
   */
  public Map<Long, String> getHexfileIdxMap() {
    return this.hexfileIdxMap;
  }


  /**
   * @param hexfileIdxMap the hexfileIdxMap to set
   */
  public void setHexfileIdxMap(final Map<Long, String> hexfileIdxMap) {
    this.hexfileIdxMap = hexfileIdxMap;
  }


  /**
   * @return the pverRevision
   */
  public String getPverRevision() {
    return this.pverRevision;
  }


  /**
   * @param pverRevision the pverRevision to set
   */
  public void setPverRevision(final String pverRevision) {
    this.pverRevision = pverRevision;
  }

  /**
   * @return the predecessorCheck
   */
  public boolean isPredecessorCheck() {
    return this.predecessorCheck;
  }


  /**
   * @param predecessorCheck the predecessorCheck to set
   */
  public void setPredecessorCheck(final boolean predecessorCheck) {
    this.predecessorCheck = predecessorCheck;
  }


  /**
   * @return the datafileoption
   */
  public ExcelReportTypeEnum getDatafileoption() {
    if (this.datafileoption != null) {
      return this.datafileoption;
    }
    return ExcelReportTypeEnum.ONEFILECHECK;
  }


  /**
   * @param onefilecheck the datafileoption to set
   */
  public void setDatafileoption(final ExcelReportTypeEnum onefilecheck) {
    this.datafileoption = onefilecheck;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CompliReviewInputMetaData");
    builder.append("[");
    builder.append("webflowID=").append(this.webflowID).append(STR_DELIMITER_CONSTANT);
    builder.append("pverName=").append(this.pverName).append(STR_DELIMITER_CONSTANT);
    builder.append("pverVariant=").append(this.pverVariant).append(STR_DELIMITER_CONSTANT);
    builder.append("a2lFileName=").append(this.a2lFileName).append(STR_DELIMITER_CONSTANT);

    for (Entry<Long, Long> pidcElementEntry : this.hexFilePidcElement.entrySet()) {
      builder.append("pidcElement=").append(pidcElementEntry.getKey()).append(STR_EQUALS_CONSTANT)
          .append(pidcElementEntry.getValue()).append(STR_DELIMITER_CONSTANT);

    }

    for (Entry<Long, String> hexfileEntry : this.hexfileIdxMap.entrySet()) {
      builder.append("Hexfile=").append(hexfileEntry.getKey()).append(STR_EQUALS_CONSTANT)
          .append(hexfileEntry.getValue()).append(STR_DELIMITER_CONSTANT);

    }

    for (Entry<Long, String> dstFiles : this.dstMap.entrySet()) {
      builder.append("dtsfile=").append(dstFiles.getKey()).append(STR_EQUALS_CONSTANT).append(dstFiles.getValue())
          .append(STR_DELIMITER_CONSTANT);

    }
    builder.append("]");
    return builder.toString();
  }


}

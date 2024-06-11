/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrFileData implements IModel, Comparable<EmrFileData> {

  /**
   *
   */
  private static final long serialVersionUID = -8459139120700920154L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;

  /** The fuel type number. */
  private Long fuelTypeNumber;

  /** The value num. */
  private BigDecimal valueNum;

  /** The value text. */
  private String valueText;

  /** category id */
  private Long categoryId;

  /** column id */
  private Long colId;

  /** column val id */
  private Long colValId;

  /** emission standard id1 */
  private Long emissionStdProcedureId;

  /** emission standard id2 */
  private Long emissionStdTestcaseId;

  /** file id */
  private Long fileId;

  /** measure unit id */
  private Long measureUnitId;


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
  public void setId(final Long catId) {
    this.id = catId;
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
   * @return the fuelTypeNumber
   */
  public Long getFuelTypeNumber() {
    return this.fuelTypeNumber;
  }


  /**
   * @param fuelTypeNumber the fuelTypeNumber to set
   */
  public void setFuelTypeNumber(final Long fuelTypeNumber) {
    this.fuelTypeNumber = fuelTypeNumber;
  }


  /**
   * @return the valueNum
   */
  public BigDecimal getValueNum() {
    return this.valueNum;
  }


  /**
   * @param valueNum the valueNum to set
   */
  public void setValueNum(final BigDecimal valueNum) {
    this.valueNum = valueNum;
  }


  /**
   * @return the valueText
   */
  public String getValueText() {
    return this.valueText;
  }


  /**
   * @param valueText the valueText to set
   */
  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }


  /**
   * @return the categoryId
   */
  public Long getCategoryId() {
    return this.categoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(final Long categoryId) {
    this.categoryId = categoryId;
  }


  /**
   * @return the colId
   */
  public Long getColId() {
    return this.colId;
  }


  /**
   * @param colId the colId to set
   */
  public void setColId(final Long colId) {
    this.colId = colId;
  }


  /**
   * @return the colValId
   */
  public Long getColValId() {
    return this.colValId;
  }


  /**
   * @param colValId the colValId to set
   */
  public void setColValId(final Long colValId) {
    this.colValId = colValId;
  }


  /**
   * @return the fileId
   */
  public Long getFileId() {
    return this.fileId;
  }


  /**
   * @param fileId the fileId to set
   */
  public void setFileId(final Long fileId) {
    this.fileId = fileId;
  }


  /**
   * @return the measureUnitId
   */
  public Long getMeasureUnitId() {
    return this.measureUnitId;
  }


  /**
   * @param measureUnitId the measureUnitId to set
   */
  public void setMeasureUnitId(final Long measureUnitId) {
    this.measureUnitId = measureUnitId;
  }


  /**
   * @return the emissionStdProcedureId
   */
  public Long getEmissionStdProcedureId() {
    return this.emissionStdProcedureId;
  }


  /**
   * @param emissionStdProcedureId the emissionStdProcedureId to set
   */
  public void setEmissionStdProcedureId(final Long emissionStdProcedureId) {
    this.emissionStdProcedureId = emissionStdProcedureId;
  }


  /**
   * @return the emissionStdTestcaseId
   */
  public Long getEmissionStdTestcaseId() {
    return this.emissionStdTestcaseId;
  }


  /**
   * @param emissionStdTestcaseId the emissionStdTestcaseId to set
   */
  public void setEmissionStdTestcaseId(final Long emissionStdTestcaseId) {
    this.emissionStdTestcaseId = emissionStdTestcaseId;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((EmrFileData) obj).getId());


    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrFileData o) {

    return ModelUtil.compare(getId(), o.getId());
  }

}

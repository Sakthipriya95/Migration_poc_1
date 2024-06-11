/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrExcelMapping implements IModel, Comparable<EmrExcelMapping> {


  /**
   *
   */
  private static final long serialVersionUID = -4493472368136569976L;


  /** The id. */
  private Long id;


  /** The version. */
  private Long version;

  /** The value in excel. */
  private String valueInExcel;

  /** emr col id. */
  private Long colId;

  /** emr col val id */
  private Long colValId;

  /** emission std id */
  private Long emissionStdId;

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
   * @return the valueInExcel
   */
  public String getValueInExcel() {
    return this.valueInExcel;
  }


  /**
   * @param valueInExcel the valueInExcel to set
   */
  public void setValueInExcel(final String valueInExcel) {
    this.valueInExcel = valueInExcel;
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
   * @return the emissionStdId
   */
  public Long getEmissionStdId() {
    return this.emissionStdId;
  }


  /**
   * @param emissionStdId the emissionStdId to set
   */
  public void setEmissionStdId(final Long emissionStdId) {
    this.emissionStdId = emissionStdId;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrExcelMapping cat) {
    return ModelUtil.compare(getId(), cat.getId());
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
      return ModelUtil.isEqual(getId(), ((EmrExcelMapping) obj).getId());
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

}

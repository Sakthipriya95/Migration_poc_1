/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class RmRiskLevel implements IModel, Comparable<RmRiskLevel> {

  /**
   *
   */
  private static final long serialVersionUID = 6701477886729930965L;


  /**
   * RISK LEVEL Config
   */
  public enum RISK_LEVEL_CONFIG {
                                 /**
                                  * flag HIGH. Risk product less tha or equals to 9
                                  */
                                 RISK_LVL_HIGH("RISK_LVL_HIGH", 9l, 255, 120, 120),
                                 /**
                                  * flag MEDIUM.Risk product less than 9 and greater than or equal to 4
                                  */
                                 RISK_LVL_MEDIUM("RISK_LVL_MEDIUM", 4l, 255, 217, 102),
                                 /**
                                  * flag LOW. Risk product 1
                                  */
                                 RISK_LVL_LOW("RISK_LVL_LOW", 1l, 198, 239, 206),
                                 /**
                                  * flag NA
                                  */
                                 RISK_LVL_NA("RISK_LVL_NA", 0l, 255, 255, 255);

    private final String code;
    private final long riskThreshold;
    private final int r;
    private final int g;
    private final int b;

    RISK_LEVEL_CONFIG(final String code, final long riskThreshold, final int r, final int g, final int b) {
      this.code = code;
      this.riskThreshold = riskThreshold;
      this.r = r;
      this.g = g;
      this.b = b;
    }

    /**
     * @param code risk code
     * @return RISK_LEVEL_COLOR
     */
    public static RISK_LEVEL_CONFIG getType(final String code) {
      for (RISK_LEVEL_CONFIG value : RISK_LEVEL_CONFIG.values()) {
        if (value.code.equalsIgnoreCase(code)) {
          return value;
        }
      }
      return null;
    }

    /**
     * @return level code
     */
    public String getCode() {
      return this.code;
    }

    /**
     * @return the riskThreshold
     */
    public long getRiskThreshold() {
      return this.riskThreshold;
    }

    /**
     * @return red value
     */
    public int getRed() {
      return this.r;
    }

    /**
     * @return green value
     */
    public int getGreen() {
      return this.g;
    }

    /**
     * @return blue value
     */
    public int getBlue() {
      return this.b;
    }
  }

  private Long id;

  private String code;

  private String engName;

  private String gerName;

  private String name;

  private String engDesc;

  private String gerDesc;

  private String desc;

  private Long riskWeight;

  private Long version;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RmRiskLevel riskLvl) {

    return ModelUtil.compare(getName(), riskLvl.getName());
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
      return ModelUtil.isEqual(getName(), ((RmRiskLevel) obj).getName()) &&
          ModelUtil.isEqual(getId(), ((RmRiskLevel) obj).getId());
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
  public Long getId() {

    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long riskLvlId) {
    this.id = riskLvlId;

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
   * @return the engName
   */
  public String getEngName() {
    return this.engName;
  }


  /**
   * @param engName the engName to set
   */
  public void setEngName(final String engName) {
    this.engName = engName;
  }


  /**
   * @return the gerName
   */
  public String getGerName() {
    return this.gerName;
  }


  /**
   * @param gerName the gerName to set
   */
  public void setGerName(final String gerName) {
    this.gerName = gerName;
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
   * @return the engDesc
   */
  public String getEngDesc() {
    return this.engDesc;
  }


  /**
   * @param engDesc the engDesc to set
   */
  public void setEngDesc(final String engDesc) {
    this.engDesc = engDesc;
  }


  /**
   * @return the gerDesc
   */
  public String getGerDesc() {
    return this.gerDesc;
  }


  /**
   * @param gerDesc the gerDesc to set
   */
  public void setGerDesc(final String gerDesc) {
    this.gerDesc = gerDesc;
  }


  /**
   * @return the desc
   */
  public String getDesc() {
    return this.desc;
  }


  /**
   * @param desc the desc to set
   */
  public void setDesc(final String desc) {
    this.desc = desc;
  }


  /**
   * @return the riskWeight
   */
  public Long getRiskWeight() {
    return this.riskWeight;
  }


  /**
   * @param riskWeight the riskWeight to set
   */
  public void setRiskWeight(final Long riskWeight) {
    this.riskWeight = riskWeight;
  }


  /**
   * @return the code
   */
  public String getCode() {
    return this.code;
  }


  /**
   * @param code the code to set
   */
  public void setCode(final String code) {
    this.code = code;
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel.RISK_LEVEL_CONFIG;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * The Class RmCategory.
 *
 * @author rgo7cob
 */
public class RmCategory implements IModel, Comparable<RmCategory> {


  /**
   *
   */
  private static final long serialVersionUID = 5369959535413508772L;


  /**
   * new enum for Category Value.
   *
   * @author rgo7cob
   */
  public enum CATEGORY_VALUE {
                              /** Category type with impact and risk. */
                              RB_SW_SHARE_100("RB_SW_100", "100%", RISK_LEVEL_CONFIG.RISK_LVL_LOW.getCode()),

                              /** Category type with impact no risk. */
                              RB_SW_SAHRE_X("RB_SW_X", "x%", RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getCode()),

                              /** Category Type Shape. */
                              RB_SW_SHARE_0("RB_SW_0", "0%", RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getCode()),

                              /** Category type data. */
                              INPUT_DATA_BY_RB(
                                               "RB_INPUT_YES",
                                               "Initial Data by RB",
                                               RISK_LEVEL_CONFIG.RISK_LVL_LOW.getCode()),

                              /** Category type data. risk level is medium with the input not from RB */
                              INPUT_DATA_NOT_BY_RB(
                                                   "RB_INPUT_NO",
                                                   "Initial Data not by RB",
                                                   RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getCode()),

                              /** Category type data. risk level is high with the third state */
                              THIRD_STATE_RB(
                                             "RB_INPUT_THIRD_STATE",
                                             "New third state",
                                             RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getCode());

    /** The ui type. */
    private final String dbCode;

    /** The db type */
    private final String value;

    /** The risk type */
    private final String riskConfig;

    /**
     * Instantiates a new category value.
     *
     * @param uiType the dbCode
     * @param db type
     * @param riskConfig risk type
     */
    CATEGORY_VALUE(final String dbCode, final String value, final String riskConfig) {
      this.dbCode = dbCode;
      this.value = value;
      this.riskConfig = riskConfig;
    }


    /**
     * Gets the category value.
     *
     * @param code in data base
     * @return the ui value of the given category
     */
    public static String getCategoryValue(final String code) {
      for (CATEGORY_VALUE value : CATEGORY_VALUE.values()) {
        if (value.dbCode.equals(code)) {
          return value.value;
        }
      }
      return "";

    }

    /**
     * Gets the category risk.
     *
     * @param code db code category
     * @return the ui value of the given category
     */
    public static String getRiskConfig(final String code) {
      for (CATEGORY_VALUE value : CATEGORY_VALUE.values()) {
        if (value.dbCode.equals(code)) {
          return value.riskConfig;
        }
      }
      return "";
    }
  }


  /**
   * CDReview Participant types.
   */
  public enum CATEGORY_TYPE {

                             /** Category type with impact and risk. */
                             IMPACTED_WITH_RISK("I"),

                             /** Category type with impact no risk. */
                             IMPACT_WITHOUT_RISK("N"),

                             /** Category Type Shape. */
                             SHARE("S"),

                             /** Category type data. */
                             DATA("D");

    /** The db type. */
    final String dbType;


    /**
     * Instantiates a new category type.
     *
     * @param dbType the db type
     */
    CATEGORY_TYPE(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * Gets the db type.
     *
     * @return ParticipantType
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Return the type object for the given db type.
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static CATEGORY_TYPE getType(final String dbType) {
      for (CATEGORY_TYPE type : CATEGORY_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }


  /** The id. */
  private Long id;

  /** The code. */
  private String code;

  /** The name eng. */
  private String nameEng;

  /** The name ger. */
  private String nameGer;

  /** The name. */
  private String name;

  /** The category type. */
  private CATEGORY_TYPE categoryType;

  /** The version. */
  private Long version;

  /** The value. */
  private String value;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RmCategory cat) {
    /**
     * Sorting by ID to ensure Order in forming Nattable header
     */
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
      return ModelUtil.isEqual(getId(), ((RmCategory) obj).getId());
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
   * Gets the name eng.
   *
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * Sets the name eng.
   *
   * @param nameEng the nameEng to set
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * Sets the name.
   *
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the category type.
   *
   * @return the categoryType
   */
  public CATEGORY_TYPE getCategoryType() {
    return this.categoryType;
  }


  /**
   * Sets the category type.
   *
   * @param categoryType the categoryType to set
   */
  public void setCategoryType(final CATEGORY_TYPE categoryType) {
    this.categoryType = categoryType;
  }

  /**
   * Gets the German name.
   *
   * @return the value
   */
  public String getNameGer() {
    return this.nameGer;
  }

  /**
   * Sets the name ger.
   *
   * @param nameGer the nameGer to set
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value value
   */
  public void setValue(final String value) {
    this.value = value;
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

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class PidcRmProjCharacter implements IModel, Comparable<PidcRmProjCharacter> {

  /**
   *
   */
  private static final long serialVersionUID = -8040440308793494889L;


  /**
   * Relevant value (Y, N, NA)
   */
  public enum RELEVANT_TYPE {
                             /**
                              * Yes
                              */
                             Y("Y"),
                             /**
                              * No
                              */
                             N("N"),
                             /**
                              * NA - Not Applicable
                              */
                             NA("NA");

    private final String uiType;

    RELEVANT_TYPE(final String uiType) {
      this.uiType = uiType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the risk flag object for the given db type
     *
     * @param uiType ui type
     * @return the flag object
     */
    public static RELEVANT_TYPE getType(final String uiType) {
      for (RELEVANT_TYPE type : RELEVANT_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return RELEVANT_TYPE.NA;
    }
  }

  private Long id;

  private Long version;

  private Long rmDefId;

  private Long projCharId;

  private String relevant;

  private Long rbShareId;

  private Long rbDataId;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcRmProjCharacter obj) {

    return obj.getId().compareTo(this.id);
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
      return ModelUtil.isEqual(getId(), ((PidcRmProjCharacter) obj).getId());
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
  public void setId(final Long objId) {
    this.id = objId;

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
   * @return the rbShareId
   */
  public Long getRbShareId() {
    return this.rbShareId;
  }


  /**
   * @param rbShareId the rbShareId to set
   */
  public void setRbShareId(final Long rbShareId) {
    this.rbShareId = rbShareId;
  }


  /**
   * @return the rbDataId
   */
  public Long getRbDataId() {
    return this.rbDataId;
  }


  /**
   * @param rbDataId the rbDataId to set
   */
  public void setRbDataId(final Long rbDataId) {
    this.rbDataId = rbDataId;
  }


  /**
   * @return the rmDefId
   */
  public Long getRmDefId() {
    return this.rmDefId;
  }


  /**
   * @param rmDefId the rmDefId to set
   */
  public void setRmDefId(final Long rmDefId) {
    this.rmDefId = rmDefId;
  }


  /**
   * @return the projCharId
   */
  public Long getProjCharId() {
    return this.projCharId;
  }


  /**
   * @param projCharId the projCharId to set
   */
  public void setProjCharId(final Long projCharId) {
    this.projCharId = projCharId;
  }


  /**
   * @return the relevant
   */
  public String getRelevant() {
    return this.relevant;
  }


  /**
   * @param relevant the relevant to set
   */
  public void setRelevant(final String relevant) {
    this.relevant = relevant;
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter.RELEVANT_TYPE;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author gge6cob
 */
/**
 * The Class PidcRMCharacterMapping.
 *
 * @author gge6cob
 */
public class PidcRMCharacterMapping implements Comparable<PidcRMCharacterMapping>, Cloneable {

  /**
   * The Enum SortColumn.
   */
  public static enum SortColumn {

                                 /** sort column for name. */
                                 CHARACTER(0),

                                 /** sort column for isRelevant : Yes. */
                                 IS_RELEVANT_YES(1),

                                 /** sort column for isRelevant : No. */
                                 IS_RELEVANT_NO(2),

                                 /** sort column for isRelevant : NA. */
                                 IS_RELEVANT_NA(3),
                                 /** sort column for rbSoftwareShare. */
                                 RB_SHARE(4),
                                 /** sort column for rbInitialData. */
                                 RB_INIT_DATA(5);

    /** The col index. */
    int colIndex;

    /**
     * Instantiates a new sort column.
     *
     * @param colIndex the col index
     */
    SortColumn(final int colIndex) {
      this.colIndex = colIndex;
    }

    /**
     * Gets the type.
     *
     * @param colIndex the col index
     * @return the type
     */
    public static SortColumn getType(final int colIndex) {
      for (SortColumn type : SortColumn.values()) {
        if (type.colIndex == colIndex) {
          return type;
        }
      }
      return CHARACTER;
    }
  }

  /** The pidc proj char id. */
  private long pidcProjCharId;

  /** The proj char id. */
  private long projCharId;

  /** The parent proj char id. */
  private long parentProjCharId = 0l;

  /** The risk def id. */
  private long riskDefId;

  /** The risk char id. */
  private long riskCharId;

  /** The project character. */
  private String projectCharacter;

  /** The version. */
  private Long version = 1l;

  /** The is relevant. */
  private boolean isRelevantYes = false;

  /** The is relevant No. */
  private boolean isRelevantNo = false;

  /** The is relevant NA. */
  private boolean isRelevantNA = true;

  /** The rb software share. */
  private String rbSoftwareShare;

  /** The rb initial data. */
  private String rbInputData;

  /** The rb input data - category id. */
  private long rbInputDataId;

  /** The risk impact map. */
  private Map<Integer, String> riskImpactMap = new HashMap<>();

  /** Map to hold risk id of the corresponding column. */
  private Map<Integer, Long> colIndexRiskIdMap = new HashMap<>();

  /** The pidc rm char. */
  private PidcRmProjCharacter pidcRmChar;

  /** The relevant char. */
  private String relevantChar = null;

  private Map<Long, RmCategory> allRBInputDataCatMap = null;

  private Map<Long, RmCategory> allRBSwShareCatgMap = null;

  boolean visible = true;

  /**
   * @return the allRBInputDataMap
   */
  public Map<Long, RmCategory> getAllRBInputDataMap() {
    return this.allRBInputDataCatMap;
  }


  /**
   * Gets the proj char id.
   *
   * @return the projCharId
   */
  public long getProjCharId() {
    return this.projCharId;
  }


  /**
   * Sets the proj char id.
   *
   * @param projCharId the projCharId to set
   */
  public void setProjCharId(final long projCharId) {
    this.projCharId = projCharId;
  }


  /**
   * Gets the risk def id.
   *
   * @return the riskDefId
   */
  public long getRiskDefId() {
    return this.riskDefId;
  }


  /**
   * Sets the risk def id.
   *
   * @param riskDefId the riskDefId to set
   */
  public void setRiskDefId(final long riskDefId) {
    this.riskDefId = riskDefId;
  }


  /**
   * Gets the risk char id.
   *
   * @return the riskCharId
   */
  public long getRiskCharId() {
    return this.riskCharId;
  }


  /**
   * Sets the risk char id.
   *
   * @param riskCharId the riskCharId to set
   */
  public void setRiskCharId(final long riskCharId) {
    this.riskCharId = riskCharId;
  }


  /**
   * Gets the project character.
   *
   * @return the projectCharacter
   */
  public String getProjectCharacter() {
    return this.projectCharacter;
  }


  /**
   * Sets the project character.
   *
   * @param projectCharacter the projectCharacter to set
   */
  public void setProjectCharacter(final String projectCharacter) {
    this.projectCharacter = projectCharacter;
  }

  /**
   * Compare to.
   *
   * @param input1 the input 1
   * @param input2 the input 2
   * @param sortCol the sort col
   * @param columnNum the column num
   * @return the int
   */
  public static int compareTo(final PidcRMCharacterMapping input1, final PidcRMCharacterMapping input2,
      final SortColumn sortCol) {
    int ret;
    switch (sortCol) {
      case CHARACTER:
        ret = ApicUtil.compare(input1.getProjectCharacter(), input2.getProjectCharacter());
        break;
      case IS_RELEVANT_YES:
        ret = ApicUtil.compare(input1.isRelevantYes(), input2.isRelevantYes());
        break;
      case IS_RELEVANT_NO:
        ret = ApicUtil.compare(input1.isRelevantNo(), input2.isRelevantNo());
        break;
      case IS_RELEVANT_NA:
        ret = ApicUtil.compare(input1.isRelevantNA(), input2.isRelevantNA());
        break;
      case RB_SHARE:
        ret = ApicUtil.compare(input1.getRbSoftwareShare(), input2.getRbSoftwareShare());
        break;
      case RB_INIT_DATA:
        ret = ApicUtil.compare(input1.getInputDataByRB(), input2.getInputDataByRB());
        break;
      default:
        ret = ApicUtil.compare(input1.getProjectCharacter(), input2.getProjectCharacter());
        break;
    }
    return ret;
  }


  /**
   * Gets the risk impact map.
   *
   * @return the riskImpactMap
   */
  public Map<Integer, String> getRiskImpactMap() {
    if (this.isRelevantYes) {
      return this.riskImpactMap;
    }
    return null;
  }

  /**
   * Sets the risk impact map.
   *
   * @param riskImpactMap the risk impact map
   */
  public void setRiskImpactMap(final Map<Integer, String> riskImpactMap) {
    this.riskImpactMap = riskImpactMap;
  }

  /**
   * Checks if is relevant yes.
   *
   * @return the isRelevantYes
   */
  public boolean isRelevantYes() {
    return this.isRelevantYes;
  }

  /**
   * Sets the relevant yes.
   *
   * @param isRelevantYes the isRelevantYes to set
   */
  public void setRelevantYes(final boolean isRelevantYes) {
    this.isRelevantYes = isRelevantYes;
    if (isRelevantYes) {
      this.relevantChar = RELEVANT_TYPE.Y.getUiType();
      setRelevantNo(false);
      setRelevantNA(false);
    }
  }

  /**
   * Checks if is relevant no.
   *
   * @return the isRelevantNo
   */
  public boolean isRelevantNo() {
    return this.isRelevantNo;
  }

  /**
   * Sets the relevant no.
   *
   * @param isRelevantNo the isRelevantNo to set
   */
  public void setRelevantNo(final boolean isRelevantNo) {
    this.isRelevantNo = isRelevantNo;
    if (isRelevantNo) {
      this.relevantChar = RELEVANT_TYPE.N.getUiType();
      setRelevantYes(false);
      setRelevantNA(false);
    }
  }

  /**
   * Checks if is relevant NA.
   *
   * @return the isRelevantNA
   */
  public boolean isRelevantNA() {
    return this.isRelevantNA;
  }

  /**
   * Sets the relevant NA.
   *
   * @param isRelevantNA the isRelevantNA to set
   */
  public void setRelevantNA(final boolean isRelevantNA) {
    this.isRelevantNA = isRelevantNA;
    if (isRelevantNA) {
      this.relevantChar = CommonUtilConstants.EMPTY_STRING;
      setRelevantYes(false);
      setRelevantNo(false);
    }
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
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getProjCharId(), ((PidcRMCharacterMapping) obj).getProjCharId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getProjCharId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcRMCharacterMapping compareRowObj) {
    return ApicUtil.compare(this.projCharId, compareRowObj.projCharId);
  }


  /**
   * Gets the col index risk id map.
   *
   * @return the colIndexRiskIdMap
   */
  public Map<Integer, Long> getColIndexRiskIdMap() {
    return this.colIndexRiskIdMap;
  }


  /**
   * Sets the col index risk id map.
   *
   * @param colIndexRiskIdMap the colIndexRiskIdMap to set
   */
  public void setColIndexRiskIdMap(final Map<Integer, Long> colIndexRiskIdMap) {
    this.colIndexRiskIdMap = colIndexRiskIdMap;
  }


  /**
   * Gets the pidc proj char id.
   *
   * @return the pidcProjCharId
   */
  public long getPidcProjCharId() {
    if (this.pidcRmChar != null) {
      return this.pidcRmChar.getId();
    }
    return this.pidcProjCharId;
  }


  /**
   * Sets the pidc proj char id.
   *
   * @param pidcProjCharId the pidcProjCharId to set
   */
  public void setPidcProjCharId(final long pidcProjCharId) {
    this.pidcProjCharId = pidcProjCharId;
  }


  /**
   * Gets the parent proj char id.
   *
   * @return the parentProjCharId
   */
  public long getParentProjCharId() {
    return this.parentProjCharId;
  }

  /**
   * Sets the parent proj char id.
   *
   * @param parentProjCharId the parentProjCharId to set
   */
  public void setParentProjCharId(final long parentProjCharId) {
    this.parentProjCharId = parentProjCharId;
  }


  /**
   * Sets the pidc rm char.
   *
   * @param pidcRmChar the new pidc rm char
   */
  public void setPidcRmChar(final PidcRmProjCharacter pidcRmChar) {
    this.pidcRmChar = pidcRmChar;
  }


  /**
   * Sets the relevant flags.
   *
   * @param relevantStr the new relevant flags
   */
  public void setRelevantFlags(final String relevantStr) {
    // Reset all
    setRelevantYes(false);
    setRelevantNA(false);
    setRelevantNo(false);

    PidcRmProjCharacter.RELEVANT_TYPE relevant = RELEVANT_TYPE.getType(relevantStr);
    if (relevant.equals(RELEVANT_TYPE.Y)) {
      setRelevantYes(true);
    }
    else if (relevant.equals(RELEVANT_TYPE.N)) {
      setRelevantNo(true);
    }
    else {
      setRelevantNA(true);
    }
  }


  /**
   * Gets the relevant string.
   *
   * @return the relevant string
   */
  public String getRelevantFlag() {
    return this.relevantChar;
  }


  /**
   * @return the isInputDataByRB
   */

  public String getInputDataByRB() {
    if (this.rbInputDataId != 0l) {
      return this.allRBInputDataCatMap.get(this.rbInputDataId).getName();
    }
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * @param inputDataByRB String value
   */
  public void setInputDataByRB(final String inputDataByRB) {
    this.rbInputData = inputDataByRB;
    if (this.allRBInputDataCatMap != null) {
      for (long key : this.allRBInputDataCatMap.keySet()) {
        String data = this.allRBInputDataCatMap.get(key).getName();
        if (data.equals(inputDataByRB)) {
          this.rbInputDataId = key;
        } // default setting
        else if (inputDataByRB == null) {
          this.rbInputData = data;
          this.rbInputDataId = key;
          break;
        }

      }
    }
  }


  /**
   * @return the rbInputDataId
   */
  public long getRbInputDataId() {
    return this.rbInputDataId;
  }


  /**
   * @param rbInputDataId the rbInputDataId to set
   */
  public void setRbInputDataId(final long rbInputDataId) {
    this.rbInputDataId = rbInputDataId;
    this.rbInputData = this.allRBInputDataCatMap.get(rbInputDataId).getName();
  }


  /**
   * @param allRBInputDataCatMap
   */
  public void setAllRBInputDataCatgMap(final Map<Long, RmCategory> allRBInputDataCatMap) {
    this.allRBInputDataCatMap = allRBInputDataCatMap;
    setInputDataByRB(this.rbInputData);
  }

  /**
   * @param allRBSwShareCatgMap the allRBShareCategoryMap to set
   */
  public void setAllRBShareCatgMap(final Map<Long, RmCategory> allRBSwShareCatgMap) {
    this.allRBSwShareCatgMap = allRBSwShareCatgMap;
    if (this.allRBSwShareCatgMap != null) {
      for (long key : this.allRBSwShareCatgMap.keySet()) {
        this.rbSoftwareShare = this.allRBSwShareCatgMap.get(key).getValue();
        break;
      }
    }
  }

  /**
   * @return the rbShareId
   */
  public long getRbShareId() {
    if (this.allRBSwShareCatgMap != null) {
      for (long key : this.allRBSwShareCatgMap.keySet()) {
        String data = this.allRBSwShareCatgMap.get(key).getValue();
        if (data.equals(this.rbSoftwareShare)) {
          return key;
        }
      }
    }
    return 0;
  }


  /**
   * @param rbShareId the rbShareId to set
   */
  public void setRbShareId(final long rbShareId) {
    this.rbSoftwareShare = this.allRBSwShareCatgMap.get(rbShareId).getValue();
  }


  /**
   * Gets the rb software share.
   *
   * @return the rbSoftwareShare
   */
  public String getRbSoftwareShare() {
    return this.rbSoftwareShare;
  }

  /**
   * Sets the rb software share.
   *
   * @param rbSoftwareShare the rbSoftwareShare to set
   */
  public void setRbSoftwareShare(final String rbSoftwareShare) {
    this.rbSoftwareShare = rbSoftwareShare;
  }

  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }

  @Override
  public PidcRMCharacterMapping clone() {
    PidcRMCharacterMapping rmCharMapping = null;
    try {
      rmCharMapping = (PidcRMCharacterMapping) super.clone();
    }
    catch (CloneNotSupportedException e) {
      CDMLogger.getInstance()
          .error("Error in cloning PidcRMCharacterMapping for updating row object during webservice call", e);
    }
    return rmCharMapping;
  }


  /**
   * @return the visible
   */
  public boolean isVisible() {
    return this.visible;
  }


  /**
   * @param visible the visible to set
   */
  public void setVisible(final boolean visible) {
    this.visible = visible;
  }


}

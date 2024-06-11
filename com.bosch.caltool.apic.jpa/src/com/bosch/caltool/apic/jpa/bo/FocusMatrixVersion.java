/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author bne4cob
 */
// ICDM-2569
public class FocusMatrixVersion extends ApicObject implements Comparable<FocusMatrixVersion> {

  /**
   * Focus Matrix review Status
   */
  public static enum FM_REVIEW_STATUS {
                                       /**
                                        * Yes
                                        */
                                       YES("Y"),
                                       /**
                                        * No
                                        */
                                       NO("N"),
                                       /**
                                        * Not defined
                                        */
                                       NOT_DEFINED("");

    /**
     * Status of review
     */
    public final String status;

    FM_REVIEW_STATUS(final String type) {
      this.status = type;
    }

    /**
     * Return the FM review object for the given db type
     *
     * @param status db literal of type
     * @return the file type object
     */
    public static FM_REVIEW_STATUS getStatus(final String status) {
      for (FM_REVIEW_STATUS type : FM_REVIEW_STATUS.values()) {
        if (type.getStatusStr().equals(status)) {
          return type;
        }
      }
      return NOT_DEFINED;
    }

    /**
     * @return the type
     */
    public String getStatusStr() {
      return this.status;
    }
  }

  /**
   * Focus Matrix version Status
   */
  public static enum FM_VERS_STATUS {
                                     /**
                                      * Working set version
                                      */
                                     WORKING_SET("W"),
                                     /**
                                      * Old version(Archived)
                                      */
                                     OLD("O");

    /**
     * Status of version
     */
    public final String status;

    FM_VERS_STATUS(final String code) {
      this.status = code;
    }

    /**
     * Return the enum object for the given db type
     *
     * @param status db literal of type
     * @return the file type object
     */
    public static FM_VERS_STATUS getStatus(final String status) {
      for (FM_VERS_STATUS code : FM_VERS_STATUS.values()) {
        if (code.getDbStatus().equals(status)) {
          return code;
        }
      }
      return WORKING_SET;
    }

    /**
     * @return the db status code
     */
    public String getDbStatus() {
      return this.status;
    }
  }

  /**
   * If true, focus matrix children are loaded
   */
  private boolean focusMatrixDefLoaded;

  /**
   * Defined focus matrix items of this PIDC
   * <p>
   * Key - Attribute ID <br>
   * Value - Map of &lt;UC Item ID, Focus Matrix Definition Item&gt;
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, FocusMatrixDetails>> projFocusMatrixDefMap =
      new ConcurrentHashMap<>();

  /**
   * @param apicDataProvider ApicDataProvider instance
   * @param fmVersID primary key
   */
  protected FocusMatrixVersion(final ApicDataProvider apicDataProvider, final Long fmVersID) {
    super(apicDataProvider, fmVersID);
    getDataCache().addRemoveFocusMatrixVersion(this, true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    if (isWorkingSet()) {
      return getVersionName();
    }
    StringBuilder name = new StringBuilder();
    name.append(getRevisionNumber()).append(". ")
        .append(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_04, getCreatedDate()));
    if (!CommonUtils.isEmptyString(getVersionName())) {
      name.append(" - ").append(getVersionName());
    }
    return name.toString();
  }

  /**
   * @return name of this version
   */
  public String getVersionName() {
    return getEntityProvider().getDbFocuMatrixVersion(getID()).getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    return isWorkingSet() && getPidcVersion().canModifyFocusMatrix();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Description is not applicable
    return null;
  }

  /**
   * @return the PIDC Version
   */
  public PIDCVersion getPidcVersion() {
    return ((ApicDataProvider) getDataProvider())
        .getPidcVersion(getEntityProvider().getDbFocuMatrixVersion(getID()).getTPidcVersion().getPidcVersId());
  }

  /**
   * @return status of the FM version
   */
  public FM_VERS_STATUS getVersionStatus() {
    return FM_VERS_STATUS.getStatus(getEntityProvider().getDbFocuMatrixVersion(getID()).getStatus());
  }

  /**
   * @return revision number of this record
   */
  public Long getRevisionNumber() {
    return getEntityProvider().getDbFocuMatrixVersion(getID()).getRevNumber();
  }

  /**
   * @return the Reviewed By user id
   */
  public ApicUser getReviewedBy() {
    return null == getEntityProvider().getDbFocuMatrixVersion(getID()).getReviewedUser() ? null
        : ((ApicDataProvider) getDataProvider())
            .getApicUser(getEntityProvider().getDbFocuMatrixVersion(getID()).getReviewedUser().getUsername());
  }

  /**
   * @return the Reviewed By user id
   */
  public Calendar getReviewedOn() {
    return null == getEntityProvider().getDbFocuMatrixVersion(getID()) ? null
        : ApicUtil.timestamp2calendar(getEntityProvider().getDbFocuMatrixVersion(getID()).getReviewedDate());
  }


  /**
   * @return the link
   */
  public String getLink() {
    return null == getEntityProvider().getDbFocuMatrixVersion(getID()) ? ApicConstants.EMPTY_STRING
        : getEntityProvider().getDbFocuMatrixVersion(getID()).getLink();
  }

  /**
   * @return the comments
   */
  public String getRemarks() {
    return null == getEntityProvider().getDbFocuMatrixVersion(getID()) ? ApicConstants.EMPTY_STRING
        : getEntityProvider().getDbFocuMatrixVersion(getID()).getRemark();
  }

  /**
   * @return the comments
   */
  public FM_REVIEW_STATUS getReviewStatus() {
    return FM_REVIEW_STATUS.getStatus(getEntityProvider().getDbFocuMatrixVersion(getID()).getRvwStatus());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.FOCUS_MATRIX_VERSION;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbFocuMatrixVersion(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFocuMatrixVersion(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFocuMatrixVersion(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFocuMatrixVersion(getID()).getModifiedDate());
  }

  private FocusMatrix focusMatrix;

  /**
   * @return Focus Matrix definition
   */
  public FocusMatrix getFocusMatrix() {
    if (this.focusMatrix == null) {
      this.focusMatrix = new FocusMatrix((ApicDataProvider) getDataProvider(), this);
    }
    return this.focusMatrix;
  }

  /**
   * @return map of focus matrix definitions of this PIDC <br>
   *         Key - Attribute ID <br>
   *         Value - Map of &lt;UC Item ID, Focus Matrix Definition Item&gt;
   */
  protected Map<Long, ConcurrentMap<Long, FocusMatrixDetails>> getFocusMatrixItemMap() {

    if (!this.focusMatrixDefLoaded) {

      getLogger().debug("Loading focus matrix definition for FM version - {}", getID());

      this.projFocusMatrixDefMap.clear();

      Long attrID;
      Long ucItemID;
      ConcurrentMap<Long, FocusMatrixDetails> childMap;


      for (TFocusMatrix tFocusMatrix : getEntityProvider().getDbFocuMatrixVersion(getID()).getTFocusMatrixs()) {

        attrID = tFocusMatrix.getTabvAttribute().getAttrId();
        childMap = this.projFocusMatrixDefMap.get(attrID);
        if (childMap == null) {
          childMap = new ConcurrentHashMap<>();
          this.projFocusMatrixDefMap.put(attrID, childMap);
        }
        // Here it is assumed that primary key is unique across use case ID and uc section id.
        if (tFocusMatrix.getTabvUseCaseSection() == null) {
          ucItemID = tFocusMatrix.getTabvUseCas().getUseCaseId();
        }
        else {
          ucItemID = tFocusMatrix.getTabvUseCaseSection().getSectionId();
        }

        FocusMatrixDetails fmDetail = getDataCache().getFocusMatrix(tFocusMatrix.getFmId());
        childMap.put(ucItemID, fmDetail);
      }

      this.focusMatrixDefLoaded = true;

      getLogger().debug("Focus matrix definition loading completed for FM version - {}", getID());

    }

    return this.projFocusMatrixDefMap;
  }

  /**
   * @return the focusMatrixDefinitionLoaded status
   */
  public boolean isFocusMatrixDefinitionLoaded() {
    return this.focusMatrixDefLoaded;
  }

  /**
   * Resets focus matrix definitions loaded in this class
   */
  public void resetFocusMatrixDefinitionLoaded() {
    this.focusMatrixDefLoaded = false;
  }


  /**
   * @return true if this is a working set version
   */
  public boolean isWorkingSet() {
    return getVersionStatus() == FM_VERS_STATUS.WORKING_SET;
  }

  /**
   * Map of focus matrix version attributes, if focus matrix version is an older version.
   * <p>
   * Note : map is empty if this version is working set
   * <p>
   * Key - Attribute ID <br>
   * Value - BO
   */
  private final ConcurrentMap<Long, Set<FocusMatrixVersionAttr>> fmVersAttrMap = new ConcurrentHashMap<>();

  /**
   * Map of focus matrix version attributes, if focus matrix version is an older version.
   * <p>
   * Note : map is empty if this version is working set
   * <p>
   * Key - Attribute ID <br>
   * Value - set of BOs
   *
   * @return map
   */
  Map<Long, Set<FocusMatrixVersionAttr>> getFocusMatrixVersionAttrMap() {

    if (this.fmVersAttrMap.isEmpty()) {

      for (TFocusMatrixVersionAttr dbFmVersAttr : getEntityObj().getTFocusMatrixVersionAttrs()) {
        long attrID = dbFmVersAttr.getTabvAttribute().getAttrId();
        Set<FocusMatrixVersionAttr> fmVersAttrSet = this.fmVersAttrMap.get(attrID);
        if (fmVersAttrSet == null) {
          fmVersAttrSet = new HashSet<>();
          this.fmVersAttrMap.put(attrID, fmVersAttrSet);
        }
        fmVersAttrSet
            .add(new FocusMatrixVersionAttr((ApicDataProvider) getDataProvider(), dbFmVersAttr.getFmvAttrId()));

      }
    }
    return this.fmVersAttrMap;
  }

  /**
   * @return entity object TFocusMatrixVersionAttr
   */
  private TFocusMatrixVersion getEntityObj() {
    return getEntityProvider().getDbFocuMatrixVersion(getID());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrixVersion other) {
    long thisRevNo = getRevisionNumber();
    if (!isWorkingSet()) {
      thisRevNo = 100000000 - thisRevNo;
    }

    long otherRevNo = other.getRevisionNumber();
    if (!other.isWorkingSet()) {
      otherRevNo = 100000000 - otherRevNo;
    }

    int compareResult = ApicUtil.compare(thisRevNo, otherRevNo);

    if (compareResult == 0) {
      compareResult = ApicUtil.compare(getID(), other.getID());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}

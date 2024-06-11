/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * FocusMatrixDetails.java, This class is the business object of the FOCUS_MATRIX entity
 *
 * @author dmo5cob
 */

public class FocusMatrixDetails extends ApicObject {

  /**
   * Initial size of tooltip SB
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;
  private static final String FLD_FM_COLOR = "FM_COLOR";
  private static final String FLD_FM_COMMENTS = "FM_COMMENTS";
  private static final String FLD_FM_LINK = "FM_LINK";

  /**
   * @param apicDataProvider ApicDataProvider instance
   * @param fmId primary key
   */
  protected FocusMatrixDetails(final ApicDataProvider apicDataProvider, final Long fmId) {
    super(apicDataProvider, fmId);
  }


  /**
   * @return Focus Matrix version
   */
  // ICDM-2569
  public FocusMatrixVersion getFocusMatrixVersion() {
    return getDataCache()
        .getFocusMatrixVersion(getEntityProvider().getDbFocuMatrix(getID()).getTFocusMatrixVersion().getFmVersId());
  }

  /**
   * @return the ucpaId
   */
  public Long getUcpaId() {
    return null == getEntityProvider().getDbFocuMatrix(getID()).getTabvUcpAttr() ? null
        : getEntityProvider().getDbFocuMatrix(getID()).getTabvUcpAttr().getUcpaId();
  }

  /**
   * @return the use case Id
   */
  public Long getUseCaseId() {
    return null == getEntityProvider().getDbFocuMatrix(getID()).getTabvUseCas() ? null
        : getEntityProvider().getDbFocuMatrix(getID()).getTabvUseCas().getUseCaseId();
  }

  /**
   * @return the usecasesection Id
   */
  public Long getUseCaseSectionId() {
    return null == getEntityProvider().getDbFocuMatrix(getID()).getTabvUseCaseSection() ? null
        : getEntityProvider().getDbFocuMatrix(getID()).getTabvUseCaseSection().getSectionId();
  }

  /**
   * @return the attrs Id
   */
  public Long getAttributeId() {
    return getEntityProvider().getDbFocuMatrix(getID()).getTabvAttribute().getAttrId();
  }

  /**
   * @return the colorCode
   */
  public FocusMatrixColorCode getColorCode() {
    return FocusMatrixColorCode.getColor(getEntityProvider().getDbFocuMatrix(getID()).getColorCode());
  }

  /**
   * @return the comments
   */
  public String getComments() {
    return getEntityProvider().getDbFocuMatrix(getID()).getComments();
  }

  /**
   * @return the comments
   */
  public boolean isDeleted() {
    return ApicConstants.YES.equals(getEntityProvider().getDbFocuMatrix(getID()).getDeletedFlag()) ? true : false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.FOCUS_MATRIX;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbFocuMatrix(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFocuMatrix(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFocuMatrix(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFocuMatrix(getID()).getModifiedDate());
  }


  /**
   * ICDM-1641
   *
   * @return link
   */
  public String getLink() {
    return getEntityProvider().getDbFocuMatrix(getID()).getLink();
  }

  /**
   * @return the use case item
   */
  public AbstractUseCaseItem getUseCaseItem() {
    AbstractUseCaseItem ret;
    if (getUseCaseSectionId() == null) {
      ret = getDataCache().getUseCase(getUseCaseId());
    }
    else {
      ret = getDataCache().getUseCaseSection(getUseCaseSectionId());

    }

    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);

    toolTip.append("Attribute : ").append(getDataCache().getAttribute(getAttributeId()).getName())
        .append("\nUse Case Item : ").append(getUseCaseItem().getName()).append("\nColor Code : ")
        .append(getColorCode().getDisplayColorTxt());

    if (!CommonUtils.isNull(getLink())) {
      toolTip.append("\nLink : ").append(CommonUtils.checkNull(getLink()));
    }

    toolTip.append("\nMatrix Definition Level : ")
        .append(isLocalMapping() ? ApicConstants.FMD_LEVEL_LOCAL : ApicConstants.FMD_LEVEL_GLOBAL);

    if (!CommonUtils.isNull(getComments())) {
      toolTip.append("\nComments : ").append(getComments());
    }

    return toolTip.toString();
  }

  /**
   * Returns whether the focus matrix definition is configured locally or against the global use use attribute mapping
   *
   * @return true, if mapping is local
   */
  public boolean isLocalMapping() {
    return null == getEntityProvider().getDbFocuMatrix(getID()).getTabvUcpAttr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    objDetails.put(FLD_FM_COLOR, getColorCode().getColor());
    objDetails.put(FLD_FM_COMMENTS, getComments());
    objDetails.put(FLD_FM_LINK, getLink());


    return objDetails;
  }
}

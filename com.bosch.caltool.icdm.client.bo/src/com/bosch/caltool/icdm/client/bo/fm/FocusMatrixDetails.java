/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;


/**
 * FocusMatrixDetails.java, This class is the business object of the FOCUS_MATRIX entity
 *
 * @author dmo5cob
 */

public class FocusMatrixDetails implements IDataObject {

  /**
   * Initial size of tooltip SB
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;
  /**
   * FocusMatrix
   */
  private final FocusMatrix focusMatrix;
  /**
   * FocusMatrixVersion
   */
  private final FocusMatrixVersion fmVersion;
  /**
   * FocusMatrixDataHandler
   */
  private final FocusMatrixDataHandler fmDataHandler;

  /**
   * @param focusMatrix FocusMatrix
   * @param fmVersion FocusMatrixVersion
   * @param fmDataHandler FocusMatrixDataHandler
   */
  protected FocusMatrixDetails(final FocusMatrix focusMatrix, final FocusMatrixVersion fmVersion,
      final FocusMatrixDataHandler fmDataHandler) {
    this.focusMatrix = focusMatrix;
    this.fmVersion = fmVersion;
    this.fmDataHandler = fmDataHandler;
  }


  /**
   * @return Focus Matrix version
   */
  // ICDM-2569
  public FocusMatrixVersion getFocusMatrixVersion() {
    return this.fmVersion;
  }

  /**
   * @return the ucpaId
   */
  public Long getUcpaId() {
    return this.focusMatrix.getUcpaId();
  }

  /**
   * @return the use case Id
   */
  public Long getUseCaseId() {
    return this.focusMatrix.getUseCaseId();
  }

  /**
   * @return the focusMatrix
   */
  public FocusMatrix getFocusMatrix() {
    return this.focusMatrix;
  }


  /**
   * @return the usecasesection Id
   */
  public Long getUseCaseSectionId() {
    return this.focusMatrix.getSectionId();
  }

  /**
   * @return the attrs Id
   */
  public Long getAttributeId() {
    return this.focusMatrix.getAttrId();
  }

  /**
   * @return the colorCode
   */
  public FocusMatrixColorCode getColorCode() {
    return FocusMatrixColorCode.getColor(this.focusMatrix.getColorCode());
  }

  /**
   * @return the comments
   */
  public String getComments() {
    return this.focusMatrix.getComments();
  }

  /**
   * @return the comments
   */
  public boolean isDeleted() {
    return this.focusMatrix.getIsDeleted();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // Not applicable
    return null;
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
    return this.focusMatrix.getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.focusMatrix.getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.focusMatrix.getCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.focusMatrix.getModifiedDate();
  }


  /**
   * ICDM-1641
   *
   * @return link
   */
  public String getLink() {
    return this.focusMatrix.getLink();
  }

  /**
   * @return the use case item
   */
  public IUseCaseItem getUseCaseItem() {
    IUseCaseItem ret;
    if (getUseCaseSectionId() == null) {
      ret = this.fmDataHandler.getUcDataHandler().getUseCaseDetailsModel().getUsecaseMap().get(getUseCaseId());
    }
    else {
      ret = this.fmDataHandler.getUcDataHandler().getUseCaseDetailsModel().getUcSectionMap().get(getUseCaseSectionId());
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */

  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);

    toolTip.append("Attribute : ")
        .append(this.fmDataHandler.getPidcDataHandler().getAttributeMap().get(getAttributeId()).getName())
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
    return null == this.focusMatrix.getUcpaId();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String user) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String date) {
    // TODO Auto-generated method stub

  }
}

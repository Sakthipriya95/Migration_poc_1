/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author dmo5cob
 */
public class FocusMatrixUseCaseItem implements Comparable<FocusMatrixUseCaseItem> {

  /**
   * AbstractUseCaseItem instance
   */
  private final AbstractUseCaseItem useCaseItem;
  /**
   * The mapping id between a use case item and the attribute where attribute is the key and upca_id is the value
   */
  private final Map<Attribute, Long> attributeMapping = new HashMap<>();
  /**
   * PIDC Version ID
   */
  //ICDM-2569
  private final FocusMatrixVersion fmVersion;
  /**
   * Filter text
   */
  private FocusMatrixUsecaseItemFilterTxt focusMatrixUCFilTxt;


  /**
   * @param useCaseItem AbstractUseCaseItem instance
   * @param fmVersion focus matrix version
   */
  public FocusMatrixUseCaseItem(final AbstractUseCaseItem useCaseItem, final FocusMatrixVersion fmVersion) {
    this.useCaseItem = useCaseItem;
    this.fmVersion = fmVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrixUseCaseItem arg0) {
    return 1;
  }

  /**
   * @param attribute Attribute instance
   * @return the colorCode
   */
  public FocusMatrixColorCode getColorCode(final Attribute attribute) {
    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getID());
    if (!CommonUtils.isNullOrEmpty(childMap)) {
      FocusMatrixDetails fmDetails = childMap.get(getUseCaseItem().getID());
      return (fmDetails == null) || fmDetails.isDeleted() ? FocusMatrixColorCode.NOT_DEFINED : fmDetails.getColorCode();
    }
    return FocusMatrixColorCode.NOT_DEFINED;
  }

  /**
   * @param attribute Attribute instance
   * @return true if mapped
   */
  public boolean isMapped(final Attribute attribute) {

    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getID());
    if (!CommonUtils.isNullOrEmpty(childMap)) {
      FocusMatrixDetails fmDetails = childMap.get(getUseCaseItem().getID());
      if ((null != fmDetails) && checkForTheCellItem(attribute, fmDetails)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param attribute Attribute instance
   * @return the comments
   */
  public String getComments(final Attribute attribute) {

    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getID());
    if (!CommonUtils.isNullOrEmpty(childMap)) {
      FocusMatrixDetails fmDetails = childMap.get(getUseCaseItem().getID());
      return (fmDetails == null) || fmDetails.isDeleted() ? "" : fmDetails.getComments();
    }
    return "";
  }

  /**
   * @param attribute Attribute
   * @return link
   */
  public String getLink(final Attribute attribute) {

    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getID());
    if (!CommonUtils.isNullOrEmpty(childMap)) {
      FocusMatrixDetails fmDetails = childMap.get(getUseCaseItem().getID());
      return (fmDetails == null) || fmDetails.isDeleted() ? ApicConstants.EMPTY_STRING : fmDetails.getLink();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @param attribute Attribute
   * @return tool tip
   */
  public String getToolTip(final Attribute attribute) {
    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getID());
    if (!CommonUtils.isNullOrEmpty(childMap)) {
      FocusMatrixDetails fmDetails = childMap.get(getUseCaseItem().getID());
      return fmDetails == null ? ApicConstants.EMPTY_STRING : fmDetails.getToolTip();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return the useCaseItem
   */
  public AbstractUseCaseItem getUseCaseItem() {
    return this.useCaseItem;
  }

  /**
   * @return the attributeMapping
   */
  public Map<Attribute, Long> getAttributeMapping() {
    return this.attributeMapping;
  }

  /**
   * @param attribute attribute
   * @return filter text
   */
  public FocusMatrixUsecaseItemFilterTxt getFilterText(final Attribute attribute) {
    if (this.focusMatrixUCFilTxt == null) {
      this.focusMatrixUCFilTxt = new FocusMatrixUsecaseItemFilterTxt(this, attribute);
    }
    return this.focusMatrixUCFilTxt;
  }

  /**
   * @param attribute
   * @param fmDetails
   * @return
   */
  private boolean checkForTheCellItem(final Attribute attribute, final FocusMatrixDetails fmDetails) {
    return (this.fmVersion.equals(fmDetails.getFocusMatrixVersion())) &&
        fmDetails.getAttributeId().equals(attribute.getAttributeID()) &&
        ((((getUseCaseItem() instanceof UseCase) && getUseCaseItem().getID().equals(fmDetails.getUseCaseId())) ||
            ((getUseCaseItem() instanceof UseCaseSection) &&
                getUseCaseItem().getID().equals(fmDetails.getUseCaseSectionId()))) &&
            !fmDetails.isDeleted());
  }

  /**
   * @return the Focus Matrix Version
   */
  //ICDM-2569
  public FocusMatrixVersion getFocusMatrixVersion() {
    return this.fmVersion;
  }

  /**
   * @return apic data provider
   */
  ApicDataProvider getDataProvider() {
    return this.fmVersion.getDataCache().getDataProvider();
  }
}

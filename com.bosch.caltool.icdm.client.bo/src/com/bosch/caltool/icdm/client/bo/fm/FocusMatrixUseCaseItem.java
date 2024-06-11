/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * @author dmo5cob
 */
public class FocusMatrixUseCaseItem implements Comparable<FocusMatrixUseCaseItem> {

  /**
   * AbstractUseCaseItem instance
   */
  private final IUseCaseItemClientBO useCaseItem;
  /**
   * The mapping id between a use case item and the attribute where attribute is the key and upca_id is the value
   */
  private final Map<Attribute, Long> attributeMapping = new HashMap<>();
  /**
   * PIDC Version ID
   */
  // ICDM-2569
  private final FocusMatrixVersionClientBO fmVersion;
  /**
   * Filter text
   */
  private FocusMatrixUsecaseItemFilterTxt focusMatrixUCFilTxt;


  /**
   * @param useCaseItem AbstractUseCaseItem instance
   * @param fmVersion focus matrix version
   */
  public FocusMatrixUseCaseItem(final IUseCaseItemClientBO useCaseItem, final FocusMatrixVersionClientBO fmVersion) {
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
    return (obj.getClass() == this.getClass());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * @param attribute Attribute instance
   * @return the colorCode
   */
  public FocusMatrixColorCode getColorCode(final Attribute attribute) {
    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getId());
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

    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getId());
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

    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getId());
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

    ConcurrentMap<Long, FocusMatrixDetails> childMap = this.fmVersion.getFocusMatrixItemMap().get(attribute.getId());
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
  public String getToolTip(final AttributeClientBO attribute) {
    ConcurrentMap<Long, FocusMatrixDetails> childMap =
        this.fmVersion.getFocusMatrixItemMap().get(attribute.getAttribute().getId());
    if (!CommonUtils.isNullOrEmpty(childMap)) {
      FocusMatrixDetails fmDetails = childMap.get(getUseCaseItem().getID());
      return fmDetails == null ? ApicConstants.EMPTY_STRING : fmDetails.getToolTip();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return the useCaseItem
   */
  public IUseCaseItemClientBO getUseCaseItem() {
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
    return isFmValid(attribute, fmDetails) && (isUcItemValid(fmDetails) && !fmDetails.isDeleted());
  }

  /**
   * @param attribute
   * @param fmDetails
   * @return
   */
  private boolean isFmValid(final Attribute attribute, final FocusMatrixDetails fmDetails) {
    return (this.fmVersion.getFmVersion().equals(fmDetails.getFocusMatrixVersion())) &&
        fmDetails.getAttributeId().equals(attribute.getId());
  }

  /**
   * @param fmDetails
   * @return
   */
  private boolean isUcItemValid(final FocusMatrixDetails fmDetails) {
    return isUcClientBo(fmDetails) || isUCSectionClientBo(fmDetails);
  }

  /**
   * @param fmDetails
   * @return
   */
  private boolean isUCSectionClientBo(final FocusMatrixDetails fmDetails) {
    return (getUseCaseItem() instanceof UseCaseSectionClientBO) &&
        getUseCaseItem().getID().equals(fmDetails.getUseCaseSectionId());
  }

  /**
   * @param fmDetails
   * @return
   */
  private boolean isUcClientBo(final FocusMatrixDetails fmDetails) {
    return (getUseCaseItem() instanceof UsecaseClientBO) && getUseCaseItem().getID().equals(fmDetails.getUseCaseId());
  }

  /**
   * @return the Focus Matrix Version
   */
  // ICDM-2569
  public FocusMatrixVersionClientBO getFocusMatrixVersion() {
    return this.fmVersion;
  }

  /**
   * @param attr Attribute
   * @return boolean
   */
  public boolean isUcpaMapped(final Attribute attr) {
    UseCaseDataHandler ucDataHandler = this.fmVersion.getFMDataHandler().getUcDataHandler();
    if (getUseCaseItem() instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO grpBO = (UseCaseGroupClientBO) getUseCaseItem();
      return ucDataHandler.isUcGroupMapped(attr, grpBO);
    }
    if (getUseCaseItem() instanceof UsecaseClientBO) {
      UsecaseClientBO ucBO = (UsecaseClientBO) getUseCaseItem();
      ucBO.setUsecaseEditorModel(
          ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucBO.getUseCase().getId()));
      return ucDataHandler.isUCMapped(attr, ucBO);
    }
    if (getUseCaseItem() instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO ucBO = (UseCaseSectionClientBO) getUseCaseItem();
      return ucDataHandler.isUCSMapped(attr, ucBO);
    }
    return getUseCaseItem().isMapped(attr);
  }


}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.Set;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

/**
 * @author dmo5cob
 */
public class OutLineUseCaseFilterHandler {

  private final IUseCaseItemClientBO useCaseItem;
  private final UseCaseDataHandler useCaseDataHandler;

  /**
   * @param useCaseItem IUseCaseItemClientBO
   * @param useCaseDataHandler UseCaseDataHandler
   */
  public OutLineUseCaseFilterHandler(final IUseCaseItemClientBO useCaseItem,
      final UseCaseDataHandler useCaseDataHandler) {
    this.useCaseItem = useCaseItem;
    this.useCaseDataHandler = useCaseDataHandler;
  }


  /**
   * @param attr Attribute
   * @return true if the attr is mapped to UC item
   */
  public boolean isMapped(final Attribute attr) {

    if (getUseCaseItem() instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO grpBO = (UseCaseGroupClientBO) getUseCaseItem();
      return isUcGroupMapped(attr, grpBO);
    }
    if (getUseCaseItem() instanceof UsecaseClientBO) {
      UsecaseClientBO ucBO = (UsecaseClientBO) getUseCaseItem();
      ucBO.setUsecaseEditorModel(
          this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucBO.getUseCase().getId()));
      return isUCMapped(attr, ucBO);
    }
    return isUCSMapped(attr, (UseCaseSectionClientBO) getUseCaseItem());


  }

  private boolean isUcGroupMapped(final Attribute attr, final UseCaseGroupClientBO grp) {

    // Check whether child groups are mapped
    for (UseCaseGroupClientBO childUcg : grp.getChildGroups(false)) {
      if (isUcGroupMapped(attr, childUcg)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UsecaseClientBO ucase : grp.getUseCases(false)) {
      ucase.setUsecaseEditorModel(
          this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucase.getUseCase().getId()));
      if (isUCMapped(attr, ucase)) {
        return true;
      }
    }

    // if no mapping done, then return false
    return false;

  }


  private boolean isUCMapped(final Attribute attr, final UsecaseClientBO uc) {
    if (uc.canMapAttributes()) {
      Set<Long> mappedAttrIds = this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(uc.getId()).getUcItemAttrMap().get(uc.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UseCaseSectionClientBO ucSection : uc.getUseCaseSections(false)) {
        if (isUCSMapped(attr, ucSection)) {
          return true;

        }
      }
    }

    return false;
  }

  /**
   * @param attr
   * @param ucs
   * @return
   */
  public boolean isUCSMapped(final Attribute attr, final UseCaseSectionClientBO ucs) {
    if (ucs.canMapAttributes()) {
      Set<Long> mappedAttrIds = this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucs.getUseCaseSection().getUseCaseId()).getUcItemAttrMap().get(ucs.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UseCaseSectionClientBO ucsChild : ucs.getChildSections(false)) {
        if (isUCSMapped(attr, ucsChild)) {
          return true;
        }
      }
    }

    return false;
  }


  /**
   * @return the useCaseItem
   */
  public IUseCaseItemClientBO getUseCaseItem() {
    return this.useCaseItem;
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author dmo5cob
 */
public class FocusMatrix {

  /**
   * ApicDataProvider instance
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * FocusMatrixVersion instance
   */
  //ICDM-2569
  private final FocusMatrixVersion fmVersion;

  /**
   * List of selected UC items
   */
  private List<AbstractUseCaseItem> selectedUCItemsList = new ArrayList<>();

  /**
   * @param apicDataProvider ApicDataProvider instance
   * @param fmVersion PIDCVersion instance
   */
  public FocusMatrix(final ApicDataProvider apicDataProvider, final FocusMatrixVersion fmVersion) {

    this.apicDataProvider = apicDataProvider;
    this.fmVersion = fmVersion;
  }


  /**
   * @return the pidcVersion
   */
  public PIDCVersion getPidcVersion() {
    return this.fmVersion.getPidcVersion();
  }


  /**
   * @return list of FocusMatrixAttribute objects which represent each row in the focus matrix
   */
  public SortedSet<FocusMatrixAttribute> getFocusMatrixAttrsSet() {
    return this.fmVersion.isWorkingSet() ? getFmAttrsFromProjectAttrs() : getFmAttrsFromFmVersAttrs();
  }


  /**
   * @return
   */
  //ICDM-2569
  private SortedSet<FocusMatrixAttribute> getFmAttrsFromFmVersAttrs() {
    SortedSet<FocusMatrixAttribute> fmAttrSet = new TreeSet<>();
    for (Entry<Long, Set<FocusMatrixVersionAttr>> entry : this.fmVersion.getFocusMatrixVersionAttrMap().entrySet()) {
      Attribute attr = this.apicDataProvider.getAttribute(entry.getKey());
      FocusMatrixAttribute fmAttr = new FocusMatrixAttribute(this, attr);
      fmAttr.getFmVersAttrSet().addAll(entry.getValue());
      addUCItems(attr, fmAttr);
      fmAttrSet.add(fmAttr);
    }
    return fmAttrSet;
  }


  /**
   * @return
   */
  //ICDM-2569
  private SortedSet<FocusMatrixAttribute> getFmAttrsFromProjectAttrs() {
    SortedSet<FocusMatrixAttribute> setFmAttrs = new TreeSet<>();
    Map<Long, PIDCAttribute> allPIDCAttrs = getPidcVersion().getAttributesAll();
    for (Attribute attribute : this.apicDataProvider.getDataCache().getFocusMatrixApplicableAttrMap(allPIDCAttrs)
        .values()) {
      FocusMatrixAttribute fmAttr = new FocusMatrixAttribute(this, allPIDCAttrs.get(attribute.getID()));
      addUCItems(attribute, fmAttr);
      setFmAttrs.add(fmAttr);
    }
    return setFmAttrs;
  }


  /**
   * @param attribute
   * @param fmAttr
   */
  private void addUCItems(final Attribute attribute, final FocusMatrixAttribute fmAttr) {
    // if selection is not null
    if (null != getSelectedUCItemsList()) {
      // iterate over the list of selected items
      for (AbstractUseCaseItem ucItem : getSelectedUCItemsList()) {
        FocusMatrixUseCaseItem fmUCItem = new FocusMatrixUseCaseItem(ucItem, this.fmVersion);
        // use case
        if (ucItem instanceof UseCase) {
          UseCase useCase = (UseCase) ucItem;

          if (null != useCase.getMappingEntity(attribute)) {
            fmUCItem.getAttributeMapping().put(attribute, useCase.getMappingEntity(attribute).getUcpaId());

          }
        } // use case section
        else if (ucItem instanceof UseCaseSection) {
          UseCaseSection useCaseSection = (UseCaseSection) ucItem;
          if (null != useCaseSection.getMappingEntity(attribute)) {
            fmUCItem.getAttributeMapping().put(attribute, useCaseSection.getMappingEntity(attribute).getUcpaId());
          }
        }
        fmAttr.getFmUseCaseItemsSet().add(fmUCItem);
      }
    }
  }


  /**
   * @return the selectedUCItemsList
   */
  public List<AbstractUseCaseItem> getSelectedUCItemsList() {
    return this.selectedUCItemsList;
  }


  /**
   * @param selectedUCItemsList the selectedUCItemsList to set
   */
  public void setSelectedUCItemsList(final List<AbstractUseCaseItem> selectedUCItemsList) {
    this.selectedUCItemsList = selectedUCItemsList;
  }

  /**
   * @return FocusMatrixVersion item
   */
  //ICDM-2569
  public FocusMatrixVersion getFocusMatrixVersion() {
    return this.fmVersion;
  }
}

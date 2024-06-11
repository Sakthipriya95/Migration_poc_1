/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Statistics of PIDC Version
 *
 * @author bne4cob
 */
// ICDM-2247
public class PIDCVersionStatistics extends ProjectObjectStatistics<PIDCVersion> {

  /**
   * count of 'new' attributes
   */
  private int newAttrCount;

  /**
   * Mandatory attributes + project use case mapped attributes
   */
  private int importantAttrCount;


  /**
   * Number of attributes among import attributes where used flag is set (to YES or NO)
   */
  private int usedFlagSetImpAttrCount;


  /**
   * Number of attributes marked as focus matrix applicable
   */
  private int focusMatrixApplicableAttrCount;

  /**
   * Number of attributes having focus matrix defintion, among the FM applicable attributes
   */
  private int focusMatrixRatedAttrCount;

  // ICDM-2461
  /**
   * Number of use project case items (last node)
   */
  private final Set<AbstractUseCaseItem> endUcItems = new HashSet<>();

  /**
   * is project use case attr not mapped
   */
  private boolean isProjUseCaseAttrNotDefined = false;

  // Task 234241
  /**
   * Mandatory attributes count only without considering project use case
   */
  private int totalMandatoryAttrCountOnly;

  // Task 234241
  /**
   * Used attributes only, without considering project use case
   */
  private int totalMandtryAttrUsedCountOnly;

  /**
   * @param pidcVers PIDC Version
   */
  PIDCVersionStatistics(final PIDCVersion pidcVers) {
    super(pidcVers);
    getLogger().debug("Computing PIDC Version statistics for {}", pidcVers.getID());
    buildStatistics();
    getLogger().debug("Statistics computation completed");

  }

  /**
   * Build statistics once, based on the attribute properties. The data is stored in fields.
   */
  private void buildStatistics() {

    Map<Long, PIDCAttribute> projAttrMap = getProjectObject().getAttributes(false);
    Set<FavUseCaseItemNode> rootUcFavNodes = new HashSet<>(getProjectObject().getPidc().getRootUcFavNodes());
    Set<AbstractUseCaseItem> mappableFavUcItemSet = getMappableFavouriteUcItem();
    Map<Long, Attribute> fmApplAttrMap = getProjectObject().getDataCache().getFocusMatrixApplicableAttrMap(projAttrMap);

    for (Entry<Long, PIDCAttribute> projAttrEntry : projAttrMap.entrySet()) {
      PIDCAttribute pidcAttr = projAttrEntry.getValue();
      // New Attributes
      if (pidcAttr.getID() == null) {
        this.newAttrCount++;
      }

      // Relevant attributes (Mandatory OR attributes mapped to project use case items
      boolean mappedToProjAttr = isMappedToProjAttr(rootUcFavNodes, pidcAttr);
      boolean isMandatoryAttrFlag = pidcAttr.isMandatory();
      boolean isValueDefinedFlag = getProjectObject().isValueDefined(pidcAttr);

      if (isMandatoryAttrFlag) {
        this.totalMandatoryAttrCountOnly++;
        if (isValueDefinedFlag) {
          this.totalMandtryAttrUsedCountOnly++;
        }
      }


      if (isMandatoryAttrFlag || mappedToProjAttr) {
        this.importantAttrCount++;
        if (isValueDefinedFlag) {
          this.usedFlagSetImpAttrCount++;
        }
        else {
          if (mappedToProjAttr) {
            this.isProjUseCaseAttrNotDefined = true;
          }
        }
      }

      // Attributes marked as focus matrix relevant
      if (fmApplAttrMap.containsKey(projAttrEntry.getKey()) && pidcAttr.isFocusMatrixApplicable()) {
        this.focusMatrixApplicableAttrCount++;
        checkRatedAttribute(mappableFavUcItemSet, pidcAttr);
      }

    }

  }


  /**
   * Finds attributes with focus matrix definition(color != WHITE), among the attributes marked as FM relevant
   *
   * @param mappableFavUcItemSet mappable favourite use case items
   * @param pidcAttr PIDC attribute being checked
   */
  private void checkRatedAttribute(final Set<AbstractUseCaseItem> mappableFavUcItemSet, final PIDCAttribute pidcAttr) {

    Map<Long, ConcurrentMap<Long, FocusMatrixDetails>> projFmMap =
        getProjectObject().getFocusMatrixWorkingSetVersion().getFocusMatrixItemMap();

    for (AbstractUseCaseItem ucItem : mappableFavUcItemSet) {

      if (ucItem.isFocusMatrixRelevant(true) && (projFmMap.get(pidcAttr.getAttribute().getID()) != null) &&
          (projFmMap.get(pidcAttr.getAttribute().getID()).get(ucItem.getID()) != null)) {

        FocusMatrixDetails projFmItem = projFmMap.get(pidcAttr.getAttribute().getID()).get(ucItem.getID());
        if (!projFmItem.isDeleted() && (projFmItem.getColorCode() != FocusMatrixColorCode.NOT_DEFINED)) {
          this.focusMatrixRatedAttrCount++;
          break;
        }
      }
    }
  }

  /**
   * Find mappable favourite use case items
   *
   * @return set of use case items
   */
  private Set<AbstractUseCaseItem> getMappableFavouriteUcItem() {
    Set<AbstractUseCaseItem> projFavUcMappableItemSet = new HashSet<>();
    for (FavUseCaseItem projFavUcItem : getProjectObject().getPidc().getFavoriteUCMap().values()) {
      if (projFavUcItem.getUseCaseItem() instanceof UseCaseGroup) {
        projFavUcMappableItemSet = addChildItems(projFavUcItem.getUseCaseItem(), projFavUcMappableItemSet);
      }
      else {
        projFavUcMappableItemSet.addAll(projFavUcItem.getUseCaseItem().getMappableItems());
        // ICDM-2461
        this.endUcItems.addAll(projFavUcItem.getUseCaseItem().getMappableItems());
      }
    }

    return projFavUcMappableItemSet;
  }

  /**
   * Get child items and add relevant usecase items to the model
   *
   * @param projFavUcMappableItemSet
   * @param endUcItems Set to hold the number of project use case items(last node is considered)
   */
  private Set<AbstractUseCaseItem> addChildItems(final AbstractUseCaseItem useCaseGrp,
      final Set<AbstractUseCaseItem> projFavUcMappableItemSet) {
    if (CommonUtils.isNullOrEmpty(((UseCaseGroup) useCaseGrp).getChildGroupSet(false))) {
      // add child usecase items if there are any
      for (AbstractUseCaseItem useCaseItem : useCaseGrp.getChildUCItems()) {
        for (AbstractUseCaseItem uc : useCaseItem.getMappableItems()) {
          // ICDM-2461
          this.endUcItems.add(uc);
          if (uc.isFocusMatrixRelevant(true)) {
            // add to the list in the model only in case of focus matrix relevant & project specific use case
            projFavUcMappableItemSet.add(uc);
          }
        }
      }
    }
    else {
      for (UseCaseGroup grpChild : ((UseCaseGroup) useCaseGrp).getChildGroupSet(false)) {
        // add child items of usecase groups
        addChildItems(grpChild, projFavUcMappableItemSet);
      }
    }
    return projFavUcMappableItemSet;

  }

  /**
   * Checkes whether the PIDC attribute is mapped to any of the given project use case nodes
   *
   * @param mappableFavUcItemSet use case items
   * @param pidcAttr PIDC Attribute
   * @return true if mapped
   */
  private boolean isMappedToProjAttr(final Set<FavUseCaseItemNode> rootUcFavNodes, final PIDCAttribute pidcAttr) {
    boolean mapped = false;
    for (FavUseCaseItemNode node : rootUcFavNodes) {
      if (node.isMapped(pidcAttr.getAttribute())) {
        mapped = true;
        break;
      }
    }
    return mapped;
  }

  /**
   * @return number of attributes 'new'
   */
  public int getNewAttributesCount() {
    return this.newAttrCount;
  }

  /**
   * @return number of project use cases
   */
  public int getProjectUseCaseCount() {
    return this.endUcItems.size();
  }

  /**
   * @return the importantAttrCount
   */
  public int getImportantAttrCount() {
    return this.importantAttrCount;
  }


  /**
   * @return the usedFlagSetImpAttrCount
   */
  public int getUsedFlagSetImpAttrCount() {
    return this.usedFlagSetImpAttrCount;
  }


  /**
   * @return the focusMatrixApplicableAttrCount
   */
  public int getFocusMatrixApplicableAttrCount() {
    return this.focusMatrixApplicableAttrCount;
  }


  /**
   * @return the focusMatrixRatedAttrCount
   */
  public int getFocusMatrixRatedAttrCount() {
    return this.focusMatrixRatedAttrCount;
  }


  /**
   * @return the isProjUseCaseAttrNotDefined
   */
  public boolean isProjUseCaseAttrNotDefined() {
    return this.isProjUseCaseAttrNotDefined;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStatisticsAsString() {

    return getDataProvider().getMessage(ApicConstants.MSGGRP_PIDC_EDITOR, "PROJECT_STATISTICS_PIDCVERS",
        getAllAttributesCount(), getUsedAttributesCount(), getNotUsedAttributesCount(), getNotDefinedAttributesCount(),
        getNewAttributesCount(), getLastModifiedDateStr(), this.endUcItems.size()/* ICDM-2461 */,
        getUsedFlagSetImpAttrCount(), getImportantAttrCount(), getTotalMandtryAttrUsedCountOnly(),
        getTotalMandatoryAttrCountOnly(), getFocusMatrixApplicableAttrCount() - getFocusMatrixRatedAttrCount(),
        getFocusMatrixApplicableAttrCount());

  }

  /**
   * @return true if the coverage pidc , coverage mandatory attrs and unrated fm statistics are compliant ( if of the
   *         form x and y then x should be equal to y)
   */
  @Override
  public boolean validateStatistics() {
    if ((getUsedFlagSetImpAttrCount() != getImportantAttrCount() /* coverage pidc */) ||
        (getTotalMandtryAttrUsedCountOnly() != getTotalMandatoryAttrCountOnly() /* coverage mandatory attrs */) ||
        ((getFocusMatrixApplicableAttrCount() -
            getFocusMatrixRatedAttrCount()) != getFocusMatrixApplicableAttrCount() /* unrated fm */)) {
      return false;
    }

    return true;
  }


  /**
   * @return the totalMandatoryAttrCountOnly
   */
  public int getTotalMandatoryAttrCountOnly() {
    return this.totalMandatoryAttrCountOnly;
  }


  /**
   * @return the totalMandtryAttrUsedCountOnly
   */
  public int getTotalMandtryAttrUsedCountOnly() {
    return this.totalMandtryAttrUsedCountOnly;
  }


}

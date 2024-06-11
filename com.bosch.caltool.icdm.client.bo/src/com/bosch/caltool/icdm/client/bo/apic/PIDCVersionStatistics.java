/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixColorCode;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDetails;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItem;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseFavNodesMgr;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Statistics of PIDC Version
 *
 * @author bne4cob
 */
public class PIDCVersionStatistics extends ProjectObjectStatistics<PidcVersion> {

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
  private final Set<IUseCaseItemClientBO> endUcItems = new HashSet<>();

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
   * instance of usecase data handler
   */
  private final UseCaseDataHandler useCaseDataHandler;
  /**
   * instance of focus matrix data handler
   */
  private final FocusMatrixDataHandler fmDataHandler;


  /**
   * @param pidcVers PIDC Version
   * @param focusMatrixDataHandler
   */
  PIDCVersionStatistics(final PidcVersion pidcVers, final AbstractProjectObjectBO projObjBO,
      final UseCaseDataHandler ucDataHandler, final FocusMatrixDataHandler focusMatrixDataHandler) {
    super(pidcVers, projObjBO);
    CDMLogger.getInstance().debug("Computing PIDC Version statistics for {}", pidcVers.getId());
    this.useCaseDataHandler = ucDataHandler;
    this.fmDataHandler = focusMatrixDataHandler;
    buildStatistics();
    CDMLogger.getInstance().debug("Statistics computation completed");

  }

  /**
   * Build statistics once, based on the attribute properties. The data is stored in fields.
   */
  private void buildStatistics() {

    Map<Long, PidcVersionAttribute> projAttrMap = getProjectObjectBO().getPidcDataHandler().getPidcVersAttrMapDefined();

    UseCaseFavNodesMgr ucFavMgr;
    ucFavMgr = new UseCaseFavNodesMgr(getProjectObject(), this.useCaseDataHandler);
    this.useCaseDataHandler.setUcFavMgr(ucFavMgr);
    Set<FavUseCaseItemNode> rootUcFavNodes = new HashSet<>(this.useCaseDataHandler.getRootProjectUcFavNodes());
    // get fav use case items
    Set<IUseCaseItemClientBO> mappableFavUcItemSet = getMappableFavouriteUcItem(ucFavMgr.getProjFavMap());

    Map<PidcVersionAttribute, Attribute> fmApplAttrMap = this.fmDataHandler.getFocusMatrixApplicableAttrMap();

    for (Entry<Long, PidcVersionAttribute> projAttrEntry : projAttrMap.entrySet()) {
      PidcVersionAttribute pidcAttr = projAttrEntry.getValue();
      // New Attributes
      if (pidcAttr.getId() == null) {
        this.newAttrCount++;
      }


      // Relevant attributes (Mandatory OR attributes mapped to project use case items
      boolean mappedToProjAttr = isMappedToProjAttr(rootUcFavNodes, pidcAttr);
      PidcVersionAttributeBO projAttrHandler =
          new PidcVersionAttributeBO(pidcAttr, (PidcVersionBO) getProjectObjectBO());


      boolean isVisible = projAttrHandler.isVisible();

      // an attribute is mandatory only if it is visible
      boolean isMandatoryAttrFlag = projAttrHandler.isMandatory();
      boolean isValueDefinedFlag = getProjectObjectBO().isValueDefined(pidcAttr);

      if (isMandatoryAttrFlag) {
        this.totalMandatoryAttrCountOnly++;
        if (isVisible && isValueDefinedFlag) {
          this.totalMandtryAttrUsedCountOnly++;
        }
      }


      if ((isMandatoryAttrFlag || mappedToProjAttr)) {
        this.importantAttrCount++;
        if (isVisible && isValueDefinedFlag) {
          this.usedFlagSetImpAttrCount++;
        }
        else {
          if (mappedToProjAttr) {
            this.isProjUseCaseAttrNotDefined = true;
          }
        }
      }

      // Attributes marked as focus matrix relevant
      if (fmApplAttrMap.containsKey(pidcAttr) && pidcAttr.isFocusMatrixApplicable()) {
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
  private void checkRatedAttribute(final Set<IUseCaseItemClientBO> mappableFavUcItemSet,
      final PidcVersionAttribute pidcAttr) {

    Map<Long, ConcurrentMap<Long, FocusMatrixDetails>> projFmMap;

    projFmMap = this.fmDataHandler.getSelFmVersion().getFocusMatrixItemMap();

    for (IUseCaseItemClientBO ucItem : mappableFavUcItemSet) {

      if (ucItem.isFocusMatrixRelevant(true) && (projFmMap.get(pidcAttr.getAttrId()) != null) &&
          (projFmMap.get(pidcAttr.getAttrId()).get(ucItem.getID()) != null)) {

        FocusMatrixDetails projFmItem = projFmMap.get(pidcAttr.getAttrId()).get(ucItem.getID());
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
   * @param map
   * @return set of use case items
   * @throws ApicWebServiceException
   */
  private Set<IUseCaseItemClientBO> getMappableFavouriteUcItem(final Map<Long, FavUseCaseItem> projFavMap) {
    Set<IUseCaseItemClientBO> projFavUcMappableItemSet = new HashSet<>();
    for (FavUseCaseItem projFavUcItem : projFavMap.values()) {
      IUseCaseItemClientBO ucItemClientBo = projFavUcItem.getUseCaseItem(this.useCaseDataHandler);
      if (ucItemClientBo instanceof UseCaseGroupClientBO) {
        addChildItems(ucItemClientBo, projFavUcMappableItemSet);
      }
      else {

        if (ucItemClientBo instanceof UsecaseClientBO) {
          ((UsecaseClientBO) ucItemClientBo)
              .setUsecaseEditorModel(this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
                  .get(projFavUcItem.getUseCaseItem(this.useCaseDataHandler).getUcItem().getId()));
        }
        projFavUcMappableItemSet.addAll(ucItemClientBo.getMappableItems());
        this.endUcItems.addAll(ucItemClientBo.getMappableItems());
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
  private Set<IUseCaseItemClientBO> addChildItems(final IUseCaseItemClientBO useCaseGrp,
      final Set<IUseCaseItemClientBO> projFavUcMappableItemSet) {
    UseCaseGroupClientBO useCaseGrpClientBo1 = (UseCaseGroupClientBO) useCaseGrp;
    SortedSet<UseCaseGroupClientBO> childUcSet = useCaseGrpClientBo1.getChildGroupSet(false);
    if (CommonUtils.isNullOrEmpty(childUcSet)) {

      // add child usecase items if there are any
      for (IUseCaseItemClientBO useCaseItem : useCaseGrpClientBo1.getChildUCItems()) {
        if (useCaseItem instanceof UsecaseClientBO) {
          ((UsecaseClientBO) useCaseItem).setUsecaseEditorModel(this.useCaseDataHandler.getUseCaseDetailsModel()
              .getUsecaseDetailsModelMap().get(useCaseItem.getUcItem().getId()));
        }
        for (IUseCaseItemClientBO uc : useCaseItem.getMappableItems()) {
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
      UseCaseGroupClientBO useCaseGrpClientBo2 = (UseCaseGroupClientBO) useCaseGrp;
      for (UseCaseGroupClientBO grpChild : useCaseGrpClientBo2.getChildGroupSet(false)) {
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
  private boolean isMappedToProjAttr(final Set<FavUseCaseItemNode> rootUcFavNodes,
      final PidcVersionAttribute pidcAttr) {
    boolean mapped = false;
    for (FavUseCaseItemNode node : rootUcFavNodes) {
      if (node.isMapped(getProjectObjectBO().getPidcDataHandler().getAttributeMap().get(pidcAttr.getAttrId()))) {
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
    String stats = "";
    try {
      stats = new CommonDataBO().getMessage(ApicConstants.MSGGRP_PIDC_EDITOR, "PROJECT_STATISTICS_PIDCVERS",
          getAllAttributesCount(), getUsedAttributesCount(), getNotUsedAttributesCount(),
          getNotDefinedAttributesCount(), getNewAttributesCount(), getLastModifiedDateStr(),
          this.endUcItems.size()/* ICDM-2461 */, getUsedFlagSetImpAttrCount(), getImportantAttrCount(),
          getTotalMandtryAttrUsedCountOnly(), getTotalMandatoryAttrCountOnly(),
          getFocusMatrixApplicableAttrCount() - getFocusMatrixRatedAttrCount(), getFocusMatrixApplicableAttrCount());
    }
    catch (ApicWebServiceException | ParseException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return stats;
  }

  /**
   * @return true if the coverage pidc , coverage mandatory attrs and unrated fm statistics are compliant ( if of the
   *         form x and y then x should be equal to y)
   */
  @Override
  public boolean validateStatistics() {
    boolean isValidStatistics = true;
    if ((getUsedFlagSetImpAttrCount() != getImportantAttrCount() /* coverage pidc */) ||
        (getTotalMandtryAttrUsedCountOnly() != getTotalMandatoryAttrCountOnly() /* coverage mandatory attrs */) ||
        ((getFocusMatrixApplicableAttrCount() -
            getFocusMatrixRatedAttrCount()) != getFocusMatrixApplicableAttrCount() /* unrated fm */)) {
      isValidStatistics = false;
    }

    return isValidStatistics;
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

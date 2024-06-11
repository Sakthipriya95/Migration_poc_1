/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;

/**
 * @author nip4cob
 */
public class MultipleFocusMatrixCommand extends AbstractSimpleCommand {

  private Map<Long, UsecaseFavorite> input = new HashMap<>();
  private TabvProjectAttr tabvProjectAttr;
  private TUsecaseFavorite tUsecaseFav;

  /**
   * @param serviceData
   * @param input
   * @param tabvProjectAttr
   * @throws IcdmException
   */
  protected MultipleFocusMatrixCommand(final ServiceData serviceData, final Map<Long, UsecaseFavorite> input,
      final TabvProjectAttr tabvProjectAttr) throws IcdmException {
    super(serviceData);
    this.input = input;
    this.tabvProjectAttr = tabvProjectAttr;
  }


  /**
   * @param serviceData
   * @param input
   * @param tUsecaseFav
   * @throws IcdmException
   */
  public MultipleFocusMatrixCommand(final ServiceData serviceData, final Map<Long, UsecaseFavorite> input,
      final TUsecaseFavorite tUsecaseFav) throws IcdmException {
    super(serviceData);
    this.input = input;
    this.tUsecaseFav = tUsecaseFav;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    Set<IUseCaseItem> leafNodes;
    Set<Long> attrIdSet = new HashSet<>();
    TPidcVersion tPidcVersion;
    if (null != this.tabvProjectAttr) {
      leafNodes = new UcpAttrLoader(getServiceData()).getLeafNodes(this.input, false);
      tPidcVersion = this.tabvProjectAttr.getTPidcVersion();
      attrIdSet.add(this.tabvProjectAttr.getTabvAttribute().getAttrId());
    }
    else {
      Map<Long, UsecaseFavorite> favMap = new HashMap<>();
      favMap.put(this.tUsecaseFav.getUcFavId(),
          new UsecaseFavoriteLoader(getServiceData()).createDataObject(this.tUsecaseFav));
      leafNodes = new UcpAttrLoader(getServiceData()).getLeafNodes(favMap, false);
      PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
      tPidcVersion = pidcVersionLoader.getEntityObject(
          pidcVersionLoader.getActivePidcVersion(this.tUsecaseFav.getTabvProjectidcard().getProjectId()).getId());
      PidcVersionWithDetails pidcVersionWithDetails =
          pidcVersionLoader.getPidcVersionWithDetails(tPidcVersion.getPidcVersId());

      // Add FM relevant attrs only
      Map<Long, PidcVersionAttribute> pidcVersionAttributeMap = pidcVersionWithDetails.getPidcVersionAttributeMap();
      attrIdSet = pidcVersionAttributeMap.values().stream().filter(PidcVersionAttribute::isFocusMatrixApplicable)
          .map(PidcVersionAttribute::getAttrId).collect(Collectors.toSet());
    }
    updateFmRevelvantEntities(leafNodes, tPidcVersion, attrIdSet);
  }


  /**
   * @param leafNodes
   * @param tPidcVersion
   * @param attrIdSet
   * @throws IcdmException
   */
  private void updateFmRevelvantEntities(final Set<IUseCaseItem> leafNodes, final TPidcVersion tPidcVersion,
      final Set<Long> attrIdSet)
      throws IcdmException {
    Set<TFocusMatrixVersion> tFocusMatrixVersions = tPidcVersion.getTFocusMatrixVersions();
    Set<TFocusMatrix> fmListToUpdate = new HashSet<>();
    Set<IUseCaseItem> useCaseItemSet = new HashSet<>(leafNodes);
    for (Long attrId : attrIdSet) {
      if (null != tFocusMatrixVersions) {
        for (TFocusMatrixVersion tFocusMatrixVersion : tFocusMatrixVersions) {
          // Get the working set of FocusMatrixVersions
          if (tFocusMatrixVersion.getRevNumber() != 0L) {
            continue;
          }
          for (TFocusMatrix tFocusMatrix : tFocusMatrixVersion.getTFocusMatrixs()) {
            for (IUseCaseItem useCaseItem : leafNodes) {
              // If focus matrix is already present for this leaf node , update the FM relevant flag to YES
              // Else create a new focus matrix entity
              if (useCaseItem instanceof UseCase) {
                UseCase uc = (UseCase) useCaseItem;
                if (CommonUtils.isEqual(uc.getId(), tFocusMatrix.getTabvUseCas().getUseCaseId()) &&
                    Long.valueOf(tFocusMatrix.getTabvAttribute().getAttrId()).equals(attrId)) {
                  if (useCaseItemSet.contains(useCaseItem)) {
                    useCaseItemSet.remove(useCaseItem);
                  }
                  tFocusMatrix.setDeletedFlag(ApicConstants.CODE_NO);
                  fmListToUpdate.add(tFocusMatrix);
                }
              }
              else if (useCaseItem instanceof UseCaseSection) {
                UseCaseSection usecaseSec = (UseCaseSection) useCaseItem;

                if (((null != tFocusMatrix.getTabvUseCaseSection()) &&
                    CommonUtils.isEqual(usecaseSec.getId(), tFocusMatrix.getTabvUseCaseSection().getSectionId())) &&
                    Long.valueOf(tFocusMatrix.getTabvAttribute().getAttrId()).equals(attrId)) {
                  if (useCaseItemSet.contains(useCaseItem)) {
                    useCaseItemSet.remove(useCaseItem);
                  }
                  tFocusMatrix.setDeletedFlag(ApicConstants.CODE_NO);
                  fmListToUpdate.add(tFocusMatrix);
                }
              }
            }
          }
          updateFocusMatrix(fmListToUpdate);
          createFocusMatrix(attrId, tFocusMatrixVersion, useCaseItemSet);
        }
      }
    }
  }

  /**
   * @param attrId
   * @param workingSet
   * @param useCaseItemSet
   * @throws IcdmException
   */
  private void createFocusMatrix(final Long attrId, final TFocusMatrixVersion workingSet,
      final Set<IUseCaseItem> useCaseItemSet)
      throws IcdmException {
    UcpAttrLoader ucpaLoader = new UcpAttrLoader(getServiceData());
    ConcurrentMap<Long, ConcurrentMap<Long, Long>> ucpAttrIdMap = loadUcpAttrForFMVers(workingSet, ucpaLoader);
    Set<FocusMatrix> fmListToCreate = new HashSet<>();
    for (IUseCaseItem useCaseItem : useCaseItemSet) {
      FocusMatrix fmToCreate = new FocusMatrix();
      fmToCreate.setAttrId(attrId);
      fmToCreate.setIsDeleted(false);
      fmToCreate.setFmVersId(workingSet.getFmVersId());
      if (useCaseItem instanceof UseCase) {
        UseCase uc = (UseCase) useCaseItem;
        if ((null != ucpAttrIdMap.get(uc.getId())) && (null != ucpAttrIdMap.get(uc.getId()).get(attrId))) {
          fmToCreate.setUseCaseId(uc.getId());
          fmToCreate.setUcpaId(ucpAttrIdMap.get(uc.getId()).get(attrId));
          fmListToCreate.add(fmToCreate);
          createFocusMatrix(fmToCreate);
        }
      }
      else if (useCaseItem instanceof UseCaseSection) {
        UseCaseSection usecaseSec = (UseCaseSection) useCaseItem;
        if ((null != ucpAttrIdMap.get(usecaseSec.getId())) &&
            (null != ucpAttrIdMap.get(usecaseSec.getId()).get(attrId))) {
          fmToCreate.setUseCaseId(usecaseSec.getUseCaseId());
          fmToCreate.setSectionId(usecaseSec.getId());
          fmToCreate.setUcpaId(ucpAttrIdMap.get(usecaseSec.getId()).get(attrId));
          fmListToCreate.add(fmToCreate);
          createFocusMatrix(fmToCreate);
        }
      }
    }
  }


  /**
   * @param fm
   * @throws IcdmException
   */
  private void createFocusMatrix(final FocusMatrix fm) throws IcdmException {
    // create new focus matrix entity
    FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), fm, false, false);
    executeChildCommand(cmd);
  }

  /**
   * @param workingSet
   * @param ucpaLoader
   * @return
   * @throws DataException
   */
  private ConcurrentMap<Long, ConcurrentMap<Long, Long>> loadUcpAttrForFMVers(final TFocusMatrixVersion workingSet,
      final UcpAttrLoader ucpaLoader)
      throws DataException {
    ConcurrentMap<Long, ConcurrentMap<Long, Long>> ucpAttrIdMap = new ConcurrentHashMap<>();
    Set<UcpAttr> ucpaForFocusMatrixVersion = ucpaLoader.getUCPAForFocusMatrixVersion(workingSet.getFmVersId());
    for (UcpAttr ucpAttr : ucpaForFocusMatrixVersion) {
      Long ucItemID;

      if (ucpAttr.getSectionId() == null) {
        ucItemID = ucpAttr.getUseCaseId();
      }
      else {
        ucItemID = ucpAttr.getSectionId();
      }
      ConcurrentMap<Long, Long> attrToUcpaMap = ucpAttrIdMap.get(ucItemID);
      if (null == attrToUcpaMap) {
        // if there is no entry for uc item id
        attrToUcpaMap = new ConcurrentHashMap<>();
        ucpAttrIdMap.put(ucItemID, attrToUcpaMap);
      }
      attrToUcpaMap.put(ucpAttr.getAttrId(), ucpAttr.getId());
    }
    return ucpAttrIdMap;
  }

  /**
   * @param fmListToUpdate
   * @throws IcdmException
   */
  private void updateFocusMatrix(final Set<TFocusMatrix> fmListToUpdate) throws IcdmException {
    FocusMatrixLoader fmLoader = new FocusMatrixLoader(getServiceData());
    for (TFocusMatrix tFocusMatrix : fmListToUpdate) {
      FocusMatrix fm = fmLoader.createDataObject(tFocusMatrix);
      FocusMatrixCommand cmd = new FocusMatrixCommand(getServiceData(), fm, true, false);
      executeChildCommand(cmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return false;
  }

}

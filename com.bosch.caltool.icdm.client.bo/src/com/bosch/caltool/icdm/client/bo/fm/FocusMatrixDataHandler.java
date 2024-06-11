/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixDetailsModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixVersionAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class FocusMatrixDataHandler extends AbstractClientDataHandler {


  // ICDM-2612
  private FocusMatrixVersionClientBO selFmVersion;

  /**
   * UseCaseDataHandler
   */
  private UseCaseDataHandler ucDataHandler;
  /**
   * UseCaseRootNode
   */
  private UseCaseRootNode useCaseRootNode;
  /**
   * ProjFavUcRootNode
   */
  private final ProjFavUcRootNode projFavUcRootNode;


  /**
   * PidcVersionBO
   */
  private final PidcVersionBO pidcVersionBO;

  private Map<Long, FocusMatrixVersionClientBO> fmVersionMap;

  /**
   * key- fm version id, value - set of focus matrix verison attributes
   */
  private final Map<Long, Set<FocusMatrixVersionAttrClientBO>> fmVersAttrMap = new HashMap<>();

  private boolean allAttrNeededForExport;

  /**
   * @param pidcVersionBO PidcVersionBO
   */
  public FocusMatrixDataHandler(final PidcVersionBO pidcVersionBO) {
    super();


    this.ucDataHandler = new UseCaseDataHandler(pidcVersionBO.getPidcVersion());
    this.useCaseRootNode = new UseCaseRootNode(this.ucDataHandler);
    this.projFavUcRootNode = new ProjFavUcRootNode();
    this.pidcVersionBO = pidcVersionBO;
    this.allAttrNeededForExport = false;
  }

  /**
   * @param ucDataHandler usecase data handler
   * @param pidcVerisonBO PidcVersionBO
   */
  public FocusMatrixDataHandler(final UseCaseDataHandler ucDataHandler, final PidcVersionBO pidcVerisonBO) {
    super();

    this.ucDataHandler = ucDataHandler;
    this.useCaseRootNode = new UseCaseRootNode(this.ucDataHandler);
    this.projFavUcRootNode = new ProjFavUcRootNode();
    this.pidcVersionBO = pidcVerisonBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    cnsForAttrProjAttrGroup();

    cnsForUsecaseItems();

    cnsForFMVersions();

    cnsForFocusMatrix();

  }

  /**
   *
   */
  private void cnsForAttrProjAttrGroup() {
    registerCnsChecker(MODEL_TYPE.PROJ_ATTR, chData -> {
      Long pidcVersId = ((PidcVersionAttribute) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(getPidcVersion().getId(), pidcVersId) &&
          ((PidcVersionAttribute) CnsUtils.getModel(chData)).isFocusMatrixApplicable();
    });
    registerCnsAction(this::refreshFocusMatrixVersions, MODEL_TYPE.PROJ_ATTR);
    registerCnsChecker(MODEL_TYPE.ATTRIBUTE, MODEL_TYPE.SUPER_GROUP, MODEL_TYPE.GROUP);
    registerCnsAction(this::refreshPidcDataHandler, MODEL_TYPE.SUPER_GROUP, MODEL_TYPE.GROUP);
  }

  /**
   *
   */
  private void cnsForUsecaseItems() {
    registerCns(this::refreshUsecaseModel, MODEL_TYPE.USE_CASE);
    registerCns(this::refreshUsecaseModel, MODEL_TYPE.USE_CASE_SECT);
    registerCns(this::refreshUsecaseModel, MODEL_TYPE.USE_CASE_GROUP);
    registerCns(chData -> {
      Long pidcId = ((UsecaseFavorite) CnsUtils.getModel(chData)).getProjectId();
      return CommonUtils.isEqual(getPidcVersion().getPidcId(), pidcId);
    }, this::refreshUsecaseModel, MODEL_TYPE.UC_FAV);
    registerCns(this::refreshUsecaseModel, MODEL_TYPE.UCP_ATTR);
  }

  /**
   * load the focus matrix for working set
   */
  private void refreshUsecaseModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // re initialise uc handler and usecase root node
    this.ucDataHandler = new UseCaseDataHandler(this.pidcVersionBO.getPidcVersion());
    this.useCaseRootNode = new UseCaseRootNode(this.ucDataHandler);
    for (ChangeDataInfo chData : chDataInfoMap.values()) {
      if ((chData.getType() == MODEL_TYPE.USE_CASE_SECT) && (chData.getChangeType() == CHANGE_OPERATION.CREATE)) {
        // when the usecase section is added newly and if its the first child
        refreshFocusMatrix(null);
      }
    }
  }

  /**
   * for focus matrix entities
   */
  private void cnsForFocusMatrix() {
    registerCnsChecker(MODEL_TYPE.FOCUS_MATRIX, chData -> {
      Long fmvVersID = ((FocusMatrix) CnsUtils.getModel(chData)).getFmVersId();
      FocusMatrixVersionClientBO fmWorking = getFocusMatrixWorkingSet();
      // If working set is still null, it means FM details are not loaded yet.
      return (fmWorking != null) && CommonUtils.isEqual(fmWorking.getFmVersion().getId(), fmvVersID);
    });
    registerCnsAction(this::refreshFocusMatrix, MODEL_TYPE.FOCUS_MATRIX);
  }

  /**
   * load the focus matrix for working set
   */
  private void refreshPidcDataHandler(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    PidcDetailsLoader loader = new PidcDetailsLoader(getPidcDataHandler());
    loader.loadDataModel(getPidcVersion().getId());
  }

  /**
   * for focus matrix version entries
   */
  private void cnsForFMVersions() {
    registerCnsChecker(MODEL_TYPE.FOCUS_MATRIX_VERSION, chData -> {
      Long pidcVersId = ((FocusMatrixVersion) CnsUtils.getModel(chData)).getPidcVersId();
      return CommonUtils.isEqual(this.pidcVersionBO.getPidcVersion().getId(), pidcVersId);
    });

    registerCnsAction(this::refreshFocusMatrixVersions, MODEL_TYPE.FOCUS_MATRIX_VERSION);
  }


  /**
   * load the fm versions again
   */
  private void refreshFocusMatrixVersions(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    if (null != this.selFmVersion) {
      try {

        // remember the selected focus matrix version
        Long selectedFMVerisonId = this.selFmVersion.getFmVersion().getId();

        // clear and load the versions map
        this.fmVersionMap.clear();
        getFMVersionsForPIDCVersion();

        // set the selected focus matrix version
        for (FocusMatrixVersionClientBO fmVersionBO : this.fmVersionMap.values()) {
          if (CommonUtils.isEqual(fmVersionBO.getFmVersion().getId(), selectedFMVerisonId)) {
            setSelFmVersion(fmVersionBO);
          }
        }

      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * load the focus matrix for working set
   */
  private void refreshFocusMatrix(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // just reset the loading flag , calling the web service part will be done when the map is needed in UI
    refreshFocusMatrixVersions(chDataInfoMap);
    getFocusMatrixWorkingSet().resetFocusMatrixDefinitionLoaded();
  }

  /**
   * @return the ucDataHandler
   */
  public UseCaseDataHandler getUcDataHandler() {
    return this.ucDataHandler;
  }


  /**
   * @return the useCaseRootNode
   */
  public UseCaseRootNode getUseCaseRootNode() {
    return this.useCaseRootNode;
  }


  /**
   * @return the projFavUcRootNode
   */
  public ProjFavUcRootNode getProjFavUcRootNode() {
    return this.projFavUcRootNode;
  }


  /**
   * @param id Long
   * @return Map<Long, FocusMatrix>
   */
  public FocusMatrixDetailsModel getFocusMatrixEntries(final Long id) {
    FocusMatrixServiceClient fmServiceClient = new FocusMatrixServiceClient();
    FocusMatrixDetailsModel focusMatrixModel = null;
    try {
      focusMatrixModel = fmServiceClient.getFocusMatrixForVersion(id);

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return focusMatrixModel;
  }


  /**
   * @return the pidcDataHandler
   */
  public PidcDataHandler getPidcDataHandler() {
    return this.pidcVersionBO.getPidcDataHandler();
  }


  /**
   * @return the set of attrs which can be marked as relevant for focus matrix. Visible attrs which are not deleted and
   *         do not belong to the attribute class "doc" and "sthr" are considered. <br>
   *         Key - Attribute ID <br>
   *         Value - Attribute
   */
  public ConcurrentMap<PidcVersionAttribute, Attribute> getFocusMatrixApplicableAttrMap() {
    Map<Long, PidcVersionAttribute> allPIDCAttrs = this.pidcVersionBO.getAttributes();
    List<Long> excludeAttrClassesList = getExcludeAttrClassesList();

    ConcurrentMap<PidcVersionAttribute, Attribute> fMatrixAttrMap = new ConcurrentHashMap<>();

    for (PidcVersionAttribute projAttr : allPIDCAttrs.values()) {
      PidcVersionAttributeBO pidcVersAttrHandler = new PidcVersionAttributeBO(projAttr, this.pidcVersionBO);
      Attribute attr = this.pidcVersionBO.getPidcDataHandler().getAttributeMap().get(projAttr.getAttrId());
      if (excludeAttrClassesList.contains(attr.getCharacteristicId()) || !pidcVersAttrHandler.isVisible() ||
          attr.isDeleted()) {
        continue;
      }
      fMatrixAttrMap.put(projAttr, attr);
    }

    return fMatrixAttrMap;

  }


  /**
   * @return the list of attr char classes which are excluded from focus matrix
   */
  private List<Long> getExcludeAttrClassesList() {
    // ICDM-1611
    List<Long> excludeAttrClassesList = new ArrayList<Long>();

    for (Characteristic attrClass : this.pidcVersionBO.getPidcDataHandler().getCharacteristicMap().values()) {

      if (CommonUtils.isEqual(attrClass.getFocusMatrixYn(), ApicConstants.CODE_NO)) {
        excludeAttrClassesList.add(attrClass.getId());
      }

    }
    return excludeAttrClassesList;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersionBO.getPidcVersion();
  }


  /**
   * @return the pidcVersionBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }


  /**
   * @param ucItem UsecaseClientBO
   * @param attribute Attribute
   * @return ucp attr id
   */
  public Long getMappingEntity(final IUseCaseItemClientBO ucItem, final Attribute attribute) {
    if (ucItem instanceof UsecaseClientBO) {
      UsecaseClientBO usecase = (UsecaseClientBO) ucItem;

      if (usecase.canMapAttributes()) {
        UsecaseModel usecaseModel =
            this.ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucItem.getID());
        usecaseModel.getUcItemAttrMap();
      }
    }
    UsecaseModel usecaseModel =
        this.ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucItem.getID());
    usecaseModel.getAttrToUcpAttrMap();
    return null;
  }

  /**
   * @param listOfFavItems List<IUseCaseItemClientBO>
   * @param parentElementUCItem IUseCaseItemClientBO
   * @return SortedSet<IUseCaseItemClientBO>
   */
  public SortedSet<IUseCaseItemClientBO> getChildFocusMatrixItems(final List<IUseCaseItemClientBO> listOfFavItems,
      final IUseCaseItemClientBO parentElementUCItem) {

    if (parentElementUCItem instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO ucGroup = (UseCaseGroupClientBO) parentElementUCItem;
      SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<IUseCaseItemClientBO>();
      if (CommonUtils.isNullOrEmpty(ucGroup.getChildGroupSet(false))) {
        childSet.addAll(getFocusMatrixRelevantUseCases(ucGroup.getUseCases(false), listOfFavItems));
      }
      else {
        for (UseCaseGroupClientBO childGrp : ucGroup.getChildGroupSet(false)) {
          if (!getChildFocusMatrixItems(listOfFavItems, childGrp).isEmpty()) {
            childSet.add(childGrp);
          }
        }
      }
      return childSet;
    }
    return parentElementUCItem.getChildFocusMatrixItems();
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @param set
   * @param listOfFavItems
   * @return set of use cases
   */
  private final Set<UsecaseClientBO> getFocusMatrixRelevantUseCases(final Set<UsecaseClientBO> set,
      final List<IUseCaseItemClientBO> listOfFavItems) {

    Set<UsecaseClientBO> ucSet = new HashSet<UsecaseClientBO>();

    for (UsecaseClientBO useCase : set) {
      if (listOfFavItems.contains(useCase)) {
        UsecaseEditorModel usecaseEditorModel =
            getUcDataHandler().getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(useCase.getUseCase().getId());
        useCase.setUsecaseEditorModel(usecaseEditorModel);
        addToSetIfFmRelevant(ucSet, useCase);
      }
    }

    return ucSet;
  }

  /**
   * @param ucSet
   * @param usecaseClientBO
   */
  private void addToSetIfFmRelevant(final Set<UsecaseClientBO> ucSet, final UsecaseClientBO usecaseClientBO) {
    if ((usecaseClientBO.isFocusMatrixRelevant(true)) || ((!usecaseClientBO.getChildFocusMatrixItems().isEmpty()) &&
        !usecaseClientBO.isDeleted() && !usecaseClientBO.getMappableItems().isEmpty())) {
      ucSet.add(usecaseClientBO);
    }
  }

  /**
   * @return FocusMatrixVersionClientBO
   */
  public FocusMatrixVersionClientBO getFocusMatrixWorkingSet() {
    if (null != this.fmVersionMap) {
      for (FocusMatrixVersionClientBO fmVersion : this.fmVersionMap.values()) {
        if (fmVersion.isWorkingSet()) {
          return fmVersion;
        }
      }
    }
    return null;
  }

  /**
   * @param fmVersionClient
   * @return
   * @throws ApicWebServiceException
   */
  private Map<Long, FocusMatrixVersionClientBO> getFMVersionsForPIDCVersion() throws ApicWebServiceException {
    FocusMatrixVersionServiceClient fmVersionClient = new FocusMatrixVersionServiceClient();

    if ((null == this.fmVersionMap) || this.fmVersionMap.isEmpty()) {
      this.fmVersionMap = new HashMap<>();
      Map<Long, FocusMatrixVersion> fmVersionsForPidc =
          fmVersionClient.getFocusMatrixVersionForPidcVers(this.pidcVersionBO.getPidcVersion().getId());
      for (com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion fmVersion : fmVersionsForPidc.values()) {
        FocusMatrixVersionClientBO fmVersionClientBO = new FocusMatrixVersionClientBO(fmVersion, this);
        this.fmVersionMap.put(fmVersion.getId(), fmVersionClientBO);
      }
    }
    return this.fmVersionMap;
  }

  /**
   * @return the selFmVersion
   * @throws ApicWebServiceException
   */
  // ICDM-2612
  public FocusMatrixVersionClientBO getSelFmVersion() {
    try {
      if (this.selFmVersion == null) {

        Map<Long, FocusMatrixVersionClientBO> fmVersionForPidcVers;

        fmVersionForPidcVers = getFMVersionsForPIDCVersion();
        for (FocusMatrixVersionClientBO fmVersionClientBO : fmVersionForPidcVers.values()) {
          if (fmVersionClientBO.isWorkingSet()) {
            this.selFmVersion = fmVersionClientBO;
          }
        }

      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return this.selFmVersion;
  }

  /**
   * @return sorted collection of focus matrix versions
   * @throws ApicWebServiceException
   */
  // ICDM-2569
  public SortedSet<FocusMatrixVersionClientBO> getFocusMatrixVersionsSorted() throws ApicWebServiceException {
    return new TreeSet<FocusMatrixVersionClientBO>(getFMVersionsForPIDCVersion().values());
  }

  /**
   * @param selectedItem
   */
  public void setSelFmVersion(final FocusMatrixVersionClientBO selectedItem) {
    this.selFmVersion = selectedItem;
  }

  /**
   * @param id focus matrix verison id
   * @return Set<FocusMatrixVersionAttrClientBO>
   * @throws ApicWebServiceException exception while getting version attributes from server
   */
  public Set<FocusMatrixVersionAttrClientBO> getFocusMatrixVersionAttributes(final Long id)
      throws ApicWebServiceException {
    Set<FocusMatrixVersionAttrClientBO> fmVerAttrSet = this.fmVersAttrMap.get(id);
    if (null == fmVerAttrSet) {
      fmVerAttrSet = new HashSet<>();
      FocusMatrixVersionAttrServiceClient fmVersAttrClient = new FocusMatrixVersionAttrServiceClient();
      Map<Long, FocusMatrixVersionAttr> focusMatrixAttrForVersion = fmVersAttrClient.getFocusMatrixAttrForVersion(id);
      for (FocusMatrixVersionAttr focusMatrixVersionAttr : focusMatrixAttrForVersion.values()) {
        FocusMatrixVersionAttrClientBO fmVersAttrBo = new FocusMatrixVersionAttrClientBO(focusMatrixVersionAttr,
            this.pidcVersionBO.getPidcDataHandler(), this.pidcVersionBO.getPidcVersion());
        fmVerAttrSet.add(fmVersAttrBo);
      }
      this.fmVersAttrMap.put(id, fmVerAttrSet);
      return fmVerAttrSet;
    }
    return fmVerAttrSet;
  }

  /**
   * @return the allAttrNeededForExport
   */
  public boolean isAllAttrNeededForExport() {
    return this.allAttrNeededForExport;
  }

  /**
   * @param allAttrNeededForExport the allAttrNeededForExport to set
   */
  public void setAllAttrNeededForExport(final boolean allAttrNeededForExport) {
    this.allAttrNeededForExport = allAttrNeededForExport;
  }
}

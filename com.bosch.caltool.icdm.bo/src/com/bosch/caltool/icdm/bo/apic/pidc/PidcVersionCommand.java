/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcCocWpUpdationBO;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcSubVarCocWpCommand;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVariantCocWpCommand;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVersCocWpCommand;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefinitionLoader;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefintionCommand;
import com.bosch.caltool.icdm.bo.rm.PidcRmProjCharCommand;
import com.bosch.caltool.icdm.bo.rm.PidcRmProjCharacterLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVersCocWp;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;

/**
 * @author mkl2cob
 */
public class PidcVersionCommand extends AbstractCommand<PidcVersion, PidcVersionLoader> {

  private boolean isCopyAttr;

  private boolean isNewRevison;


  /**
   * @param serviceData ServiceData
   * @param inputData PIDCVersion
   * @param isUpdate boolean
   * @param isDelete boolean
   * @throws IcdmException Exception
   */
  public PidcVersionCommand(final ServiceData serviceData, final PidcVersion inputData, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, inputData, new PidcVersionLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : verifyIsUpdate(isUpdate));
  }

  /**
   * @param isUpdate
   * @return
   */
  private static COMMAND_MODE verifyIsUpdate(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * @param serviceData instance
   * @param inputData pidc version
   * @param isCopyAttr
   * @param isCreate
   * @param isNewVersion
   * @throws IcdmException exception
   */
  public PidcVersionCommand(final ServiceData serviceData, final PidcVersion inputData, final boolean isCopyAttr,
      final boolean isCreate, final boolean isNewVersion) throws IcdmException {
    super(serviceData, inputData, new PidcVersionLoader(serviceData), COMMAND_MODE.CREATE);
    this.isCopyAttr = isCopyAttr;
    this.isNewRevison = isNewVersion;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  protected void create() throws IcdmException {
    TPidcVersion entity = new TPidcVersion();
    setValuesToEntity(entity);
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);

    if (null != entity.getTabvProjectidcard().getTPidcVersions()) {
      entity.getTabvProjectidcard().getTPidcVersions().add(entity);
    }
    else {
      entity.getTabvProjectidcard().setTPidcVersions(new HashSet<>(Arrays.asList(entity)));
    }

    createPidcRmDef(entity);
    if (!this.isNewRevison) {
      createFocusMatrixVersion(entity);
    }

    List<TabvProjectAttr> tabvProjectAttrs = entity.getTabvProjectAttrs();
    if (tabvProjectAttrs == null) {
      tabvProjectAttrs = new ArrayList<>();
      entity.setTabvProjectAttrs(tabvProjectAttrs);
    }

    if (this.isCopyAttr) {
      List<TabvProjectAttr> newProjAttrs = new ArrayList<>();
      if (null != getInputData().getParentPidcVerId()) {
        newProjAttrs = copyProjectAttrs(entity);
      }
      if (!newProjAttrs.isEmpty()) {
        entity.setTabvProjectAttrs(newProjAttrs);
      }
    }

    if (this.isNewRevison) {
      createNewRevisionForPidcVersion(entity, tabvProjectAttrs);
    }
  }

  /**
   * @param entity
   * @param tabvProjectAttrs
   * @throws IcdmException
   */
  private void createNewRevisionForPidcVersion(final TPidcVersion entity, final List<TabvProjectAttr> tabvProjectAttrs)
      throws IcdmException {
    List<TabvProjectAttr> newProjAttrs = new ArrayList<>();
    TPidcVersion parentDbVersion =
        new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getParentPidcVerId());
    if (null != getInputData().getParentPidcVerId()) {
      newProjAttrs = copyProjAttrs(entity, parentDbVersion);

    }
    if (!newProjAttrs.isEmpty()) {
      tabvProjectAttrs.addAll(newProjAttrs);
    }
    if (!parentDbVersion.getTabvProjectVariants().isEmpty()) {
      createpidcDetStructure(parentDbVersion, entity);
      createVariantRevisions(entity, parentDbVersion);
    }
    copyFocusMatrixVersion(entity, parentDbVersion);

    // copy pal WPs to new pidc version
    copyWorkPackageMetadata(entity, parentDbVersion);

    // copy CoC Wp to new pidc version
    if (CommonUtils.isNotEmpty(parentDbVersion.gettPidcVersCocWp())) {
      copyPidcVersCoCWorkPackage(entity, parentDbVersion);
    }
  }


  /**
   * Method to copy pal wp to new pidc version from existing pidc version
   *
   * @param entity - newly created pidc version
   * @param parentDbVersion - parent pidc version
   * @throws IcdmException
   */
  private void copyWorkPackageMetadata(final TPidcVersion entity, final TPidcVersion parentDbVersion)
      throws IcdmException {
    for (TA2lWorkPackage dbA2lWp : parentDbVersion.getTA2lWorkPackageList()) {
      A2lWorkPackage a2lWp = new A2lWorkPackage();
      a2lWp.setName(dbA2lWp.getWpName());
      a2lWp.setDescription(dbA2lWp.getWpDesc());
      a2lWp.setNameCustomer(dbA2lWp.getWpNameCust());
      a2lWp.setParentA2lWpId(dbA2lWp.getA2lWpId());
      a2lWp.setPidcVersId(entity.getPidcVersId());
      A2lWorkPackageCommand createA2lWpcommand = new A2lWorkPackageCommand(getServiceData(), a2lWp, false, false);
      executeChildCommand(createA2lWpcommand);
    }

  }

  /**
   * Method to copy PidcVersCoCWp to new pidc version from existing pidc version
   *
   * @param newPidcVersion - newly created pidc version
   * @param parentDbVersion - parent pidc version
   * @throws IcdmException
   */

  private void copyPidcVersCoCWorkPackage(final TPidcVersion newPidcVersion, final TPidcVersion parentDbVersion)
      throws IcdmException {
    PidcCocWpUpdationBO pidcCoCWpUpdationSeviceData = new PidcCocWpUpdationBO(getServiceData());
    for (TPidcVersCocWp parentPidcVersCoCWp : parentDbVersion.gettPidcVersCocWp()) {
      PidcVersCocWp versCoCWpNew = new PidcVersCocWp();
      versCoCWpNew.setPidcVersId(newPidcVersion.getPidcVersId());
      versCoCWpNew.setWPDivId(parentPidcVersCoCWp.getTWrkpkgdiv().getWpDivId());
      versCoCWpNew.setUsedFlag(parentPidcVersCoCWp.getUsedFlag());
      versCoCWpNew.setAtChildLevel(CommonUtils.getBooleanType(parentPidcVersCoCWp.getIsAtChildLevel()));
      PidcVersCocWpCommand createNewVersCoCWpcommand = new PidcVersCocWpCommand(getServiceData(), versCoCWpNew, false);
      executeChildCommand(createNewVersCoCWpcommand);
      PidcVersCocWp pidcVarCocWP = createNewVersCoCWpcommand.getNewData();
      pidcCoCWpUpdationSeviceData.updatePidcVersCocWpReferenceEntity(pidcVarCocWP);
    }

  }

  /**
   * @param entity
   * @param parentDbVersion
   * @throws IcdmException
   */
  private void copyFocusMatrixVersion(final TPidcVersion entity, final TPidcVersion parentDbVersion)
      throws IcdmException {
    FocusMatrixVersionLoader focusMatrixVerLoader = new FocusMatrixVersionLoader(getServiceData());
    Long workingSetVersionId = focusMatrixVerLoader.getWorkingSetVersionId(parentDbVersion.getPidcVersId());
    TFocusMatrixVersion parentFocusMatrixVers = focusMatrixVerLoader.getEntityObject(workingSetVersionId);
    FocusMatrixVersion parentVersion = focusMatrixVerLoader.getDataObjectByID(parentFocusMatrixVers.getFmVersId());
    FocusMatrixVersion fmVer = new FocusMatrixVersion();
    fmVer.setPidcVersId(entity.getPidcVersId());
    fmVer.setName(parentFocusMatrixVers.getName());
    fmVer.setRevNum(parentFocusMatrixVers.getRevNumber());
    fmVer.setStatus(parentFocusMatrixVers.getStatus());
    fmVer.setLink(parentFocusMatrixVers.getLink());
    fmVer.setRemark(parentFocusMatrixVers.getRemark());
    fmVer.setReviewedUser(
        parentFocusMatrixVers.getReviewedUser() == null ? null : parentFocusMatrixVers.getReviewedUser().getUserId());
    fmVer.setReviewedDate(parentVersion.getReviewedDate() == null ? null : parentVersion.getReviewedDate());
    fmVer.setRvwStatus(parentFocusMatrixVers.getRvwStatus());
    FocusMatrixVersionCommand fmVerCmd = new FocusMatrixVersionCommand(getServiceData(), fmVer, false, false);
    executeChildCommand(fmVerCmd);
    FocusMatrixVersion createdFmVers = fmVerCmd.getNewData();
    createFocusMatrix(createdFmVers, parentFocusMatrixVers);
  }

  /**
   * @param createdFmVers
   * @param parentFocusMatrixVers
   * @throws IcdmException
   */
  private void createFocusMatrix(final FocusMatrixVersion createdFmVers,
      final TFocusMatrixVersion parentFocusMatrixVers)
      throws IcdmException {
    for (TFocusMatrix tFocusMatrix : parentFocusMatrixVers.getTFocusMatrixs()) {
      FocusMatrix newFocusMatrix = new FocusMatrix();
      newFocusMatrix.setAttrId(tFocusMatrix.getTabvAttribute().getAttrId());
      newFocusMatrix.setColorCode(tFocusMatrix.getColorCode());
      newFocusMatrix.setComments(tFocusMatrix.getComments());
      newFocusMatrix.setFmVersId(createdFmVers.getId());
      newFocusMatrix.setIsDeleted(tFocusMatrix.getDeletedFlag().equals(ApicConstants.CODE_YES));
      newFocusMatrix.setLink(tFocusMatrix.getLink());
      if (null != tFocusMatrix.getTabvUseCaseSection()) {
        newFocusMatrix.setSectionId(tFocusMatrix.getTabvUseCaseSection().getSectionId());
      }
      newFocusMatrix.setUseCaseId(tFocusMatrix.getTabvUseCas().getUseCaseId());
      newFocusMatrix
          .setUcpaId(tFocusMatrix.getTabvUcpAttr() == null ? null : tFocusMatrix.getTabvUcpAttr().getUcpaId());
      FocusMatrixCommand focusMatrixCommand = new FocusMatrixCommand(getServiceData(), newFocusMatrix, false, false);
      executeChildCommand(focusMatrixCommand);
    }
  }

  private List<TabvProjectAttr> copyProjectAttrs(final TPidcVersion entity) throws IcdmException {
    PidcVersionAttributeModel model =
        new ProjectAttributeLoader(getServiceData()).createModel(getInputData().getParentPidcVerId());
    List<TabvProjectAttr> projAttrSet = new ArrayList<>();
    for (PidcVersionAttribute verAttr : model.getPidcVersAttrMap().values()) {
      if (canCopyAttr(verAttr)) {
        verAttr.setPidcVersId(entity.getPidcVersId());
        verAttr.setVersion(1L);
        TabvProjectAttr newProjattr = createVersionAttr(verAttr);
        projAttrSet.add(newProjattr);
      }
    }
    return projAttrSet;
  }

  /**
   * @param model
   * @param entity
   * @param parentDbVersion
   * @throws IcdmException
   */
  private void createVariantRevisions(final TPidcVersion entity, final TPidcVersion parentDbVersion)
      throws IcdmException {
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader verLoader = new PidcVersionLoader(getServiceData());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    if (null != getInputData().getParentPidcVerId()) {
      for (TabvProjectVariant dbVariant : parentDbVersion.getTabvProjectVariants()) {
        // copy Risk Definitions of the parent
        TPidcRmDefinition dbRiskDefVar = dbVariant.getTPidcRmDefinition();
        // Create Command for PIDC Variant
        PidcRmDefinition newRmDef;
        PidcVariantData pidcVariantData = new PidcVariantData();
        if (null != dbRiskDefVar) {
          PidcRmDefinitionLoader riskDefLoader = new PidcRmDefinitionLoader(getServiceData());
          PidcRmDefinition oldpidcRmDef = riskDefLoader.createDataObject(dbRiskDefVar);
          newRmDef = createPidcRmDefCommands(oldpidcRmDef);
          pidcVariantData.setRiskDefId(newRmDef.getId());
        }

        PidcVariant variantToCreate = varLoader.getDataObjectByID(dbVariant.getVariantId());
        variantToCreate.setId(null);


        variantToCreate.setNameValueId(dbVariant.getTabvAttrValue().getValueId());

        variantToCreate.setPidcVersionId(entity.getPidcVersId());

        variantToCreate.setDeleted(dbVariant.getDeletedFlag().equals(ApicConstants.CODE_YES));
        pidcVariantData.setDestPidcVar(variantToCreate);
        pidcVariantData.setVarNameAttrValue(attrValLoader.getDataObjectByID(dbVariant.getTabvAttrValue().getValueId()));
        pidcVariantData.setPidcVersion(verLoader.getDataObjectByID(entity.getPidcVersId()));
        pidcVariantData.setDummyVariantId(dbVariant.getVariantId());
        pidcVariantData.setNewRevision(true);
        createVariant(pidcVariantData, parentDbVersion);
      }
    }
  }

  /**
   * @param parentDbVersion reference version
   * @param entity pidc version entity
   * @throws IcdmException
   */
  private void createpidcDetStructure(final TPidcVersion parentDbVersion, final TPidcVersion entity)
      throws IcdmException {
    for (TabvPidcDetStructure parentDetStructure : parentDbVersion.getTabvPidcDetStructures()) {
      PidcDetStructure newStructure = new PidcDetStructure();
      setDetStructureProperties(parentDetStructure, newStructure, entity);
      PidcDetStructureCommand cmd = new PidcDetStructureCommand(getServiceData(), newStructure, false, false, true);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param detStructure
   * @param newStructure
   * @param entity
   */
  private void setDetStructureProperties(final TabvPidcDetStructure parentDetStructure,
      final PidcDetStructure newStructure, final TPidcVersion entity) {
    newStructure.setAttrId(parentDetStructure.getTabvAttribute().getAttrId());
    newStructure.setPidcVersId(entity.getPidcVersId());
    newStructure.setPidAttrLevel(parentDetStructure.getPidAttrLevel());
  }

  /**
   * @param variantToCreate
   * @param pidcVariantData
   * @param model
   * @param parentDbVersion
   * @param newPidcVers
   * @throws IcdmException
   */
  private void createVariant(final PidcVariantData pidcVariantData, final TPidcVersion parentDbVersion)
      throws IcdmException {
    PidcVariantCommand variantCmd = new PidcVariantCommand(getServiceData(), pidcVariantData, false);
    executeChildCommand(variantCmd);
    copyVarAttrs(pidcVariantData, variantCmd.getNewData().getId());
    copyVarCoCWp(pidcVariantData, variantCmd.getNewData().getId());
    createSubVariants(variantCmd.getNewData(), pidcVariantData, parentDbVersion);
  }


  /**
   * @param newVariant
   * @param pidcVariantData
   * @param model
   * @param parentDbVersion
   * @throws IcdmException
   */
  private void createSubVariants(final PidcVariant newVariant, final PidcVariantData pidcVariantData,
      final TPidcVersion parentDbVersion)
      throws IcdmException {

    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    PidcSubVariantLoader subVarLoader = new PidcSubVariantLoader(getServiceData());
    for (TabvProjectVariant tabVProjVar : parentDbVersion.getTabvProjectVariants()) {
      if (tabVProjVar.getVariantId() == pidcVariantData.getDummyVariantId()) {
        for (TabvProjectSubVariant dbSubVar : tabVProjVar.getTabvProjectSubVariants()) {
          PidcSubVariant subVar = subVarLoader.getDataObjectByID(dbSubVar.getSubVariantId());
          subVar.setId(null);
          PidcSubVariantData subvarData = new PidcSubVariantData();
          subvarData.setDestPidcSubVar(subVar);
          subvarData.setDummySubVarId(dbSubVar.getSubVariantId());
          subvarData.setNewRevison(true);
          subvarData.setPidcVariantId(newVariant.getId());
          subvarData.setSubvarNameAttrValue(attrValLoader.getDataObjectByID(dbSubVar.getTabvAttrValue().getValueId()));
          PidcSubVariantCommand pidcSubvarCmd = new PidcSubVariantCommand(getServiceData(), subvarData, false);
          executeChildCommand(pidcSubvarCmd);
          createSubVarAttrs(subvarData, pidcSubvarCmd.getNewData().getId());
          copySubVarCoCWp(subvarData, pidcSubvarCmd.getNewData().getId());
        }
      }
    }
  }

  /**
   * @param model2
   * @param entity
   * @param parentDbVersion2
   * @return
   * @throws IcdmException
   */
  private List<TabvProjectAttr> copyProjAttrs(final TPidcVersion entity, final TPidcVersion parentDbVersion)
      throws IcdmException {

    List<TabvProjectAttr> projAttrSet = new ArrayList<>();
    PidcVersionAttributeLoader versAttrLoader = new PidcVersionAttributeLoader(getServiceData());

    for (TabvProjectAttr verAttr : parentDbVersion.getTabvProjectAttrs()) {
      PidcVersionAttribute parentVersAttr = versAttrLoader.getDataObjectByID(verAttr.getPrjAttrId());
      parentVersAttr.setPidcVersId(entity.getPidcVersId());
      parentVersAttr.setAttrId(verAttr.getTabvAttribute().getAttrId());
      parentVersAttr.setVersion(1L);
      parentVersAttr.setId(null);
      TabvProjectAttr newProjattr = createVersionAttr(parentVersAttr);
      projAttrSet.add(newProjattr);
    }
    return projAttrSet;
  }

  private List<TabvVariantsAttr> copyVarAttrs(final PidcVariantData pidcVariantData, final Long variantId)
      throws IcdmException {
    List<TabvVariantsAttr> varAttrList = new ArrayList<>();
    PidcVariantAttributeLoader varAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    TabvProjectVariant parentVariant =
        new PidcVariantLoader(getServiceData()).getEntityObject(pidcVariantData.getDummyVariantId());
    for (TabvVariantsAttr parentVarAttr : parentVariant.getTabvVariantsAttrs()) {
      PidcVariantAttribute varAttr = varAttrLoader.getDataObjectByID(parentVarAttr.getVarAttrId());
      varAttr.setPidcVersionId(pidcVariantData.getPidcVersion().getId());
      varAttr.setVariantId(variantId);
      varAttr.setAttrId(parentVarAttr.getTabvAttribute().getAttrId());
      varAttr.setVersion(1L);
      varAttr.setId(null);
      TabvVariantsAttr newVarAttr = createPidcVarAttr(varAttr);
      varAttrList.add(newVarAttr);
    }
    return varAttrList;

  }

  /**
   * Method to copy VariantCoCWp to new pidc version from existing pidc version
   *
   * @param newVariantId - newly created pidc variant Id
   * @param parentPidcVariantData - parent variant data
   * @throws IcdmException
   */


  private void copyVarCoCWp(final PidcVariantData parentPidcVariantData, final Long newVariantId) throws IcdmException {
    TabvProjectVariant parentVariant =
        new PidcVariantLoader(getServiceData()).getEntityObject(parentPidcVariantData.getDummyVariantId());
    PidcCocWpUpdationBO pidcCoCWpUpdationSeviceData = new PidcCocWpUpdationBO(getServiceData());
    for (TPidcVariantCocWp parentVarCoCWp : parentVariant.gettPidcVarCocWp()) {
      PidcVariantCocWp varCoCWpNew = new PidcVariantCocWp();
      varCoCWpNew.setWPDivId(parentVarCoCWp.getTwrkpkgdiv().getWpDivId());
      varCoCWpNew.setPidcVariantId(newVariantId);
      varCoCWpNew.setUsedFlag(parentVarCoCWp.getUsedFlag());
      varCoCWpNew.setAtChildLevel(CommonUtils.getBooleanType(parentVarCoCWp.getIsAtChildLevel()));
      PidcVariantCocWpCommand createNewPidcVarCocWpCmd =
          new PidcVariantCocWpCommand(getServiceData(), varCoCWpNew, false, false);
      executeChildCommand(createNewPidcVarCocWpCmd);
      PidcVariantCocWp pidcVarCocWP = createNewPidcVarCocWpCmd.getNewData();
      pidcCoCWpUpdationSeviceData.updatePidcVarsCocWpReferenceEntity(pidcVarCocWP,
          createNewPidcVarCocWpCmd.getCmdMode());
    }
  }


  private List<TabvProjSubVariantsAttr> createSubVarAttrs(final PidcSubVariantData subVarData,
      final Long newSubVariantId)
      throws IcdmException {
    PidcSubVariantAttributeLoader subVarAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjectSubVariant tabvProjectSubVariant =
        new PidcSubVariantLoader(getServiceData()).getEntityObject(subVarData.getDummySubVarId());
    List<TabvProjSubVariantsAttr> subVarAttrList = new ArrayList<>();
    for (TabvProjSubVariantsAttr parentSubVarAttr : tabvProjectSubVariant.getTabvProjSubVariantsAttrs()) {
      PidcSubVariantAttribute subVarAttr = subVarAttrLoader.getDataObjectByID(parentSubVarAttr.getSubVarAttrId());
      subVarAttr.setPidcVersionId(subVarData.getPidcVersionId());
      subVarAttr.setVersion(1L);
      subVarAttr.setSubVariantId(newSubVariantId);
      subVarAttr.setId(null);
      subVarAttr.setVariantId(subVarData.getPidcVariantId());
      subVarAttr.setAttrId(parentSubVarAttr.getTabvAttribute().getAttrId());
      TabvProjSubVariantsAttr newSubVarAttr = createPidcSubVarAttr(subVarAttr);
      subVarAttrList.add(newSubVarAttr);
    }
    return subVarAttrList;
  }


  /**
   * @param subVarAttr
   * @return
   * @throws IcdmException
   */


  private TabvProjSubVariantsAttr createPidcSubVarAttr(final PidcSubVariantAttribute subVarAttr) throws IcdmException {
    PidcSubVariantAttributeCommand cmd = new PidcSubVariantAttributeCommand(getServiceData(), subVarAttr, false, false);
    executeChildCommand(cmd);
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    return loader.getEntityObject(cmd.getNewData().getId());
  }

  /**
   * Method to copy SubVariantCoCWp to new pidc version from existing pidc version
   *
   * @param newSubVariantId - newly created pidc Subvariant Id
   * @param parentSubVarData - parent Subvariant data
   * @throws IcdmException
   */

  private void copySubVarCoCWp(final PidcSubVariantData parentSubVarData, final Long newSubVariantId)
      throws IcdmException {
    TabvProjectSubVariant parentSubVariant =
        new PidcSubVariantLoader(getServiceData()).getEntityObject(parentSubVarData.getDummySubVarId());
    PidcCocWpUpdationBO pidcCoCWpUpdationSeviceData = new PidcCocWpUpdationBO(getServiceData());
    for (TPidcSubVarCocWp parentSubVarCoC : parentSubVariant.gettPidcSubVarCocWp()) {
      PidcSubVarCocWp varSubCoCNew = new PidcSubVarCocWp();
      varSubCoCNew.setWPDivId(parentSubVarCoC.getTwrkpkgdiv().getWpDivId());
      varSubCoCNew.setPidcSubVarId(newSubVariantId);
      varSubCoCNew.setUsedFlag(parentSubVarCoC.getUsedFlag());
      PidcSubVarCocWpCommand createNewPidcSubVarCocWpCmd =
          new PidcSubVarCocWpCommand(getServiceData(), varSubCoCNew, false, false);
      executeChildCommand(createNewPidcSubVarCocWpCmd);
      // to update the entity relations for pidcSubVarCocWp
      PidcSubVarCocWp pidcSubVarCocWp = createNewPidcSubVarCocWpCmd.getNewData();
      pidcCoCWpUpdationSeviceData.updatePidcSubVarsCocWpReferenceEntity(pidcSubVarCocWp,
          createNewPidcSubVarCocWpCmd.getCmdMode());
    }
  }


  /**
   * @param varAttr
   * @return
   * @throws IcdmException
   */
  private TabvVariantsAttr createPidcVarAttr(final PidcVariantAttribute varAttr) throws IcdmException {
    PidcVariantAttributeCommand cmd = new PidcVariantAttributeCommand(getServiceData(), varAttr, false, false);
    executeChildCommand(cmd);
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    return loader.getEntityObject(cmd.getNewData().getId());
  }

  /**
   * @param verAttr
   * @return
   * @throws IcdmException
   */
  private TabvProjectAttr createVersionAttr(final PidcVersionAttribute verAttr) throws IcdmException {
    PidcVersionAttributeCommand verAttrCmd = new PidcVersionAttributeCommand(getServiceData(), verAttr, false, false);
    executeChildCommand(verAttrCmd);
    PidcVersionAttributeLoader pidcAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    return pidcAttrLoader.getEntityObject(verAttrCmd.getNewData().getId());
  }

  /**
   * @param verAttr
   * @return
   * @throws DataException
   */
  public boolean canCopyAttr(final IProjectAttribute verAttr) throws DataException {

    // Exclude undefined attributes and attributes defined at variant level
    if ((verAttr.getId() == null) ||
        (ApicConstants.USED_NOTDEF_DISPLAY.equals(verAttr.getUsedFlag()) || verAttr.isAtChildLevel())) {
      return false;
    }
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    final int attrLevel = attrLoader.getDataObjectByID(verAttr.getAttrId()).getLevel().intValue();

    if (attrLevel > 0) {
      return false;
    }

    // Ignore name, variant attributes
    if ((attrLevel == ApicConstants.PROJECT_NAME_ATTR) || (attrLevel == ApicConstants.VARIANT_CODE_ATTR) ||
        (attrLevel == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
      return false;
    }
    // If the source pidc attr is not readable and hidden then do not copy the attr
    return !verAttr.isAttrHidden();
  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void createFocusMatrixVersion(final TPidcVersion entity) throws IcdmException {
    FocusMatrixVersion fmVer = new FocusMatrixVersion();
    fmVer.setPidcVersId(entity.getPidcVersId());
    fmVer.setName("Working Set");
    fmVer.setRevNum(0L);
    fmVer.setStatus("W");

    FocusMatrixVersionCommand fmVerCmd = new FocusMatrixVersionCommand(getServiceData(), fmVer, false, false);
    executeChildCommand(fmVerCmd);
  }


  private void createPidcRmDef(final TPidcVersion entity) throws IcdmException {
    Set<PidcRmDefinition> pidcRiskDefList;
    if (null == getInputData().getParentPidcVerId()) {
      pidcRiskDefList = createDefaultRiskDef(entity);
    }
    else {
      pidcRiskDefList =
          new PidcRmDefinitionLoader(getServiceData()).getPidRmDefintions(getInputData().getParentPidcVerId());
    }
    if (this.isNewRevison && !CommonUtils.isNullOrEmpty(pidcRiskDefList)) {
      for (PidcRmDefinition pidcRmDef : pidcRiskDefList) {
        if ((null != pidcRmDef.getIsVariant()) && ("N").equalsIgnoreCase(pidcRmDef.getIsVariant())) {
          pidcRmDef.setPidcVersId(entity.getPidcVersId());
          PidcRmDefinition newRmDef = createPidcRmDefCommands(pidcRmDef);
          createPidcProjChar(pidcRmDef, newRmDef);
        }
      }
    }
  }


  /**
   * @param oldPidcRmDef
   * @param newRmDef
   * @param entity
   * @throws IcdmException
   */
  private void createPidcProjChar(final PidcRmDefinition oldPidcRmDef, final PidcRmDefinition newRmDef)
      throws IcdmException {
    PidcRmProjCharacterLoader projCharLoader = new PidcRmProjCharacterLoader(getServiceData());
    Map<Long, PidcRmProjCharacter> pidRmProjCharMap = projCharLoader.getPidRmProjChar(oldPidcRmDef.getId());
    for (PidcRmProjCharacter pidcRmProjChar : pidRmProjCharMap.values()) {
      pidcRmProjChar.setRmDefId(newRmDef.getId());
      PidcRmProjCharCommand cmd = new PidcRmProjCharCommand(getServiceData(), pidcRmProjChar, false);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param oldPidcRmDef
   * @param riskDefLoader
   * @return
   * @throws IcdmException
   */
  private PidcRmDefinition createPidcRmDefCommands(final PidcRmDefinition oldPidcRmDef) throws IcdmException {
    PidcRmDefintionCommand rmCmd = new PidcRmDefintionCommand(getServiceData(), oldPidcRmDef);
    executeChildCommand(rmCmd);
    return rmCmd.getNewData();
  }

  /**
   * @param pidcVersion
   * @return
   */
  private Set<PidcRmDefinition> createDefaultRiskDef(final TPidcVersion pidcVersion) {
    Set<PidcRmDefinition> pidcRiskDefList = new HashSet<>();
    // Create a default PIDC Rm Definition
    PidcRmDefinition rmDef = new PidcRmDefinition();
    rmDef.setPidcVersId(pidcVersion.getPidcVersId());
    rmDef.setRmNameEng("PIDC");
    rmDef.setIsVariant("N");
    rmDef.setVersion(1L);
    pidcRiskDefList.add(rmDef);
    return pidcRiskDefList;
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TPidcVersion entity) {
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    entity.setTabvProjectidcard(pidcLoader.getEntityObject(getInputData().getPidcId()));
    Set<TPidcVersion> allVrsnSet = entity.getTabvProjectidcard().getTPidcVersions();
    Long maxRevId = 0L;
    if ((null != allVrsnSet) && !allVrsnSet.isEmpty()) {
      for (TPidcVersion vrsn : allVrsnSet) {
        if ((null != vrsn) && (vrsn.getProRevId() > maxRevId)) {
          maxRevId = vrsn.getProRevId();
        }
      }
    }
    entity.setProRevId(maxRevId + 1);

    entity.setVersName(getInputData().getVersionName());
    entity.setVersDescEng(getInputData().getVersDescEng());
    entity.setVersDescGer(getInputData().getVersDescGer());
    entity.setPidStatus(ApicConstants.PIDC_STATUS_ID_INWORK_STR);
    if (null != getInputData().getParentPidcVerId()) {
      PidcVersionLoader verLoader = new PidcVersionLoader(getServiceData());
      entity.setTPidcVers(verLoader.getEntityObject(getInputData().getParentPidcVerId()));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion dbVersToUpd = versLoader.getEntityObject(getInputData().getId());

    dbVersToUpd.setVersName(getInputData().getVersionName());
    dbVersToUpd.setVersDescEng(getInputData().getVersDescEng());
    dbVersToUpd.setVersDescGer(getInputData().getVersDescGer());
    // update the last confirmation date
    if (isObjectChanged(getInputData().getLastConfirmationDate(), getOldData().getLastConfirmationDate())) {
      try {
        dbVersToUpd.setLastConfirmationDate(string2timestamp(getInputData().getLastConfirmationDate()));
      }
      catch (ParseException exp) {
        throw new IcdmException(exp.getMessage(), exp);
      }
    }
    dbVersToUpd.setPidStatus(getInputData().getPidStatus());
    dbVersToUpd.setDeletedFlag(getInputData().isDeleted() ? "Y" : "N");
    dbVersToUpd.setProRevId(getInputData().getProRevId());
    setUserDetails(COMMAND_MODE.UPDATE, dbVersToUpd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion entity = versLoader.getEntityObject(getObjId());
    entity.getTabvProjectidcard().getTPidcVersions().add(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // Task 251264
    PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    if (null != getInputData().getId()) {
      PidcVersion pidcVersion = versionLoader.getDataObjectByID(getInputData().getId());

      return nodeAccessLoader.isCurrentUserOwner(pidcVersion.getPidcId());
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
